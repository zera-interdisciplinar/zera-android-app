# Spec — Itens do modelo

## Metadados

- **Feature:** Lista de itens de um modelo (`ModelItemsScreen`)
- **Perfil(is) envolvido(s):** Gestor e Operário (a visão do Operário depende de `isEmployee`)
- **Status:** em progresso (UI das duas visões pronta; dados, busca, filtro e paginação pendentes; sem tela que navegue para ela)
- **Última atualização:** 2026-10-09

## Contexto

`Route.ModelItems(modelId, modelName)` lista os itens de um modelo. A tela tem duas visões sobre o mesmo conteúdo (`ModelItemsContent`: busca, filtro de status, chips, contagem e `ProductList`), escolhidas por `ModelItemsState.isEmployee`:

- **Gestor** (`isEmployee = false`, padrão): `ManagerScaffold`, `currentRoute = Route.Itens`.
- **Operário** (`isEmployee = true`): `EmployeeScaffold`, `currentRoute = Route.EmployeeItems`, e o botão "Adicionar novo item" no topo, que abre `Route.ManualRegister(modelName)`.

`isEmployee` nunca é preenchido hoje: o `TODO` em `ModelItemsState` aguarda a validação do cargo do usuário, que será feita por outra pessoa. Também não há tela que navegue para `Route.ModelItems`: o toque em um modelo em `ModelsViewModel` ainda é `TODO`.

## User story

Como Gestor ou Operário, quero ver os itens de um modelo, para acompanhar o que já foi cadastrado dele e, como Operário, cadastrar um novo item já com esse modelo.

## Cenários (Given/When/Then)

### Cenário: Visão do Gestor

- **Given** `isEmployee` é `false`
- **When** a tela é composta
- **Then** usa `ManagerScaffold` e não mostra "Adicionar novo item"

### Cenário: Visão do Operário

- **Given** `isEmployee` é `true`
- **When** a tela é composta
- **Then** usa `EmployeeScaffold` e mostra "Adicionar novo item"

### Cenário: Adicionar item a partir do modelo

- **Given** o Operário está na lista de itens de um modelo
- **When** toca em "Adicionar novo item"
- **Then** navega para `Route.ManualRegister(modelName = <modelo>)`, com o modelo já preenchido (ver [cadastro-manual-item.md](cadastro-manual-item.md))

### Cenário: Abrir um item

- **Given** a lista tem itens
- **When** toca em um item
- **Then** navega para `Route.ItemDetails(itemId)`

### Cenário: Filtrar por status

- **Given** a tela está aberta
- **When** o usuário aplica filtros de status no `BottomSheet`
- **Then** `selectedStatuses` é atualizado e os filtros aparecem como chips removíveis; a lista ainda não é recarregada

## Critérios de aceite

- [x] Mesmo conteúdo nas duas visões (`ModelItemsContent`), mudando só a casca e o botão de adicionar
- [x] "Adicionar novo item" (Operário) abre o cadastro manual com o modelo preenchido
- [ ] Preencher `isEmployee` com o cargo do usuário
- [ ] Conectar ao back: falta aceitar `modelId` em `ItemsQuery`/`GetItems` e no `InventoryService` (verificar se a API suporta o filtro) e implementar o carregamento paginado
- [ ] Busca (`onSearch`), filtro (`applyFilters`) e paginação (`loadNextPage`) recarregarem a lista
- [ ] Navegar para `Route.ModelItems` a partir da lista de modelos

## Edge cases considerados

- `errorMessage` aparece em texto de erro acima da contagem.
- O `ViewModel` tem `key` por modelo (`model-items-$modelId`) para não misturar estado entre modelos.

## Fora de escopo desta spec

- Contrato de listagem de itens por modelo.
- Cargo do usuário e controle de permissão por perfil.

## Referências

- Specs relacionadas: [modelos.md](modelos.md), [itens.md](itens.md), [employee-items.md](employee-items.md), [cadastro-manual-item.md](cadastro-manual-item.md)
- Componentes: [../03-catalogo-componentes.md](../03-catalogo-componentes.md) (`ManagerScaffold`, `EmployeeScaffold`, `ProductList`)
