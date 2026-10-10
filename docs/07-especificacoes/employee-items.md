# Spec — Itens do Operário

## Metadados

- **Feature:** Lista de itens do Operário (`EmployeeItemsScreen`)
- **Perfil(is) envolvido(s):** Operário (funcionário)
- **Status:** em progresso (lista, busca e filtros prontos; cadastro de item pendente)
- **Última atualização:** 2026-10-09

## Contexto

`Route.EmployeeItems` é aberta pelo atalho "Itens" da `EmployeeBottomNavBar` e pelo item "Itens" da sidebar do Operário. Tem a mesma estrutura da `ItensScreen` do Gestor ([itens.md](itens.md)) e reaproveita o `ItensViewModel` (busca, filtros, contagem e lista paginada). Diferenças: tem o botão "Adicionar novo item" e os itens da lista não exibem a tag de status (`statusText = null`), então mostram a seta.

## User story

Como Operário, quero consultar e buscar os itens do estoque, para encontrar um item ou iniciar o cadastro de um novo.

## Cenários (Given/When/Then)

### Cenário: Buscar e filtrar

- **Given** o Operário está na lista de itens
- **When** digita na busca ou aplica filtros de status/categoria no `BottomSheet`
- **Then** o `ItensViewModel` recarrega a lista, e os filtros aplicados aparecem como chips removíveis

### Cenário: Abrir um item

- **Given** a lista tem itens
- **When** o Operário toca em um item
- **Then** navega para `Route.ItemDetails(itemId)`

### Cenário: Paginação

- **Given** a lista chegou ao fim da página carregada
- **When** o fim da lista é alcançado
- **Then** `loadNextPage` carrega a próxima página (`isLoadingMore` indica o carregamento)

### Cenário: Adicionar novo item

- **Given** o Operário está na lista de itens
- **When** toca em "Adicionar novo item"
- **Then** ainda não acontece nada (ação vazia com `TODO`)

## Critérios de aceite

- [x] Mesma busca, filtros e paginação da `ItensScreen`, reaproveitando `ItensViewModel`
- [x] Itens sem tag de status
- [x] Item leva a `Route.ItemDetails`
- [ ] "Adicionar novo item" abrir o cadastro de item do Operário (candidato: `Route.ManualRegister`, ver [cadastro-manual-item.md](cadastro-manual-item.md))

## Edge cases considerados

- Falha ao carregar: `errorMessage` aparece em texto de erro acima da contagem.

## Fora de escopo desta spec

- Regras de listagem, filtros e contrato de API (ver [itens.md](itens.md)).

## Referências

- Componentes: [../03-catalogo-componentes.md](../03-catalogo-componentes.md) (`EmployeeScaffold`, `ProductList`, `ZeraChipsGroup`)
- Specs relacionadas: [itens.md](itens.md), [item-details.md](item-details.md), [cadastro-manual-item.md](cadastro-manual-item.md)
