# Contrato de API — Alertas (`/api/v1/notifications`, `/api/v1/auth/service-token`)

Documentação de contrato das rotas do `ms-administrative-core` relacionadas a **alertas**: ingestão serviço-a-serviço (ms-inventory, IA core) e emissão do token necessário para chamá-las.

**Escopo HTTP hoje:** ingestão serviço-a-serviço (`POST /api/v1/notifications/alerts`) e **listagem do próprio usuário** (`GET /api/v1/notifications/alerts`). Não há `PATCH` nem `DELETE` — o app não fecha alerta por esta API; `CLOSED` entra na ingestão ou na manutenção (`POST /api/v1/maintenance/alerts/close-stale`).

---

## Autenticação e headers comuns

| Header | Obrigatório | Descrição |
|---|---|---|
| `Authorization` | Sim (em `/notifications/alerts`) | `Bearer <access_token>`. **POST** exige **token de serviço** com escopo `notifications:write` (`POST /api/v1/auth/service-token`). **GET** exige JWT autenticado; o destinatário é o `sub` do token (usuário), não um query param. |
| `Content-Type` | Sim (corpo JSON no POST) | `application/json` |

A unidade de destino **não** vem de header: mandar `unitId` no corpo do alerta.

`POST /api/v1/auth/service-token` é **público** neste serviço (`permitAll`): a autenticação é o par `clientId`/`clientSecret` no body.

Este microserviço **não** valida header `apiKey`. Se a chamada passar pelo gateway, o edge pode exigir `x-api-key` à parte.

### Quem acessa cada rota de alerta

| Token | `POST /notifications/alerts` | `GET /notifications/alerts` |
|---|---|---|
| Ausente ou inválido | `401` | `401` |
| JWT de usuário (`MANAGER` ou `EMPLOYEE`, claim `role`) | `403` — papel de usuário não alcança ingestão interna | `200` — só os alertas cujo `user_id` = `sub` |
| Token de serviço com `notifications:write` | `202` (alerta aceito) | Tecnicamente autenticado, mas o `sub` é o cliente (`ms-inventory`), não um `user_account` — a lista sai vazia. Não usar GET com token de serviço. |
| Token de serviço com outro escopo | `403` | Idem GET (autenticado, lista vazia / sem destinatário útil) |

O token de serviço traz claim `scope` (vira `SCOPE_notifications:write` na autorização) e **não** traz `role`. O JWT de login de usuário traz `role` e **não** traz o escopo de notificação.

Cliente configurado por ambiente (exemplo padrão): `clientId` = `ms-inventory`, segredo em `MS_INVENTORY_CLIENT_SECRET`, escopos = `notifications:write`. TTL do token: **15 minutos** (`zera.service-auth.token-ttl=PT15M`); **não há refresh token** — pedir outro `service-token` quando expirar.

---

## 1. `POST /api/v1/auth/service-token` — obter token para postar alertas

Pré-requisito para `POST /api/v1/notifications/alerts`. Não exige `Authorization`.

### Request

```
POST /api/v1/auth/service-token HTTP/1.1
Content-Type: application/json

{
  "clientId": "ms-inventory",
  "clientSecret": "<segredo do ambiente>"
}
```

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `clientId` | string | Sim | Identificador do cliente de serviço cadastrado em `zera.service-auth.clients.*`. |
| `clientSecret` | string | Sim | Segredo do mesmo cliente (Secret do k8s / variável de ambiente). Não pode ser em branco. |

```bash
curl -X POST "https://<host>/api/v1/auth/service-token" \
  -H "Content-Type: application/json" \
  -d '{"clientId":"ms-inventory","clientSecret":"<segredo>"}'
```

### Response — `200 OK`

```json
{
  "accessToken": "eyJhbGciOi...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

- `expiresIn`: segundos até expirar (900 = 15 min com TTL padrão).
- Usar `Authorization: Bearer <accessToken>` nas chamadas a `/notifications/alerts`.

| Status | Quando |
|---|---|
| `400` | Body inválido ou `clientId`/`clientSecret` em branco. |
| `401` | `clientId` inexistente ou segredo incorreto. |

---

## 2. `POST /api/v1/notifications/alerts` — registrar alerta / notificação

Rota **interna** (serviço-a-serviço). Persiste um `Alert` para o `userId` informado. Resposta **sem corpo** — o id gerado fica só no banco; a API não devolve o alerta criado.

Quem dispara hoje: principalmente **ms-inventory** (regras de estoque, garantia, aprovação de item, etc.) e, no desenho do produto, **ms-artificial-intelligence-core** (mesmo contrato de body e autenticação, quando configurado como cliente de serviço).

### Request

```
POST /api/v1/notifications/alerts HTTP/1.1
Authorization: Bearer <service_access_token>
Content-Type: application/json
```

Exemplo completo (com deduplicação por regra + evento):

```json
{
  "eventId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "ruleId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "userId": "9c858901-8a57-4791-81fe-4c455b099bc9",
  "unitId": "aa11bb22-0000-0000-0000-000000000009",
  "description": "Estoque da unidade acima de 90% da capacidade configurada.",
  "severity": "HIGH",
  "kind": "STOCK_QUANTITY_LIMIT",
  "status": "OPEN",
  "occurredAt": "2026-09-20T03:15:00"
}
```

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `eventId` | UUID | Não | Identificador do **evento** que disparou o alerta (lado do ms-inventory). |
| `ruleId` | UUID | Não | Identificador da **regra** configurada na unidade. |
| `userId` | UUID | Sim | Destinatário do alerta. Precisa existir em `user_account`; senão `404`. |
| `unitId` | UUID | Sim | Unidade associada ao alerta. |
| `description` | string | Sim | Texto exibível (máx. **500** caracteres). Não pode ser só espaços. |
| `severity` | `LOW`\|`MEDIUM`\|`HIGH` | Sim | Prioridade do alerta. |
| `kind` | `AlertKind` | Sim | Tipo semântico — ver catálogo abaixo. Valor desconhecido: `400`. |
| `status` | `OPEN`\|`CLOSED` | Sim | Estado inicial desejado; na prática quem ingere costuma mandar `OPEN`. |
| `occurredAt` | datetime (ISO-8601) | Não | Momento em que a condição foi detectada (`LocalDateTime`, sem timezone). Se omitido, o backend usa **agora** no servidor. Se informado, deve ser **passado ou presente** (`@PastOrPresent`); futuro: `400`. |

```bash
curl -X POST "https://<host>/api/v1/notifications/alerts" \
  -H "Authorization: Bearer eyJhbGciOi..." \
  -H "Content-Type: application/json" \
  -d '{
    "userId":"9c858901-8a57-4791-81fe-4c455b099bc9",
    "unitId":"aa11bb22-0000-0000-0000-000000000009",
    "description":"Item aprovado pelo gestor.",
    "severity":"LOW",
    "kind":"ITEM_APPROVED",
    "status":"OPEN"
  }'
```

### Response — `202 Accepted`

Corpo vazio. O alerta foi aceito para persistência (criação nova ou atualização por deduplicação — ver abaixo).

| Status | Quando |
|---|---|
| `400` | JSON inválido, enum desconhecido (`kind`/`severity`/`status`), campos obrigatórios ausentes, `description` em branco, ou `occurredAt` no futuro. |
| `401` | Sem token ou token inválido. |
| `403` | Token de usuário ou escopo de serviço inadequado. |
| `404` | `userId` inexistente (`User not found: <uuid>` em ProblemDetail). |

---

## 3. `GET /api/v1/notifications/alerts` — listar alertas do usuário autenticado

Rota do **app** (usuário logado). O filtro de destinatário **não** aceita `userId` na query: vem do `sub` do JWT (`Principal.getName()`). Assim o cliente não consegue listar alerta de outra pessoa.

Paginação offset (`page`/`size`); resposta é **array JSON**, sem envelope de total/páginas. Ordenação: `createdAt` **descendente** (mais recente primeiro).

### Request

```
GET /api/v1/notifications/alerts?status=OPEN&page=0&size=20 HTTP/1.1
Authorization: Bearer <user_access_token>
```

| Query | Tipo | Obrigatório | Default | Descrição |
|---|---|---|---|---|
| `status` | `OPEN`\|`CLOSED` | Não | (todos) | Se omitido, devolve abertos e fechados. Valor desconhecido: `400`. |
| `page` | int | Não | `0` | Página (0-based). |
| `size` | int | Não | `20` | Tamanho da página. |

```bash
curl "https://<host>/api/v1/notifications/alerts?status=OPEN&page=0&size=20" \
  -H "Authorization: Bearer eyJhbGciOi..."
```

### Response — `200 OK`

```json
[
  {
    "alertId": "11111111-1111-1111-1111-111111111111",
    "kind": "STOCK_QUANTITY_LIMIT",
    "severity": "HIGH",
    "status": "OPEN",
    "description": "Estoque da unidade acima de 90% da capacidade configurada.",
    "unitId": "aa11bb22-0000-0000-0000-000000000009",
    "ruleId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "eventId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
    "occurredAt": "2026-09-20T03:15:00",
    "createdAt": "2026-09-20T03:15:01",
    "updatedAt": "2026-09-20T03:15:01"
  }
]
```

`ruleId` / `eventId` podem ser `null` quando o POST de ingestão não mandou o par (notificação pontual). Lista vazia: `200` + `[]` (usuário sem alertas, ou página além do fim).

| Status | Quando |
|---|---|
| `200` | Lista (possivelmente vazia). |
| `400` | `status` que não é `OPEN`/`CLOSED`. |
| `401` | Sem token ou token inválido. |

Não há `404` nesta rota: usuário autenticado sem alertas é lista vazia, não “usuário não encontrado”.

---

## Deduplicação (`eventId` + `ruleId`)

- **Com deduplicação:** enviar **os dois** `eventId` e `ruleId` preenchidos. Se já existir alerta **`OPEN`** com o mesmo par, o backend **não cria outro registro** — atualiza o existente:
  - se a nova `severity` for **maior** que a gravada, escala a severidade;
  - caso contrário, só atualiza `updatedAt` (“touch”).
- **Sem deduplicação:** omitir `eventId` e/ou `ruleId` (ambos `null`). Cada `POST` gera um **novo** alerta. Casos típicos: `ITEM_APPROVED`, `ITEM_REJECTED`, notificações pontuais sem regra recorrente.
- **Concorrência:** índice único parcial `ux_alert_open_rule_event` garante no máximo um `OPEN` por `(rule_id, event_id)`; corrida entre duas requisições simultâneas é tratada como duplicata (refresh do vencedor).

Alertas **`CLOSED`** com o mesmo par não bloqueiam um novo `OPEN` (o índice só vale para `status = 'OPEN'`).

---

## Catálogo de `AlertKind` (todos os valores aceitos)

| `kind` | Origem / significado | Notas para quem integra |
|---|---|---|
| `STORAGE` | Legado — aviso de armazenamento | Tipos originais; ainda aceitos. |
| `TIME` | Legado — aviso temporal (ex. turno/atraso) | Idem. |
| `WARRANTY_EXPIRATION` | Garantia do item chegando ao fim | Regra ms-inventory v1. |
| `LIFESPAN_EXPIRATION` | Vida útil esperada do item chegando ao fim | Idem. |
| `USAGE_INTENSITY_LIMIT` | Intensidade de uso acima do limite | Idem. |
| `STOCK_QUANTITY_LIMIT` | Ocupação do estoque acima do limite | Idem. |
| `TIME_IN_STOCK_LIMIT` | Item há tempo demais em estoque | Idem. |
| `STALE_ITEM` | Item sem movimentação há tempo demais | Idem. |
| `RECYCLABLE_TO_LANDFILL` | Reciclável enviado ao aterro no descarte | Idem. |
| `PREDICTED_FAILURE` | Data prevista de quebra se aproximando (predição) | Idem. |
| `ITEM_APPROVED` | Cadastro de item aprovado pelo gestor | Destino: quem cadastrou o item; dedup opcional. |
| `ITEM_REJECTED` | Cadastro reprovado pelo gestor | Motivo na `description`; destino: quem cadastrou. |

Qualquer valor fora desta lista (ex. `"kind":"NAO_EXISTE"`) resulta em **`400`** antes de chegar ao use case.

---

## Comportamento no servidor (referência, não é rota)

Após `202`, o registro em `alert` contém, entre outros: `id` (UUID gerado), `user_id`, `unit_id`, `kind`, `severity`, `description`, `status`, `rule_id`, `event_id`, `occurred_at`, `created_at`, `updated_at`.

- `occurredAt` do body prevalece sobre a hora da requisição; se vier `null`, usa-se o instante da gravação.
- `status` enviado no body é persistido como informado (ex. `OPEN` na ingestão típica).
- Não há push/WebSocket neste contrato: persistência + log estruturado `[ALERT]` no serviço; o app lê via `GET`.

---

## Rota auxiliar (fora de `/notifications`) — resolver destinatário

O ms-inventory **não** recebe o `userId` do gestor pelo alerta em todos os fluxos; costuma descobrir o gestor da unidade antes de postar:

```
GET /api/v1/users?role=MANAGER&unitId=<unitId>
Authorization: Bearer <token de usuário ou conforme política do caller>
```

Contrato completo de usuários: [`team-management-api-contract.md`](team-management-api-contract.md). Para alertas dirigidos ao **operador**, usar o `userId` de quem executou a ação (ex. autor do cadastro de item).

---

## Enums usados

- **`Severity`**: `LOW`, `MEDIUM`, `HIGH` (ordem de escalonamento na deduplicação).
- **`AlertStatus`**: `OPEN`, `CLOSED`.
- **`AlertKind`**: ver tabela acima (`STORAGE`, `TIME`, …, `ITEM_REJECTED`).

---

## Qual rota escolher

| Preciso de... | Rota |
|---|---|
| Credencial para o ms-inventory / outro serviço postar alerta | `POST /auth/service-token` → usar `accessToken` |
| Registrar alerta ou notificação para um usuário | `POST /notifications/alerts` com escopo `notifications:write` |
| Evitar spam da mesma regra no mesmo evento | Repetir `POST` com o **mesmo** `ruleId` + `eventId` enquanto o alerta estiver `OPEN` |
| Notificação única (aprovação/reprovação de item) | `POST /notifications/alerts` **sem** `ruleId`/`eventId` (ou só um deles null) |
| Listar alertas do usuário no app | `GET /notifications/alerts` com JWT de usuário (`status`, `page`, `size` opcionais) |
| Fechar alerta pelo app | **Não disponível** — sem `PATCH`/`DELETE` |
| Achar `userId` do gestor da unidade antes de alertar | `GET /users?role=MANAGER&unitId=` (contrato em team-management) |