# Contrato de API — descarte (`ms-inventory`) e relatório (`ms-ai`)

Registro do lote **já feito** no inventory e PDF no `ms-artificial-intelligence-core`. Mapa, ficha e telefone: [contrato-places-api.md](contrato-places-api.md). Como o app pluga: [contexto.md](contexto.md). Catálogo de itens (`GET /items`): [contrato-pdi](../contrato-pdi/contrato-detalhes-itens.md).

O inventory guarda `destination`, `placeId` e `placeName`. Não guarda CNPJ/e-mail/telefone da parceira.

---

## 1. `POST /api/v1/disposals` — registrar descarte

Fonte: [API ms-inventory](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55869441/3.+API+ms-inventory+endpoints+contratos+e+erros). Operário ou gestor. `X-Unit-Id` no header, nunca no corpo.

Não agenda. `disposedAt` ausente = hoje. **Nunca no futuro.** Só `IN_STOCK` e `AWAITING_EVALUATION` podem ir a `DISPOSED`. `DISPOSED` é terminal. Se um item da lista não puder ser descartado, a chamada inteira responde `409` e nada é gravado.

### Headers

| Header | Obrigatório |
|---|---|
| `apiKey` | Sim |
| `Authorization` | Sim — `Bearer` JWT do adm-core |
| `X-Unit-Id` | Sim — UUID da unidade |

Sem `X-Unit-Id`: `400`. Sem token/apiKey: `401`.

```json
{
  "destination": "RECYCLING",
  "placeId": "places/ChIJxxxxxxxx",
  "placeName": "Ecoponto Central",
  "disposedAt": "2026-09-28",
  "notes": null,
  "itemIds": ["9c858901-8a57-4791-81fe-4c455b099bc9"]
}
```

| Campo | Obrigatório | Regra |
|---|---|---|
| `destination` | Sim | O fluxo do mapa manda `RECYCLING`. |
| `placeId` | Não | `placeId` do pin (`GET /recycling-places`). |
| `placeName` | Não | Nome exibido do pin. |
| `disposedAt` | Não | `YYYY-MM-DD`. Ausente = hoje. Nunca futuro. |
| `notes` | Não | — |
| `itemIds` | Sim | Ao menos um. Duplicados colapsam. |

Resposta: corpo com `id` do descarte. Esse `id` é o `disposal_id` de `POST /api/v1/reports`. Também existem `GET /disposals`, `GET /disposals/{id}` e `PATCH /disposals/{id}` (corrigir destino). O fluxo do gestor usa o `id` do POST; não precisa do GET para gerar o PDF.

O app **não** envia `disposedAt` hoje (backend assume a data atual).

---

## Assistente de IA (`/api/v1/multi-agent`, `/api/v1/reports`)

No fluxo de descarte do app, só o grupo **relatório (PDF)** é usado. O chat fica documentado aqui porque é o mesmo microserviço.

JSON em **snake_case**. Este MS **não** valida `apiKey` nas rotas próprias. Chamada de saída para o admin-core usa `apikey` de serviço.

| Autorização | Rotas |
|---|---|
| JWT encaminhado + `unit_id` conferido no admin-core | `POST /api/v1/multi-agent/process-message` |
| Sem JWT neste serviço (identifica por `user_id`) | `GET /conversations`, `GET /conversations/{thread_id}`, `POST /reports`, `GET /reports/{disposal_id}` |
| Público | `GET /health` |

`process-message` sem `Authorization`: **`422`**. Token inválido / `unit_id` que não é do usuário: **`403`** (`"unit_id does not belong to this user"`). Não há `401` neste MS. Unidade **não** vem de header — vai no body (`unit_id`).

---

## 2. `GET /health`

Sem prefixo `/api/v1`. `{ "status": "ok" }`.

---

## 3. `POST /api/v1/multi-agent/process-message` — um turno do chat

O cliente **não** escolhe o agente. `report_url` neste endpoint é **sempre `null`**. PDF = seção 5.

```json
{
  "user_id": "9c858901-8a57-4791-81fe-4c455b099bc9",
  "thread_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "unit_id": "aa11bb22-0000-0000-0000-000000000009",
  "content": "O que é o projeto Zera?"
}
```

| Campo | Obrigatório | Descrição |
|---|---|---|
| `user_id` | Sim | UUID do dono. Mesmo do JWT no admin-core. |
| `thread_id` | Sim | **O app gera** (UUID v4). Mesmo par = continua histórico. |
| `unit_id` | Sim | Tem que coincidir com `unitId` no admin-core. Ausente: `422`. Errado: `403`. |
| `content` | Sim | 1–**8000** caracteres. |

`200`:

```json
{
  "content": "Zera é a plataforma de gestão de sucata eletrônica...",
  "blocked": false,
  "blocked_reason": null,
  "agent_trace": ["guardrail_in", "orchestrator", "faq", "formatter", "judge", "guardrail_out"],
  "report_url": null
}
```

`blocked: true` ainda é HTTP **200**. `agent_trace` é telemetria, não UI. Recusa de `unit_id`: `403`. Body/header inválidos: `422` (envelope FastAPI).

---

## 4. Histórico de conversas

Sem `Authorization` neste serviço. Recorte por `user_id`. Gateway deve autenticar se a chamada passar pelo edge.

### 4.1 `GET /api/v1/multi-agent/conversations`

`user_id` obrigatório. `page` **1-based** (default 1). `page_size` 1–100 (default 20). `preview` = 40 chars da **primeira** mensagem. Lista vazia: `items: []`, `total: 0` (não `404`).

### 4.2 `GET /api/v1/multi-agent/conversations/{thread_id}?user_id=`

Array simples, mais antigas primeiro. `role`: `user` / `assistant` / `system`. Thread inexistente ou de outro usuário: **`[]`**.

---

## 5. Relatório de descarte (PDF)

Um relatório por `disposal_id` (índice neste MS). Chat **não** sobe arquivo.

### 5.1 `POST /api/v1/reports`

Gera HTML pelo `report_agent` (sem grafo inteiro), PDF, storage, persiste `{disposal_id, report_url}`. Se já existir, **devolve a URL** (não regenera).

```json
{
  "user_id": "9c858901-8a57-4791-81fe-4c455b099bc9",
  "disposal_id": "42"
}
```

`user_id` UUID obrigatório (auditoria; não filtra o GET). `disposal_id` string, mínimo 1 caractere — é o `id` do `POST /disposals`.

`200`: `{ "report_url": "https://cdn.example.com/disposal-42.pdf" }`. Arquivo: `disposal-{disposal_id}.pdf`. Falha de storage **não** grava no Mongo. `422` body inválido.

### 5.2 `GET /api/v1/reports/{disposal_id}`

Não gera. `404`: `{"detail": "disposal report not found"}`.

Fluxo no app: após confirmar, `POST /reports` e abrir `report_url`. Reabrir: `GET`; `404` → POST.

Boot Scrapy: `ms-ai-url` / `ms-ai-api-key`. Sem URL, o app falha com mensagem clara.

---

## Enums (IA)

- `role`: `user`, `assistant`, `system`.
- `agent_trace`: `guardrail_in`, `orchestrator`, `faq`, `report`, `predict_model`, `inventory_agent`, `formatter`, `judge`, `guardrail_out`.
- `blocked`: boolean. Recusa lógica **não** vira 4xx.

---

## Qual rota escolher

| Preciso de... | Rota |
|---|---|
| Registrar descarte (itens → `DISPOSED`) | `POST /api/v1/disposals` (`apiKey` + JWT + `X-Unit-Id`) |
| URL do PDF (gerar) | `POST /api/v1/reports` — **não** usar `report_url` do chat |
| URL do PDF já gerado | `GET /api/v1/reports/{disposal_id}` |
| Chat / FAQ / texto de relatório | `POST /api/v1/multi-agent/process-message` |
| Inbox de conversas | `GET /api/v1/multi-agent/conversations?user_id=` |
| Pin / ficha / telefone | [contrato-places-api.md](contrato-places-api.md) |
