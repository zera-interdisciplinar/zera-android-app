# Central de Trabalho Implementation Plan

> **For agentic workers:** Use the existing `GetEmployeePendencies` / `GET /api/v1/dashboard/work-center`. Do not add a second HTTP client. Home and this screen share the payload; they differ only in mapping.

**Goal:** Popular `WorkingCentralScreen` com a lista **item a item** do work-center, e deixar explícito que a home só mostra o **resumo por bucket**.

**Architecture:** Uma rota, duas apresentações. `GetEmployeePendencies.execute()` já devolve `WorkCenterResponseDTO`. `EmployeeHomeViewModel.pendenciesFrom` agrega contagens; `WorkingCentralViewModel.priorityFrom` emite um `NotificationItem` por elemento de `drafts`, `rejected` e `damagedWithoutDestination`. `inMaintenance` alimenta só o `SummaryCard`. Sem lista de manutenção: o contrato não manda os itens, só o contador.

**Tech Stack:** Kotlin, Retrofit (já ligado), JUnit, Compose (`NotificationList`, `SummaryCard`).

## Global Constraints

- Mesma API da home: `GET /api/v1/dashboard/work-center`, sem query.
- Não usar `GET /items` nesta tela.
- Labels da Central (spec/mock): "Completar cadastro" (Yellow), "Recusado pelo gestor" (Red), "Enviar para manutenção" (Blue).
- `awaitingEvaluation` e a seção UI "Em manutenção" ficam fora (contador sem IDs).
- `onNotificationClick` continua sem navegação de resolução.

---

## Distinção home vs Central

| | Home — seção Pendências | Central de Trabalho |
|---|---|---|
| Rota de tela | `Route.EmployeeHome` | `Route.WorkingCentral` |
| Usecase | `GetEmployeePendencies` | o mesmo |
| O que o back filtra | Actor do token | Actor do token |
| O que o app filtra | nada; só omite bucket vazio | nada; um card por item |
| Card | `N cadastros incompletos` | `Completar cadastro` + nome do item |
| `inMaintenance` | não | `"M em manutenção"` no resumo |

O back **não** marca “vai para a home” vs “vai para a Central”. Os três arrays são a fila do operário. A home comprime; a Central detalha.

---

### Task 1: Mapping tests + ViewModel

**Files:**
- Create: `app/src/test/java/com/zera/android/viewmodel/employee/WorkingCentralMappingTest.kt`
- Modify: `app/src/main/java/com/zera/android/viewmodel/employee/WorkingCentral.kt`

**Interfaces:**
- Consumes: `WorkCenterResponseDTO` via `GetEmployeePendencies.execute()`
- Produces: `priorityFrom(workCenter): List<NotificationItem>`; `maintenanceCount` = `inMaintenance`

- [ ] **Step 1: Failing tests**

```kotlin
package com.zera.android.viewmodel.employee

import com.zera.android.model.dto.inventory.PendingItemResponseDTO
import com.zera.android.model.dto.inventory.WorkCenterResponseDTO
import com.zera.android.view.theme.ZeraColorFamily
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkingCentralMappingTest {
    @Test
    fun emptyWorkCenterProducesNoCards() {
        assertEquals(0, WorkingCentralViewModel.priorityFrom(WorkCenterResponseDTO()).size)
    }

    @Test
    fun mapsEachItemInBucketOrder() {
        val workCenter = WorkCenterResponseDTO(
            drafts = listOf(
                PendingItemResponseDTO(
                    id = "d1",
                    name = "Teclado mecânico",
                    missingFields = listOf("damages"),
                ),
            ),
            rejected = listOf(
                PendingItemResponseDTO(
                    id = "r1",
                    name = "Chip controlador",
                    rejectionReason = "Foto ilegível",
                ),
            ),
            damagedWithoutDestination = listOf(
                PendingItemResponseDTO(id = "x1", name = "Mouse sem fio"),
            ),
            inMaintenance = 25,
        )

        val cards = WorkingCentralViewModel.priorityFrom(workCenter)
        assertEquals(listOf("d1", "r1", "x1"), cards.map { it.id })
        assertEquals("Completar cadastro", cards[0].label)
        assertEquals("Teclado mecânico · faltam damages", cards[0].text)
        assertEquals(ZeraColorFamily.Yellow, cards[0].style)
        assertEquals("Recusado pelo gestor", cards[1].label)
        assertEquals("Chip controlador · Foto ilegível", cards[1].text)
        assertEquals(ZeraColorFamily.Red, cards[1].style)
        assertEquals("Enviar para manutenção", cards[2].label)
        assertEquals("Mouse sem fio · aguardando destino", cards[2].text)
        assertEquals(ZeraColorFamily.Blue, cards[2].style)
    }

    @Test
    fun draftWithoutMissingFieldsUsesNameOnly() {
        val workCenter = WorkCenterResponseDTO(
            drafts = listOf(PendingItemResponseDTO(id = "d1", name = "Monitor")),
        )
        val card = WorkingCentralViewModel.priorityFrom(workCenter).single()
        assertEquals("Monitor", card.text)
    }

    @Test
    fun shiftSummaryUsesPendingListAndMaintenanceCounter() {
        val state = WorkingCentralState(
            maintenanceCount = 2,
            priorityNotifications = WorkingCentralViewModel.priorityFrom(
                WorkCenterResponseDTO(
                    drafts = listOf(PendingItemResponseDTO(id = "d1", name = "A")),
                    rejected = listOf(PendingItemResponseDTO(id = "r1", name = "B")),
                ),
            ),
        )
        assertEquals(2, state.pendingCount)
        assertEquals("2 pendências · 2 em manutenção", state.shiftSummary)
    }
}
```

- [ ] **Step 2: Run tests — expect FAIL** (`priorityFrom` missing)

Run: `./gradlew :app:testDebugUnitTest --tests com.zera.android.viewmodel.employee.WorkingCentralMappingTest`

- [ ] **Step 3: Implement ViewModel**

```kotlin
class WorkingCentralViewModel : ZeraViewModel() {
    private val getEmployeePendencies = GetEmployeePendencies()
    private val _state = mutableStateOf(WorkingCentralState())
    val state = _state

    init {
        viewModelScope.launch { loadWorkCenter() }
    }

    private suspend fun loadWorkCenter() {
        try {
            val workCenter = getEmployeePendencies.execute()
            _state.value = WorkingCentralState(
                maintenanceCount = workCenter.inMaintenance.toInt(),
                priorityNotifications = priorityFrom(workCenter),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            _state.value = WorkingCentralState()
        }
    }

    companion object {
        internal fun priorityFrom(workCenter: WorkCenterResponseDTO): List<NotificationItem> {
            val drafts = workCenter.drafts.map { item ->
                NotificationItem(
                    id = item.id,
                    label = "Completar cadastro",
                    text = draftText(item),
                    style = ZeraColorFamily.Yellow,
                )
            }
            val rejected = workCenter.rejected.map { item ->
                NotificationItem(
                    id = item.id,
                    label = "Recusado pelo gestor",
                    text = rejectedText(item),
                    style = ZeraColorFamily.Red,
                )
            }
            val damaged = workCenter.damagedWithoutDestination.map { item ->
                NotificationItem(
                    id = item.id,
                    label = "Enviar para manutenção",
                    text = "${item.name} · aguardando destino",
                    style = ZeraColorFamily.Blue,
                )
            }
            return drafts + rejected + damaged
        }

        private fun draftText(item: PendingItemResponseDTO): String {
            if (item.missingFields.isEmpty()) return item.name
            return "${item.name} · faltam ${item.missingFields.joinToString(", ")}"
        }

        private fun rejectedText(item: PendingItemResponseDTO): String {
            val reason = item.rejectionReason
            if (reason.isNullOrBlank()) return item.name
            return "${item.name} · $reason"
        }
    }
}
```

Tela Compose não muda: já lê `state.shiftSummary` e `state.priorityNotifications`.

- [ ] **Step 4: Run tests — expect PASS**

---

### Task 2: Home aponta para a Central

**Files:**
- Modify: `app/src/main/java/com/zera/android/viewmodel/employee/EmployeeHome.kt`

```kotlin
fun onPendingClick() {
    ZeraNavigator.push(Route.WorkingCentral)
}
```

Qualquer card da seção Pendências abre a lista detalhada (mesmos dados, outro recorte visual).

---

### Task 3: Docs

- `docs/07-especificacoes/central-de-trabalho.md` — dados reais; mapping; manutenção ainda TODO
- `docs/05-regras-de-negocio/inventory-dashboard.md` — tabela home (agregado) vs Central (item)
- `docs/06-contratos-api.md` e `docs/contrato-dashboard/contexto.md` — work-center usado pelas duas telas
- `docs/07-especificacoes/employee-home.md` — clique em pendência → `Route.WorkingCentral`
