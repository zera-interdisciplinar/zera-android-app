# Employee Home Pendências Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Popular a seção Pendências da home do operário com `GET /api/v1/dashboard/work-center`, via usecase novo e o `EmployeeHomeViewModel` existente.

**Architecture:** O `InventoryClient` já manda Bearer e `X-Unit-Id`. O work-center recorta pelo `Actor` do token (rascunhos e reprovados próprios). O ViewModel mapeia buckets com tamanho > 0 para `NotificationItem`; falha da chamada não derruba o restante da home.

**Tech Stack:** Kotlin, Retrofit, kotlinx.serialization, JUnit, Compose (`NotificationList`).

## Global Constraints

- Fonte: `GET /api/v1/dashboard/work-center` — não `GET /items` e não `GET /dashboard/home`.
- Sem query params; paginação não existe nessa rota.
- Headers já no interceptor: `Authorization`, `apiKey`, `X-Unit-Id`.
- Labels em pt-BR, singular/plural; ordem dos cards: drafts, rejected, damagedWithoutDestination.
- Navegação (`onPendingClick`) permanece TODO.
- `inMaintenance` / `awaitingEvaluation` do work-center não alimentam os atalhos nesta entrega.

---

### Task 1: DTO + Retrofit + teste JSON

**Files:**
- Create: `app/src/main/java/com/zera/android/model/dto/inventory/WorkCenterResponseDTO.kt`
- Create: `app/src/test/java/com/zera/android/model/dto/inventory/WorkCenterResponseDTOTest.kt`
- Modify: `app/src/main/java/com/zera/android/model/remote/service/InventoryService.kt`

**Interfaces:**
- Consumes: JSON do contrato dashboard (`drafts`, `rejected`, `damagedWithoutDestination`, `inMaintenance`, `awaitingEvaluation`)
- Produces: `WorkCenterResponseDTO`, `PendingItemResponseDTO`, `InventoryService.getWorkCenter()`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.zera.android.model.dto.inventory

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WorkCenterResponseDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesContractSample() {
        val payload = """
            {
              "drafts": [{
                "id": "aa11bb22-0000-0000-0000-000000000001",
                "displayCode": "ITM-0099",
                "name": "Monitor LG 24pol",
                "modelName": "24ML600",
                "missingFields": ["serialNumber", "photoUrl"],
                "rejectionReason": null,
                "updatedAt": "2026-09-22T11:30:00"
              }],
              "rejected": [{
                "id": "aa11bb22-0000-0000-0000-000000000002",
                "displayCode": "ITM-0088",
                "name": "Teclado Logitech",
                "modelName": "K120",
                "missingFields": [],
                "rejectionReason": "Foto ilegível, reenviar",
                "updatedAt": "2026-09-21T09:00:00"
              }],
              "damagedWithoutDestination": [{
                "id": "aa11bb22-0000-0000-0000-000000000003",
                "displayCode": "ITM-0077",
                "name": "Mouse Dell",
                "modelName": "MS116",
                "missingFields": [],
                "rejectionReason": null,
                "updatedAt": "2026-09-19T14:00:00"
              }],
              "inMaintenance": 2,
              "awaitingEvaluation": 1
            }
        """.trimIndent()

        val workCenter = json.decodeFromString<WorkCenterResponseDTO>(payload)

        assertEquals(1, workCenter.drafts.size)
        assertEquals("aa11bb22-0000-0000-0000-000000000001", workCenter.drafts.first().id)
        assertEquals(listOf("serialNumber", "photoUrl"), workCenter.drafts.first().missingFields)
        assertEquals("Foto ilegível, reenviar", workCenter.rejected.first().rejectionReason)
        assertEquals("Mouse Dell", workCenter.damagedWithoutDestination.first().name)
        assertEquals(2L, workCenter.inMaintenance)
        assertEquals(1L, workCenter.awaitingEvaluation)
    }

    @Test
    fun parsesEmptyBuckets() {
        val payload = """
            {
              "drafts": [],
              "rejected": [],
              "damagedWithoutDestination": [],
              "inMaintenance": 0,
              "awaitingEvaluation": 0
            }
        """.trimIndent()

        val workCenter = json.decodeFromString<WorkCenterResponseDTO>(payload)

        assertEquals(0, workCenter.drafts.size)
        assertEquals(0, workCenter.rejected.size)
        assertEquals(0, workCenter.damagedWithoutDestination.size)
        assertNull(workCenter.drafts.firstOrNull()?.rejectionReason)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests com.zera.android.model.dto.inventory.WorkCenterResponseDTOTest`

Expected: FAIL (class not found)

- [ ] **Step 3: Write DTO and Retrofit method**

```kotlin
package com.zera.android.model.dto.inventory

import kotlinx.serialization.Serializable

@Serializable
data class PendingItemResponseDTO(
    val id: String,
    val displayCode: String? = null,
    val name: String,
    val modelName: String? = null,
    val missingFields: List<String> = emptyList(),
    val rejectionReason: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class WorkCenterResponseDTO(
    val drafts: List<PendingItemResponseDTO> = emptyList(),
    val rejected: List<PendingItemResponseDTO> = emptyList(),
    val damagedWithoutDestination: List<PendingItemResponseDTO> = emptyList(),
    val inMaintenance: Long = 0,
    val awaitingEvaluation: Long = 0,
)
```

No `InventoryService`:

```kotlin
@GET("api/v1/dashboard/work-center")
suspend fun getWorkCenter(): WorkCenterResponseDTO
```

- [ ] **Step 4: Run tests**

Run: `./gradlew :app:testDebugUnitTest --tests com.zera.android.model.dto.inventory.WorkCenterResponseDTOTest`

Expected: PASS

- [ ] **Step 5: Commit** (só se o humano pedir)

---

### Task 2: Usecase + mapping no ViewModel

**Files:**
- Create: `app/src/main/java/com/zera/android/model/usecase/inventory/GetEmployeePendencies.kt`
- Modify: `app/src/main/java/com/zera/android/viewmodel/employee/EmployeeHome.kt`
- Modify: `app/src/test/java/com/zera/android/viewmodel/employee/EmployeeHomeMappingTest.kt`

**Interfaces:**
- Consumes: `WorkCenterResponseDTO`
- Produces: `GetEmployeePendencies.execute(): WorkCenterResponseDTO`; `EmployeeHomeViewModel.pendenciesFrom(workCenter): List<NotificationItem>`

- [ ] **Step 1: Failing mapping tests**

```kotlin
@Test
fun pendenciesSkipEmptyBuckets() {
    val workCenter = WorkCenterResponseDTO()
    assertEquals(emptyList<NotificationItem>(), EmployeeHomeViewModel.pendenciesFrom(workCenter))
}

@Test
fun pendenciesUseSingularAndPluralLabels() {
    val oneDraft = WorkCenterResponseDTO(
        drafts = listOf(PendingItemResponseDTO(id = "d1", name = "A")),
    )
    val many = WorkCenterResponseDTO(
        drafts = listOf(
            PendingItemResponseDTO(id = "d1", name = "A"),
            PendingItemResponseDTO(id = "d2", name = "B"),
            PendingItemResponseDTO(id = "d3", name = "C"),
        ),
        rejected = listOf(PendingItemResponseDTO(id = "r1", name = "R")),
        damagedWithoutDestination = listOf(
            PendingItemResponseDTO(id = "x1", name = "X"),
            PendingItemResponseDTO(id = "x2", name = "Y"),
        ),
    )

    val one = EmployeeHomeViewModel.pendenciesFrom(oneDraft)
    assertEquals("incomplete-registrations", one.single().id)
    assertEquals("1 cadastro incompleto", one.single().label)
    assertEquals(ZeraColorFamily.Red, one.single().style)

    val cards = EmployeeHomeViewModel.pendenciesFrom(many)
    assertEquals(
        listOf("incomplete-registrations", "rejected-registrations", "damaged-without-destination"),
        cards.map { it.id },
    )
    assertEquals("3 cadastros incompletos", cards[0].label)
    assertEquals("1 cadastro recusado", cards[1].label)
    assertEquals("2 itens danificados sem destino", cards[2].label)
    assertEquals(ZeraColorFamily.Red, cards[1].style)
    assertEquals(ZeraColorFamily.Yellow, cards[2].style)
}
```

- [ ] **Step 2: Usecase**

```kotlin
class GetEmployeePendencies {
    suspend fun execute(): WorkCenterResponseDTO {
        return InventoryClient.inventoryService.getWorkCenter()
    }
}
```

- [ ] **Step 3: ViewModel**

`pendenciesFrom` no companion; `loadPendencies()` em `init` com catch silencioso; estado inicial **sem** mock de pendências (lista vazia até a API).

- [ ] **Step 4: Run tests**

Run: `./gradlew :app:testDebugUnitTest --tests com.zera.android.viewmodel.employee.EmployeeHomeMappingTest --tests com.zera.android.model.dto.inventory.WorkCenterResponseDTOTest`

Expected: PASS

---

### Task 3: Docs

Atualizar:

- `docs/07-especificacoes/employee-home.md` — pendências reais via work-center
- `docs/06-contratos-api.md` — linha do `getWorkCenter`
- `docs/05-regras-de-negocio/inventory-dashboard.md` — seção pendências do operário
- `docs/04-modelo-de-dados.md` — `WorkCenterResponseDTO`
- `docs/contrato-dashboard/contexto.md` — rota agora usada pela home do operário
