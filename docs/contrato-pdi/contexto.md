# contexto

As telas de PDI (página de detalhes do item) e a listagem de itens do gestor deixaram de usar dados mockados. O contrato fechado está em [contrato-detalhes-itens.md](contrato-detalhes-itens.md). Este arquivo descreve **o que o app já implementa** e **por que** cada escolha existe.

## Integração no app

O client continua o mesmo do dashboard (`InventoryClient` / `InventoryService`). Não foi criado um Retrofit novo: itens e categorias são do mesmo `ms-inventory`, com os mesmos headers (`apiKey`, `Authorization`, `X-Unit-Id`).

```
model
├── dto/inventory/
│   ├── ItemResponseDTO.kt          item (lista e detalhe); model/materials/category encaixados
│   ├── CategoryResponseDTO.kt      chip de categoria
│   └── DashboardHomeResponseDTO.kt PagedItemsDTO (envelope da lista)
├── remote/service/InventoryService.kt
│   GET api/v1/items, api/v1/items/{id}, api/v1/categories
└── usecase/inventory/
    ├── GetItems.kt                 catálogo paginado
    ├── GetItemDetails.kt           um item por UUID
    └── GetCategories.kt            nomes/ids para o filtro
```

ViewModels:

- `ItensViewModel` — `ItensScreen` (`Route.Itens`)
- `ItemDetailsViewModel` — `ItemDetailsScreen` (`Route.ItemDetails(itemId)`)

A lista **não** reaproveita `GET /api/v1/dashboard/home.recentItems`: aquele payload é “últimos cadastrados”, sem busca/filtro/paginação de catálogo. O detalhe **não** reaproveita o objeto da lista: a navegação só leva `itemId`; a tela chama `GET /api/v1/items/{id}` para não ficar desatualizada.

## Endpoints consumidos

| Rota | Use case | Tela |
|---|---|---|
| `GET /api/v1/items` | `GetItems` | `ItensScreen` |
| `GET /api/v1/items/{id}` | `GetItemDetails` | `ItemDetailsScreen` |
| `GET /api/v1/categories` | `GetCategories` | chips de categoria em `ItensScreen` |

Não implementados (existem no contrato, mas a tela ainda só lê):

| Rota | Por quê |
|---|---|
| `POST /api/v1/items/{id}/approve` | Botão “Aprovar” existe na UI; `onApproveClick` é TODO até a regra de negócio de transição de status ser definida. |
| `PATCH /api/v1/items/{id}` | Botão “Editar” e lápis das linhas: TODO; a spec de leitura não cobre o corpo de `UpdateItemRequest`. |
| `POST /api/v1/items/{id}/reject` | Não há botão “Recusar” na tela. |

## O que foi feito na listagem e por quê

- **Use case `GetItems` + `InventoryService.getItems`.** Uma chamada por filtro simples, com `page`/`size` (20). Substitui o mock da tela e o atalho da home (que não é catálogo).
- **Busca `q`.** Só dispara em `onSearch` (e quando o campo é limpo). Evita um request a cada tecla. O placeholder ainda diz “ID ou Nome”; a API casa `displayCode`, nome do item, modelo e material — **não** o UUID `id`.
- **Chip “Todos”.** Omite `status` e `categoryId` (catálogo da unidade).
- **Chip “Pendentes”.** A API aceita **um** `status` por request. O app faz duas chamadas (`PENDING_APPROVAL` e `AWAITING_EVALUATION`), mescla por `id` e soma `totalElements`. É o workaround do contrato; não existe `statusIn`.
- **Chip “Categoria”.** `GetCategories` troca o rótulo genérico pelos `name` da unidade; o filtro manda `categoryId` (UUID), não o nome. Se categorias falharem, a lista ainda carrega — só o chip fica genérico.
- **Título “N Itens”.** Vem de `totalElements` da página (formato `pt-BR`). No chip “Pendentes” o total é a soma das duas páginas (pode superestimar se o backend paginar de forma independente).
- **Paginação infinita.** `ProductList.onEndReached` → `loadNextPage`. Dedup por `id` ao concatenar. `isLoadingMore` no rodapé.
- **Linha da lista.** Só `id`, `name` e chip de status (`ItemStatus.fromBackend`). Demais campos do JSON são ignorados (`ignoreUnknownKeys`).
- **Toque no item.** `ZeraNavigator.push(Route.ItemDetails(itemId = product.id))` — UUID, não `displayCode`.

## O que foi feito no detalhe (PDI) e por quê

- **Use case `GetItemDetails`.** Um GET por `itemId` da rota. `LaunchedEffect(itemId)` recarrega se a rota mudar.
- **Mapeamento de campos** (contrato → UI):

| UI | Origem | Por quê |
|---|---|---|
| id | `id` | Identificador da rota/API |
| Nome | `name` | Título do card |
| Subtítulo | `model.name` | Não existe campo “subtítulo”; o mock (“Notebook Mac”) era o modelo |
| Chip de status | `status` → `ItemStatus.fromBackend` | Mesma tabela da listagem; `DRAFT`/`REMOVED` não têm chip |
| Categoria | `model.category.name` | Categoria vive no modelo, não no item |
| Material | `model.materials[].name` juntados com “e” | O modelo pode ter vários materiais |
| Condição | `condition` (`NEW`/`USED`/…) em português | Texto da tabela do contrato |
| Cadastrado por | `createdByName` | Nome já vem pronto; não há GET extra de usuário |
| Data de cadastro | `createdAt` formatado `pt-BR` | ISO da API → “26 de set 2026 - 21h11” |

- **Aliases de status.** `ItemStatus.fromBackend` aceita `PENDING` e `APPROVED` além dos enums oficiais, porque payloads antigos/ variados já apareceram.
- **Botões Editar / Aprovar.** Permanecem visíveis (layout da tela), mas **não** chamam API. Implementar escrita exigiria use cases novos e regra de quem pode aprovar o quê.

## Testes

Mapeamento puro (sem Retrofit) em:

- `ItensMappingTest` — query dos chips, merge de páginas, rótulo de total, `productFrom`, `canLoadMore`
- `ItemDetailsMappingTest` — status, condição, materiais, `stateFrom`
- `ItemResponseDTOTest` / `CategoryResponseDTOTest` — serialização

## O que fazer em caso de dúvida de contrato

Pare imediatamente de desenvolver e informe de forma estruturada o contexto, o problema e o que te afeta. Não invente payload nem campo extra.
