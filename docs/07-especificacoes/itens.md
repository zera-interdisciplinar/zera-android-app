# Spec — Listagem de Itens do Gestor

## Metadados

- **Feature:** Catálogo de itens (`ItensScreen`)
- **Perfil(is) envolvido(s):** Gestor
- **Status:** implementado (leitura; aprovação/edição fora desta tela)
- **Última atualização:** 2026-09-26

## Contexto

A tela `Route.Itens` era mockada. Precisava listar o inventário da unidade com busca, chips e paginação. `recentItems` do dashboard não serve: é só “últimos cadastrados”. A fonte correta é `GET /api/v1/items`.

## User story

Como Gestor, quero buscar e filtrar os itens da minha unidade, para abrir o detalhe de qualquer um sem depender de mocks.

## Cenários (Given/When/Then)

### Cenário: Carga inicial

- **Given** o Gestor autenticado abre `Route.Itens`
- **When** `GetItems.execute(page=0, size=20)` responde 200
- **Then** o título usa `totalElements`, a lista mostra `id`/`name`/chip de status, e o chip padrão é “Todos”

### Cenário: Busca

- **Given** a tela carregou
- **When** o Gestor confirma a busca (não a cada tecla)
- **Then** a query envia `q` trimado; limpar o campo dispara de novo sem `q`

### Cenário: Chip Pendentes

- **Given** a API só aceita um `status` por chamada
- **When** o Gestor escolhe “Pendentes”
- **Then** o app chama `PENDING_APPROVAL` e `AWAITING_EVALUATION` e mescla por `id`

### Cenário: Chip de categoria

- **Given** `GetCategories` devolve categorias da unidade
- **When** o Gestor escolhe o nome de uma categoria
- **Then** a listagem manda `categoryId` igual ao `id` dessa categoria

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
- [x] Chips Todos / Pendentes / categorias reais
- [x] Busca via `q` no submit
- [x] Paginação `size=20` com `onEndReached`
- [x] Navegação pelo UUID
- [x] Erro de rede visível
- [ ] Busca pelo UUID interno `id` (a API cobre `displayCode` e nomes)
- [ ] Filtro “Pendentes” numa única request (`statusIn` não existe)

## Edge cases considerados

- Categorias vazias ou request falho: chips voltam a “Todos / Pendentes / Categoria”.
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
