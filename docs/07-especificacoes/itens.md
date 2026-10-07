# Spec — Listagem de Itens do Gestor

## Metadados

- **Feature:** Catálogo de itens (`ItensScreen`)
- **Perfil(is) envolvido(s):** Gestor
- **Status:** implementado (listagem, busca e filtros; aprovação/edição fora desta tela)
- **Última atualização:** 2026-10-03

## Contexto

A tela `Route.Itens` precisava listar o inventário da unidade com busca, filtros e paginação. `recentItems` do dashboard não serve: é só “últimos cadastrados”. A fonte correta é `GET /api/v1/items`.

## User story

Como Gestor, quero buscar e filtrar os itens da minha unidade, para abrir o detalhe de qualquer um sem depender de mocks.

## Cenários (Given/When/Then)

### Cenário: Carga inicial

- **Given** o Gestor autenticado abre `Route.Itens`
- **When** `GetItems.execute(page=0, size=20)` responde 200
- **Then** o título usa `totalElements`, a lista mostra `id`/`name`/chip de status, e nenhum chip de filtro fica aplicado por padrão

### Cenário: Busca

- **Given** a tela carregou
- **When** o Gestor confirma a busca (não a cada tecla)
- **Then** a query envia `q` trimado; limpar o campo dispara de novo sem `q`

### Cenário: Abrir e editar filtros

- **Given** o Gestor está no catálogo
- **When** toca no botão de filtro
- **Then** abre um BottomSheet com Status e Categoria em grupos de seleção múltipla

### Cenário: Aplicar múltiplos filtros

- **Given** há status e/ou categorias selecionadas no BottomSheet
- **When** o Gestor toca em “Aplicar filtros”
- **Then** o app faz OR entre valores do mesmo grupo, AND entre Status e Categoria, e mostra abaixo da busca apenas os chips aplicados

### Cenário: Remover filtro aplicado

- **Given** um ou mais filtros estão aplicados
- **When** o Gestor toca em um chip abaixo da busca
- **Then** remove somente aquele valor e recarrega a lista

### Cenário: Fechar sem aplicar

- **Given** o Gestor alterou filtros no BottomSheet
- **When** fecha o BottomSheet sem tocar em “Aplicar filtros”
- **Then** as alterações em rascunho são descartadas

### Cenário: Paginação

- **Given** `loadedPage + 1 < totalPages`
- **When** a lista chega perto do fim
- **Then** `loadNextPage` anexa itens sem duplicar `id`

### Cenário: Abrir detalhe

- **Given** há itens na lista
- **When** o Gestor toca um item
- **Then** navega para `Route.ItemDetails(itemId)` com o UUID

### Cenário: Falha da listagem

- **Given** `GetItems` falha
- **When** a tela atualiza
- **Then** `errorMessage` aparece acima do título; falha de categorias não derruba a lista

## Critérios de aceite

- [x] Sem mock na lista; dados de `GetItems`
- [x] Filtros multi-seleção de Status e Categoria no BottomSheet
- [x] Chips abaixo da busca mostram apenas os filtros aplicados e permitem removê-los
- [x] Busca via `q` no submit
- [x] Paginação `size=20` com `onEndReached`
- [x] Navegação pelo UUID
- [x] Erro de rede visível
- [ ] Busca pelo UUID interno `id` (a API cobre `displayCode` e nomes)
- [x] Combinação de vários status/categorias por chamadas compatíveis com a API atual e mesclagem por `id`

## Edge cases considerados

- Se a carga de categorias falhar, o grupo Categoria fica sem opções; a listagem sem esse filtro continua disponível.
- `DRAFT` e `REMOVED` não são opções de status porque não têm representação em `ItemStatus`.
- Status `DRAFT`/`REMOVED`: linha sem chip (`fromBackend` devolve null).
- Job anterior cancelado ao mudar filtro/busca (`CancellationException` não vira erro de UI).

## Fora de escopo desta spec

- `GET /api/v1/dashboard/home` como fonte da lista.
- Aprovar/editar/rejeitar item (tela de detalhe).
- Skeleton de `isLoading` na lista (o campo existe no state; a tela não desenha skeleton).

## Referências

- Regras: [../05-regras-de-negocio/itens.md](../05-regras-de-negocio/itens.md)
- Contratos: [../contrato-pdi/contrato-detalhes-itens.md](../contrato-pdi/contrato-detalhes-itens.md), [../contrato-pdi/contexto.md](../contrato-pdi/contexto.md)
- Spec de detalhe: [item-details.md](item-details.md)
