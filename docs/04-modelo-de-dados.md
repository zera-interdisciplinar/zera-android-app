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

- **`SelfUserResponseDTO(userId, name, email, role, status, unitId, createdAt, updatedAt, managerId: String?)`** — retornado por `GET users/{userId}`. `role` é uma `String` livre (valores observados: `"MANAGER"`, `"EMPLOYEE"`), não um enum Kotlin — a decisão de rota (`SingInViewModel.signIn()`) faz `when (selfUser.role) { "EMPLOYEE" -> ...; "MANAGER" -> ...; else -> erro }`. Não há um terceiro valor para Administrador (coerente com [00-contexto-geral.md](00-contexto-geral.md), que registra que esse perfil não tem fluxo no app). `unitId` é gravado em `SharedPreferences` no login e enviado como `X-Unit-Id` no `InventoryClient`.

## DTOs de inventário (dashboard)

Pacote `model/dto/inventory/`. Só os campos que as telas usam; o restante do JSON do contrato é ignorado (`ignoreUnknownKeys`). Contratos: [contrato-ms-inventory/contrato-inventory-dashboard.md](contrato-ms-inventory/contrato-inventory-dashboard.md), [contrato-pdi/contrato-detalhes-itens.md](contrato-pdi/contrato-detalhes-itens.md).

- **`DashboardHomeResponseDTO`** — `GET /api/v1/dashboard/home`: `activeItems`, `activeItemsChangePercent?`, `occupancyPercent?`, `pendingApproval`, `inMaintenance`, `awaitingEvaluation`, `recentItems`.
- **`PagedItemsDTO(content, page, size, totalElements, totalPages)`** — envelope de `recentItems` e de `GET /api/v1/items`.
- **`ItemResponseDTO`** — lista e PDI: `id`, `name`, `displayCode?`, `status?` (aliases JSON `itemStatus`/`approvalStatus`), `condition?`, `createdByName?`, `createdAt?`, `model?`.
- **`ModelResponseDTO`** / **`MaterialResponseDTO`** / **`CategoryResponseDTO`** — modelo aninhado no item; categorias também vêm de `GET /api/v1/categories` (`id`, `name`, metadados opcionais).
- **`IndicatorsResponseDTO`** — `GET /api/v1/dashboard/indicators`: `recyclingRatePercent`, `recyclingRateChangePoints?`, `monthlyWeightKg`, `weightByMaterial`.
- **`MonthlyWeightDTO(month, weightKg)`** — `month` no formato `yyyy-MM`.
- **`WeightByMaterialDTO(material, percent)`** — `material` é código (`PLASTIC`, `METAL`, ...).

Campos do contrato **não** modelados no app: `stockCapacity`, `disposalsInWindow`, `windowDays`, barcode/damages/serial da listagem, payload de work-center.

## Config dinâmica (Scrapy)

Pacotes `model/entity/config/` e `model/entity/scrapy/`. Contrato: [contrato-scrapy/contrato-scrapy-api.md](contrato-scrapy/contrato-scrapy-api.md).

- **`Environments`** — resposta do `POST /v1/boot`:
  - `admCoreApiUrl` ← `ms-adm-core-url` (base do `ApiClient`)
  - `admCoreApiKey` ← `ms-adm-core-api-key` (header `apiKey` do `ApiClient`; fallback do `InventoryClient`)
  - `inventoryApiUrl` ← `ms-inventory-url` (base do `InventoryClient`)
  - `inventoryApiKey` ← `ms-inventory-api-key` (opcional; se vazio, usa `admCoreApiKey`)
- **`FlagsRequest(attrs: Map<String, JsonElement>)`** — body do `POST /v1/flags`. `LoadFlags` preenche `user_id`, `role`, `unit_id` e `app_version`.
- **`AppConfig.flags`** — `JsonObject` com o mapa `key -> value` do `POST /v1/flags` (bool, número, string ou objeto, conforme o admin). Em memória; não vai para `SharedPreferences`.

## Persistência local (SharedPreferences)

Único mecanismo de persistência local hoje é `SharedPreferencesManager` (`model/local/SharedPreferencesManager.kt`), um `object` sobre um único arquivo de preferências (`"zera_prefs"`):

- `saveSession` / `getAccessToken` / `getRefreshToken` / `getUserId` / `getEmail` / `getPassword`
- `saveUnitId` / `getUnitId` — unidade do usuário logado, usada no header `X-Unit-Id`
- `hasSavedLogin` / `clearSession` (`clearSession` também remove `unit_id`)

Precisa ser inicializado explicitamente (`SharedPreferencesManager.init(context)`, hoje chamado em `MainActivity.onCreate`) antes de qualquer leitura/escrita — chamar antes disso lança `UninitializedPropertyAccessException`. Não há cache local de dados de domínio (perfil do usuário, inventário, etc.) — apenas sessão. Não há banco de dados local (Room ou similar) no projeto.

## Mapeamentos entre camadas

Não existe uma camada `toDomain()`. Os `ViewModels` mapeiam DTO → campos de UI no próprio arquivo:

- **Auth:** `SelfUserResponseDTO` é transiente (rota de destino, `unitId` na sessão).
- **Home do gestor:** `DashboardHomeResponseDTO` → `ManagerHomeState` (ocupação, totais, alertas, `ProductItem`). Regras em [05-regras-de-negocio/inventory-dashboard.md](05-regras-de-negocio/inventory-dashboard.md).
- **Indicadores:** `IndicatorsResponseDTO` → `IndexesState` (`CircularGraph`, `VerticalBarGraphItem`, `BarGraphItem`).
- **Listagem de itens:** `PagedItemsDTO` / `ItemResponseDTO` → `ProductItem` (`ItensViewModel.productFrom`). Regras em [05-regras-de-negocio/itens.md](05-regras-de-negocio/itens.md).
- **PDI:** `ItemResponseDTO` → `ItemDetailsState` (`ItemDetailsViewModel.stateFrom`).
- **Funcionários na home:** sem DTO — o contrato não traz esse número.

## Modelo de dados esperado (backlog, ainda não implementado)

Resumo leve do modelo de dados previsto pelo escopo de produto — sem detalhar schema aqui, já que ainda não há código correspondente:

- Hierarquia **Categoria → Modelo → Produto**, com o código de barras do produto físico codificando `id_produto-id_modelo-id_categoria`.
- Status de triagem por produto: reutilizável, aproveitável (peças/upgrade) ou descartável.
- Metadados de manutenção por produto: vida útil esperada, histórico de manutenções, desgaste estimado, frequência de uso.
- Registro de tempo de armazenamento de itens descartáveis, para os alertas de lote crítico.
- Total de funcionários da unidade (não coberto pelo Inventory Dashboard).
