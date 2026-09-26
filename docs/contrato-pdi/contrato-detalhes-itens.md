# Contrato de API — `/api/v1/items` e `/api/v1/categories`

Documentação de contrato das rotas de itens do `ms-inventory` para `ItensScreen` e `ItemDetailsScreen` do app mobile. **Já existem** — não é preciso criar rota nova, só usar o endpoint certo em vez de `/dashboard/home`.

## Autenticação e headers comuns

| Header | Obrigatório | Descrição |
|---|---|---|
| `apiKey` | Sim | — |
| `Authorization` | Sim | `Bearer <access_token>` — JWT do ms-administrative-core. |
| `X-Unit-Id` | Sim | UUID da unidade do usuário logado. |

Sem `X-Unit-Id`: `400 Bad Request` (ProblemDetail, mesmo formato do `dashboard-api-contract.md`). Sem token/apiKey: `401`.

---

## 1. `GET /api/v1/items` — listagem para `ItensScreen`

**Não usar `/dashboard/home.recentItems`** — é "últimos cadastrados", não o catálogo com busca/filtro/paginação completa. Esta rota é o catálogo.

### Request

```
GET /api/v1/items?status=IN_STOCK&categoryId=<uuid>&q=notebook&page=0&size=20 HTTP/1.1
Authorization: Bearer <token>
apiKey: <apiKey>
X-Unit-Id: 3fa85f64-5717-4562-b3fc-2c963f66afa6
```

| Query param | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `status` | `ItemStatus` | Não | Ver mapeamento de chips abaixo. Omitir = todos os status (chip "Todos"). |
| `categoryId` | UUID | Não | Chip "Categoria" — app manda o `id` da categoria (de `GET /api/v1/categories`), não o nome. |
| `modelId` | UUID | Não | Não usado pelas telas atuais; disponível se precisar. |
| `q` | string | Não | Busca. Cobre prefixo do `displayCode`, nome do item, nome do modelo e material — **cobre nome, e código curto (`displayCode`), não o UUID `id`.** Ver nota abaixo sobre busca por ID. |
| `eligibleForDisposal` | boolean | Não | Não usado por esta tela. |
| `page` / `size` | int | Não (default `0`/`20`) | Paginação 0-based. |

```bash
curl -X GET "https://<host>/api/v1/items?page=0&size=20" \
  -H "Authorization: Bearer eyJhbGciOi..." \
  -H "apiKey: <apiKey>" \
  -H "X-Unit-Id: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
```

### Response — `200 OK`

Envelope de paginação igual ao `/dashboard/home` (`PageResponse<ItemResponse>`):

```json
{
  "content": [
    {
      "id": "9c858901-8a57-4791-81fe-4c455b099bc9",
      "barcode": "7891234567890",
      "displayCode": "ITM-0042",
      "name": "Notebook Dell Latitude",
      "status": "IN_STOCK",
      "condition": "USED",
      "hasDamages": false,
      "damages": [],
      "notes": null,
      "photoUrl": "https://storage.example.com/photos/item-9c85.jpg",
      "unitId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "model": { "...": "ver ModelResponse na secao 2" },
      "serialNumber": "SN123456",
      "acquiredAt": "2026-07-01",
      "manufacturingYear": 2024,
      "usageIntensity": 3,
      "predictedFailureDate": "2029-07-01",
      "predictionUpdatedAt": "2026-08-01T10:00:00",
      "missingFields": [],
      "lastEventAt": "2026-09-20T15:00:00",
      "createdBy": "user-uuid",
      "createdByName": "João Silva",
      "createdAt": "2026-07-01T09:00:00",
      "updatedAt": "2026-09-20T15:00:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1230,
  "totalPages": 62
}
```

Título "1.230 Itens" da tela = `totalElements`, sem filtro (chip "Todos", sem `q`). Item mínimo da lista: `id`, `name`, `status` — os demais campos vêm de brinde, ignorar o resto na linha da lista.

### Mapeamento de chips → `status`

| Chip do app | `status` (um ou mais valores) |
|---|---|
| "Todos" | omitir `status` (não filtra) |
| "Pendentes" | `PENDING_APPROVAL` **e** `AWAITING_EVALUATION` — a API só aceita **um** valor de `status` por chamada; se o app quiser os dois num chip só, precisa de 2 chamadas (uma por status) e mesclar as páginas no cliente, **ou** pedir ao backend um `statusIn` que aceite lista. Hoje não existe filtro por múltiplos status numa chamada. |
| "Categoria" | usa `categoryId`, não `status` |

### Nota sobre "busca por ID"

O placeholder da busca diz "Pesquisar por ID ou Nome...". A implementação atual (`ItemFilter.query`) casa com **`displayCode`** (código curto, ex. `"ITM-0042"`), não com o UUID interno (`id`). Se a intenção do app é buscar pelo `displayCode` visível ao usuário, já funciona. Se for para buscar pelo UUID completo, não tem suporte hoje — confirmar com o backend qual dos dois é o "ID" da UI antes de assumir que funciona.

### Enum `ItemStatus` → chip/tag da UI

| `ItemStatus` (backend) | Chip/tag do app (`ItemStatus` do app) |
|---|---|
| `PENDING_APPROVAL` | Pendente |
| `REJECTED` | Recusado |
| `IN_STOCK` | Aprovado |
| `IN_MAINTENANCE` | Em manutenção |
| `AWAITING_EVALUATION` | Em aprovação |
| `DISPOSED` | Descartado |
| `DRAFT` | sem chip no app hoje — item ainda incompleto, geralmente não aparece pro gestor fora da Central de Trabalho |
| `REMOVED` | sem chip no app hoje — removido logicamente, não aparece em listagens normais |

---

## 2. `GET /api/v1/items/{id}` — `ItemDetailsScreen`

Navegação só leva `itemId` (UUID); esta rota resolve tudo que a tela precisa. **Não depender do objeto vindo da lista.**

### Request

```
GET /api/v1/items/9c858901-8a57-4791-81fe-4c455b099bc9 HTTP/1.1
Authorization: Bearer <token>
apiKey: <apiKey>
X-Unit-Id: 3fa85f64-5717-4562-b3fc-2c963f66afa6
```

Item de outra unidade (`X-Unit-Id` não confere) ou inexistente: `404`.

### Response — `200 OK` (`ItemResponse`, mesmo shape da seção 1, um item só)

```json
{
  "id": "9c858901-8a57-4791-81fe-4c455b099bc9",
  "displayCode": "ITM-0042",
  "name": "Notebook Dell Latitude",
  "status": "IN_STOCK",
  "condition": "USED",
  "serialNumber": "SN123456",
  "createdByName": "João Silva",
  "createdAt": "2026-07-01T09:00:00",
  "model": {
    "id": "1b2c3d4e-0000-0000-0000-000000000001",
    "name": "Latitude 5420",
    "manufacturer": "Dell",
    "materials": [{"id": "m1", "name": "Plastic"}],
    "category": {"id": "c1", "name": "Informática", "description": null},
    "...": "demais campos de ModelResponse, ver dashboard-api-contract.md"
  },
  "...": "demais campos iguais ao ItemResponse da secao 1"
}
```

### Mapeamento campo da tela → campo da resposta

| Campo da UI | Campo da resposta |
|---|---|
| `id` | `id` |
| Nome | `name` |
| Subtítulo (hoje mock "Notebook Mac") | `model.name` — é o nome do **modelo**, não existe campo separado de "subtítulo"; app deve exibir `model.name` |
| Status (chip) | `status` — mapear pela tabela da seção 1 |
| Categoria | `model.category.name` |
| Material | `model.materials[].name` (lista; item pode ter 1+ materiais no modelo) |
| Condição | `condition` — mapear pela tabela abaixo |
| Cadastrado Por | `createdByName` |
| Data de Cadastro | `createdAt` |

### Enum `ItemCondition` → texto de UI

| `ItemCondition` | Texto sugerido |
|---|---|
| `NEW` | Novo |
| `USED` | Usado |
| `SEMI_DAMAGED` | Semidanificado |
| `DAMAGED` | Danificado |

### Botões Editar e Aprovar

Ambos **já existem** como fluxo de backend:

- **Aprovar**: `POST /api/v1/items/{id}/approve` (`X-Unit-Id` obrigatório). Exige papel `MANAGER`. Sem corpo. Transição de estado inválida (ex. item já aprovado) responde `409`.
- **Editar**: `PATCH /api/v1/items/{id}` com corpo `UpdateItemRequest` (`X-Unit-Id` obrigatório). Sem restrição de papel além de estar autenticado no `ItemController` (todas as rotas exigem `Authz.INVENTORY_OPERATOR` por padrão da classe). Ver `UpdateItemRequest.java` para os campos aceitos — não coberto neste doc porque a tela hoje só lê, não edita.

Existe também `POST /api/v1/items/{id}/reject` (`MANAGER`, corpo com `reason`), útil se a tela ganhar botão "Recusar".

---

## Categorias — filtro "Categoria" da busca

`GET /api/v1/categories` (`X-Unit-Id` obrigatório) devolve a lista de categorias da unidade, para popular o chip "Categoria":

```json
[{ "id": "c1", "unitId": "3fa85f64-...", "name": "Informática", "description": null, "createdAt": "...", "updatedAt": "..." }]
```

App usa `id` no filtro `categoryId` de `GET /api/v1/items`, e `name` para exibir o chip.

---

## O que não usar como substituto

- `/dashboard/home` — números de estoque + últimos itens; `recentItems` não é o catálogo, não tem busca nem filtro de categoria/status combinável.
- `/dashboard/work-center` — pendências do usuário logado, sem paginação/busca, não serve como listagem geral.
- `/dashboard/indicators` — reciclagem, não tem itens.

## Qual rota escolher

| Preciso de... | Rota |
|---|---|
| Catálogo paginado com busca e filtros (chips) | `GET /api/v1/items` |
| Detalhe de um item por `itemId` | `GET /api/v1/items/{id}` |
| Lista de categorias para o chip "Categoria" | `GET /api/v1/categories` |
| Aprovar item | `POST /api/v1/items/{id}/approve` |
| Editar item | `PATCH /api/v1/items/{id}` |