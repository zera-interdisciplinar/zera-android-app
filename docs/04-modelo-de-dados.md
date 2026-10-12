# 04 — Modelo de Dados

## Convenção DTO vs entity

- **`model/dto/<domínio>/`** — payloads de API (`@Serializable`, sufixo `DTO`).
- **`model/entity/`** — modelos que não são o JSON da request/response: `Environments` (boot), `FlagsRequest` (body Scrapy), `UserRole` (constantes de `role`).

Não coloque DTO em `entity/` nem entity em `dto/`.

## DTOs de autenticação

Pacote `model/dto/auth/`.

- **`SingInRequestDTO(email: String, password: String)`** — corpo do `POST auth/login`.
- **`SingInResponseDTO(userId, accessToken, refreshToken, tokenType, expiresIn: Long)`** — resposta do login; campos `var` (mutáveis), sem valor padrão.

> Nota de nomenclatura: a grafia "Sing" (em vez de "Sign") é a mesma usada nas classes de código — ver "Inconsistências conhecidas" em [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md).

## DTOs de convite

Pacote `model/dto/invitation/`.

- **`RedeemRequestDTO(code, name, email, rawPassword)`** — corpo do `POST invitations/redeem`.
- **`RedeemResponseDTO(userId, name, email, role, managerId)`** — resposta do resgate do convite. O cadastro em seguida chama `SingIn.execute` para abrir a sessão completa (tokens + `unitId` + flags).

## DTOs de usuário

Pacote `model/dto/user/`. Constantes de papel em `model/entity/user/UserRole` (`EMPLOYEE`, `MANAGER`).

- **`SelfUserResponseDTO(userId, name, email, role, status, unitId, createdAt, updatedAt, managerId: String?)`** — `GET users/{id}`. `role` é `"MANAGER"` ou `"EMPLOYEE"` (v1 não tem Administrador). `unitId` vai para `SqliteManager` e o header `X-Unit-Id`.

## DTOs de inventário (dashboard)

Pacote `model/dto/inventory/`. Só os campos que as telas usam; o restante do JSON é ignorado (`ignoreUnknownKeys`). Contratos: [contrato-dashboard/contrato-inventory-dashboard.md](contrato-dashboard/contrato-inventory-dashboard.md), [contrato-pdi/contrato-detalhes-itens.md](contrato-pdi/contrato-detalhes-itens.md).

- **`DashboardHomeResponseDTO`** — `GET /api/v1/dashboard/home`: `activeItems`, `activeItemsChangePercent?`, `occupancyPercent?`, `pendingApproval`, `inMaintenance`, `awaitingEvaluation`, `recentItems`.
- **`WorkCenterResponseDTO`** — `GET /api/v1/dashboard/work-center`: `drafts`, `rejected`, `damagedWithoutDestination`, `inMaintenance`, `awaitingEvaluation`.
- **`PendingItemResponseDTO`** — item da central: `id`, `displayCode?`, `name`, `modelName?`, `missingFields`, `rejectionReason?`, `updatedAt?`.
- **`PagedItemsDTO(content, page, size, totalElements, totalPages)`** — envelope de `recentItems` e de `GET /api/v1/items`.
- **`ItemResponseDTO`** — lista e PDI: `id`, `name`, `displayCode?`, `status?` (aliases JSON `itemStatus`/`approvalStatus`), `condition?`, `createdByName?` (aliases `created_by_name`), `createdBy?` (UUID ou objeto com `name`), `createdAt?`, `model?`.
- **`ModelResponseDTO`** / **`MaterialResponseDTO`** / **`CategoryResponseDTO`** — modelo aninhado no item; categorias também vêm de `GET /api/v1/categories` (`id`, `name`, metadados opcionais).
- **`IndicatorsResponseDTO`** — `GET /api/v1/dashboard/indicators`: `recyclingRatePercent`, `recyclingRateChangePoints?`, `monthlyWeightKg`, `weightByMaterial`.
- **`MonthlyWeightDTO(month, weightKg)`** — `month` no formato `yyyy-MM`.
- **`WeightByMaterialDTO(material, percent)`** — `material` é código (`PLASTIC`, `METAL`, ...).

Campos do contrato **não** modelados no app: `stockCapacity`, `disposalsInWindow`, `windowDays`, barcode/damages/serial da listagem.

## Config dinâmica (Scrapy)

Pacotes `model/entity/config/` e `model/entity/scrapy/`. Contrato: [contrato-scrapy/contrato-scrapy-api.md](contrato-scrapy/contrato-scrapy-api.md).

- **`Environments`** — resposta do `POST /v1/boot`:
  - `admCoreApiUrl` ← `ms-adm-core-url` (base do `AdmCoreClient`)
  - `admCoreApiKey` ← `ms-adm-core-api-key` (header `apiKey` do `AdmCoreClient`; fallback do `InventoryClient`)
  - `inventoryApiUrl` ← `ms-inventory-url` (base do `InventoryClient`)
  - `inventoryApiKey` ← `ms-inventory-api-key` (opcional; se vazio, usa `admCoreApiKey`)
- **`FlagsRequest(attrs: Map<String, JsonElement>)`** — body do `POST /v1/flags`. `LoadFlags` preenche `user_id`, `role`, `unit_id` e `app_version`.
- **`AppConfig.flags`** — `JsonObject` do `POST /v1/flags`. Em memória; não vai para o SQLite.

## Persistência local (SQLite)

Sessão em `SqliteManager` (`model/local/SqliteManager.kt`), banco `zera.db`, tabela `session`. Motivo: [mudanca-shared-para-sqlite/contexto.md](mudanca-shared-para-sqlite/contexto.md).

- `saveSession` / `getAccessToken` / `getRefreshToken` / `getUserId` / `getEmail` / `getPassword`
- `saveUnitId` / `getUnitId` — header `X-Unit-Id`
- `hasSavedLogin` / `clearSession`

Init em `MainActivity.onCreate`. Sem cache de inventário. Refresh de token: [07-especificacoes/refresh-de-sessao.md](07-especificacoes/refresh-de-sessao.md).

## Modelos de chat

Pacote `model/entity/chat/`. São modelos de **UI/estado** (não são o JSON da API). O contrato do back está em [contrato-places-api/contrato-descarte.md](contrato-places-api/contrato-descarte.md) (seção `POST /api/v1/multi-agent/process-message`); spec da tela: [07-especificacoes/chat-bot.md](07-especificacoes/chat-bot.md).

- **`Message(role: MessageRole, content: String, sentAt: LocalDateTime)`** — uma mensagem da conversa. Sem `id`: a identidade é a posição na lista.
- **`MessageRole`** (enum) — `User`, `Assistant`. O back também devolve `system` (enum `role` do histórico); o app **não modela** esse valor e deve ignorá-lo ao montar a lista.

### Padrão esperado pelo back (`process-message`)

Um turno = uma requisição. Corpo (JSON em **snake_case**):

| Campo | Origem no app |
|---|---|
| `user_id` | UUID do usuário logado (o mesmo do JWT) |
| `thread_id` | UUID v4 **gerado pelo app** ao abrir a conversa e reutilizado em todos os turnos dela; novo UUID = nova conversa |
| `unit_id` | unidade do usuário logado (a mesma enviada como `X-Unit-Id`); vai no **body**, não em header |
| `content` | texto digitado, 1–8000 caracteres (`Message.content` da mensagem do usuário) |

Resposta `200`: `content` (texto do assistente → `Message.content` com `MessageRole.Assistant`), `blocked`, `blocked_reason`, `agent_trace`, `report_url`. `blocked: true` ainda é HTTP 200 e `content` traz a recusa a exibir; `agent_trace` é telemetria e `report_url` é sempre `null` neste endpoint — nenhum dos dois vai para a UI. Erros: sem `Authorization` → `422`; `unit_id` que não é do usuário → `403` (não há `401` neste MS).

O back **não devolve** data/hora por mensagem no turno: `sentAt` é preenchido pelo app (`LocalDateTime.now()`) no momento da inserção. O histórico (`GET /conversations/{thread_id}?user_id=`) devolve um array simples, mais antigas primeiro, com `role` em minúsculas (`user`/`assistant`/`system`).

Mapeamento ao integrar: `"user"` → `MessageRole.User`, `"assistant"` → `MessageRole.Assistant`, `"system"` → descartar.

## Mapeamentos entre camadas

Não existe uma camada `toDomain()`. Os `ViewModels` mapeiam DTO → campos de UI no próprio arquivo:

- **Auth:** `SelfUserResponseDTO` é transiente (rota de destino, `unitId` na sessão).
- **Home do gestor:** `DashboardHomeResponseDTO` → `ManagerHomeState` (ocupação, totais, alertas, `ProductItem`). Regras em [05-regras-de-negocio/inventory-dashboard.md](05-regras-de-negocio/inventory-dashboard.md).
- **Indicadores:** `IndicatorsResponseDTO` → `IndexesState` (`CircularGraph`, `VerticalBarGraphItem`, `BarGraphItem`).
- **Listagem de itens:** `PagedItemsDTO` / `ItemResponseDTO` → `ProductItem` (`ItensViewModel.productFrom`). Regras em [05-regras-de-negocio/itens.md](05-regras-de-negocio/itens.md).
- **PDI:** `ItemResponseDTO` → `ItemDetailsState` (`ItemDetailsViewModel.stateFrom`).
- **Funcionários na home:** sem DTO — o contrato não traz esse número.

## Identificadores do item (back-end v1)

Não misturar: `id` (UUID), `barcode` (físico, único por unidade), `displayCode` (6 dígitos na UI, atribuído no cadastro). O código de barras **não** é `id_produto-id_modelo-id_categoria`.
