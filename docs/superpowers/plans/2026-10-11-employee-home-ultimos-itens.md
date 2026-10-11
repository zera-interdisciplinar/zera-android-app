# Employee Home Últimos Itens Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Preencher a seção "Últimos itens" da home do operário com `recentItems` de `GetEmployeeHome` (`GET /api/v1/dashboard/home`), no mesmo recorte da home do gestor, e ligar "Ver Todos" ao catálogo do operário.

**Architecture:** Não criar usecase nem rota HTTP nova. `GetEmployeeHome.execute()` já devolve `DashboardHomeResponseDTO` com `recentItems`. Hoje `EmployeeHomeViewModel.loadStockOccupation()` só copia ocupação e deixa `latestProducts` mockado no estado inicial. Passar a mapear `recentItems.content` → `List<ProductItem>` (`id` + `name`, sem tag de status). A tela já renderiza `ProductList` e já navega para `Route.ItemDetails(itemId)` no clique. "Ver Todos" abre `Route.EmployeeItems` (`GET /api/v1/items`), não pagina `recentItems`.

**Tech Stack:** Kotlin, Retrofit (`InventoryService.getHome`, defaults `page=0`, `size=5`), JUnit, Compose (`ProductList` já na `EmployeeHomeScreen`).

## Global Constraints

- Fonte da lista da home: `recentItems.content` de `GET /api/v1/dashboard/home` via `GetEmployeeHome`. Não chamar `GET /api/v1/items` nem `GET /api/v1/dashboard/work-center` para esta seção.
- Página padrão: `page=0`, `size=5` (defaults de `InventoryService.getHome`). Sem paginação na home.
- `ProductItem` da home: só `id` e `name` (igual `ManagerHomeViewModel`). Sem `statusText` — o catálogo do operário (`EmployeeItemsScreen`) também omite a tag; a home do gestor também não usa status em `recentItems`.
- Lista vazia: empty state padrão do `ProductList` ("Nenhum produto encontrado").
- Falha de `GetEmployeeHome`: ocupação em zero **e** `latestProducts` vazio (sem mock residual).
- Clique no item: já existe na tela (`Route.ItemDetails(itemId)`). Não mudar esse caminho.
- Fora desta feature: atalhos mock "Manutenção"/"Descartes", "Escanear", "Adicionar Modelo", FAB do chatbot.

---

## File Structure

| Arquivo | Papel |
|---|---|
| `app/src/test/java/com/zera/android/viewmodel/employee/EmployeeHomeMappingTest.kt` | Testes de `latestProductsFrom` |
| `app/src/main/java/com/zera/android/viewmodel/employee/EmployeeHome.kt` | Estado inicial vazio; mapear `recentItems` em `loadStockOccupation`; `onSeeAllItemsClick` → `Route.EmployeeItems` |
| `app/src/main/java/com/zera/android/view/screens/employee/EmployeeHomeScreen.kt` | Sem mudança de layout; já lê `state.latestProducts` |
| Specs/regras/contratos abaixo | Documentar que a home do operário usa `recentItems` de verdade |

---

### Task 1: Mapping + ViewModel + Ver Todos

**Files:**
- Modify: `app/src/test/java/com/zera/android/viewmodel/employee/EmployeeHomeMappingTest.kt`
- Modify: `app/src/main/java/com/zera/android/viewmodel/employee/EmployeeHome.kt`

**Interfaces:**
- Consumes: `DashboardHomeResponseDTO` (`GetEmployeeHome.execute()`), campo `recentItems: PagedItemsDTO` (`content: List<ItemResponseDTO>`)
- Produces: `EmployeeHomeViewModel.latestProductsFrom(home: DashboardHomeResponseDTO): List<ProductItem>` — cada elemento `ProductItem(id = item.id, name = item.name)`; `EmployeeHomeState.latestProducts`; `onSeeAllItemsClick()` → `ZeraNavigator.push(Route.EmployeeItems)`

- [ ] **Step 1: Write the failing tests**

Append to `EmployeeHomeMappingTest.kt` (keep the existing occupation/pendencies tests):

```kotlin
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.PagedItemsDTO
import com.zera.android.view.components.lists.ProductItem

    @Test
    fun latestProductsMapsIdAndNameOnly() {
        val home = DashboardHomeResponseDTO(
            activeItems = 128,
            recentItems = PagedItemsDTO(
                content = listOf(
                    ItemResponseDTO(
                        id = "9c858901-8a57-4791-81fe-4c455b099bc9",
                        name = "Notebook Dell Latitude",
                        status = "IN_STOCK",
                    ),
                    ItemResponseDTO(
                        id = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
                        name = "Mouse sem fio",
                    ),
                ),
            ),
        )

        val products = EmployeeHomeViewModel.latestProductsFrom(home)
        assertEquals(
            listOf(
                ProductItem(
                    id = "9c858901-8a57-4791-81fe-4c455b099bc9",
                    name = "Notebook Dell Latitude",
                ),
                ProductItem(
                    id = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
                    name = "Mouse sem fio",
                ),
            ),
            products,
        )
        assertEquals(listOf(null, null), products.map { it.statusText })
    }

    @Test
    fun latestProductsEmptyWhenContentIsEmpty() {
        val home = DashboardHomeResponseDTO(
            activeItems = 0,
            recentItems = PagedItemsDTO(),
        )
        assertEquals(emptyList<ProductItem>(), EmployeeHomeViewModel.latestProductsFrom(home))
    }
```

- [ ] **Step 2: Run tests — expect FAIL** (`latestProductsFrom` missing)

Run: `./gradlew :app:testDebugUnitTest --tests com.zera.android.viewmodel.employee.EmployeeHomeMappingTest`

Expected: FAIL compiling or "Unresolved reference: latestProductsFrom".

- [ ] **Step 3: Write minimal implementation**

In `EmployeeHome.kt`:

1. Remove the mock from the initial state. Replace the `_state` initializer with:

```kotlin
    private val _state = mutableStateOf(
        EmployeeHomeState(
            maintenanceCount = "25",
            discardCount = "8",
        )
    )
```

(`maintenanceCount` / `discardCount` continuam mock — fora desta feature.)

2. Replace the class KDoc with:

```kotlin
/**
 * ViewModel da home do operário.
 *
 * Nome, cargo, ocupação, pendências e últimos itens vêm da API;
 * atalhos de manutenção/descarte ainda são mock.
 */
```

3. In `loadStockOccupation()`, copy occupancy **and** latest products:

```kotlin
    private suspend fun loadStockOccupation() {
        try {
            val home = getEmployeeHome.execute()
            _state.value = _state.value.copy(
                stockItemCount = home.activeItems.toInt(),
                stockOccupation = occupationFrom(home),
                latestProducts = latestProductsFrom(home),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // O card fica em zero e a lista cai no empty state; o restante da home continua visível.
        }
    }
```

4. Implement `onSeeAllItemsClick`:

```kotlin
    fun onSeeAllItemsClick() {
        ZeraNavigator.push(Route.EmployeeItems)
    }
```

5. Add mapper in the `companion object` (next to `occupationFrom`):

```kotlin
        internal fun latestProductsFrom(home: DashboardHomeResponseDTO): List<ProductItem> =
            home.recentItems.content.map { item ->
                ProductItem(id = item.id, name = item.name)
            }
```

Do **not** change `EmployeeHomeScreen.kt`: `ProductList(products = state.latestProducts, onItemClick = { ZeraNavigator.push(Route.ItemDetails(itemId = product.id)) })` already matches the spec.

- [ ] **Step 4: Run tests — expect PASS**

Run: `./gradlew :app:testDebugUnitTest --tests com.zera.android.viewmodel.employee.EmployeeHomeMappingTest`

Expected: PASS (todos os testes da classe, inclusive ocupação/pendências).

- [ ] **Step 5: Commit**

```bash
git add app/src/test/java/com/zera/android/viewmodel/employee/EmployeeHomeMappingTest.kt \
        app/src/main/java/com/zera/android/viewmodel/employee/EmployeeHome.kt
git commit -m "$(cat <<'EOF'
feat: show dashboard recent items on employee home

EOF
)"
```

---

### Task 2: Docs

**Files:**
- Modify: `docs/07-especificacoes/employee-home.md`
- Modify: `docs/05-regras-de-negocio/inventory-dashboard.md`
- Modify: `docs/contrato-dashboard/contexto.md`
- Modify: `docs/06-contratos-api.md`

**Interfaces:**
- Consumes: comportamento da Task 1
- Produces: specs alinhadas (últimos itens reais; "Ver Todos" → `Route.EmployeeItems`)

- [ ] **Step 1: Update `docs/07-especificacoes/employee-home.md`**

Metadados:

```markdown
- **Status:** em progresso (pendências e últimos itens reais; atalhos mock; navegação parcial)
- **Última atualização:** 2026-10-11
```

Contexto — substituir o parágrafo atual por:

```markdown
Primeira tela do Operário após o login (`Route.EmployeeHome`). Reaproveita os componentes da home do Gestor ([manager-home-dashboard.md](manager-home-dashboard.md)) dentro do `EmployeeScaffold`, com textos próprios do fluxo de Operário. Nome e cargo vêm de `GetSelfUser`; ocupação e últimos itens vêm de `GetEmployeeHome` (`GET /api/v1/dashboard/home`, `recentItems.content`); pendências de `GetEmployeePendencies` (`GET /api/v1/dashboard/work-center`). Atalhos de manutenção/descarte ainda são mock.
```

Aceite — marcar últimos itens e "Ver Todos", deixar o resto:

```markdown
- [x] Layout com os mesmos componentes da home do Gestor (`StockOccupationCard`, `ShortcutCard`, `NotificationList`, `ProductList`)
- [x] Pendências mapeadas de `drafts` / `rejected` / `damagedWithoutDestination` (contador > 0 vira um `NotificationItem`; drafts e rejected em Red, danificados em Yellow)
- [x] Dados reais de ocupação, pendências e últimos itens (`recentItems.content`)
- [ ] Dados reais de manutenção e descartes (atalhos ainda mock)
- [x] Clique em pendência abre `Route.WorkingCentral`
- [x] Clique em um último item abre `Route.ItemDetails(itemId)`
- [x] "Ver Todos" abre `Route.EmployeeItems`
- [ ] Navegação de "Escanear", "Adicionar Modelo", "Manutenção" e "Descartes" (hoje `TODO` no `EmployeeHomeViewModel`)
```

Edge cases — acrescentar:

```markdown
- Lista de últimos itens vazia (`recentItems.content` vazio ou falha de `GetEmployeeHome`): `ProductList` mostra "Nenhum produto encontrado".
```

Referências — o contrato da home também entra:

```markdown
- Contrato: [../contrato-dashboard/contrato-inventory-dashboard.md](../contrato-dashboard/contrato-inventory-dashboard.md) (`GET /dashboard/home` e `GET /dashboard/work-center`)
```

- [ ] **Step 2: Update `docs/05-regras-de-negocio/inventory-dashboard.md`**

Replace the "Últimos itens" section with:

```markdown
### Últimos itens

Vêm de `recentItems.content` (`id` + `name`) em `GET /api/v1/dashboard/home`. A home do Gestor (`GetManagerHome`) e a home do Operário (`GetEmployeeHome`) usam o mesmo campo; não há mock nessa lista. Página padrão: `page=0`, `size=5` (defaults do `InventoryService`). Sem tag de status. Lista vazia usa o empty state do `ProductList`. No Operário, "Ver Todos" abre `Route.EmployeeItems` (catálogo `GET /api/v1/items`); no Gestor, abre `Route.Itens`.
```

In "Relacionamento com perfis", replace the Operário sentence with:

```markdown
O Gestor vê `Route.ManagerHome` e `Route.Indexes`. O Operário vê ocupação, últimos itens e pendências em `Route.EmployeeHome`. O interceptor envia o token do usuário autenticado; a autorização de quem pode consultar o dashboard é do backend.
```

- [ ] **Step 3: Update contract context notes**

In `docs/contrato-dashboard/contexto.md`:

```
    ├── GetEmployeeHome.kt                 ocupação e últimos itens da home do operário
```

In `docs/06-contratos-api.md`, keep the existing `GetEmployeeHome` row; no new endpoint. Confirm the sentence still reads that `GET /api/v1/dashboard/home` is used by both homes.

- [ ] **Step 4: Commit**

```bash
git add docs/07-especificacoes/employee-home.md \
        docs/05-regras-de-negocio/inventory-dashboard.md \
        docs/contrato-dashboard/contexto.md \
        docs/06-contratos-api.md
git commit -m "$(cat <<'EOF'
docs: employee home recent items from dashboard home

EOF
)"
```

---

## Self-Review

**1. Spec coverage**

| Requisito (`employee-home.md` / regras / contrato) | Task |
|---|---|
| `recentItems.content` na home do operário | Task 1 |
| Empty state da lista | Task 1 (lista vazia + falha) + UI já existente |
| Clique → `Route.ItemDetails` | Já na tela; Task 1 não altera |
| "Ver Todos" → catálogo do operário | Task 1 (`Route.EmployeeItems`) |
| Não usar `GET /items` na home | Global Constraints |
| Docs | Task 2 |
| Manutenção/descartes/scan/modelo | Explicitamente fora |

**2. Placeholder scan:** nenhum TBD / "similar to Task N" / teste vago.

**3. Type consistency:** `latestProductsFrom(DashboardHomeResponseDTO): List<ProductItem>` usado só na Task 1; `ProductItem(id, name)` igual ao gestor; `Route.EmployeeItems` já existe no `ZeraNavHost`.
