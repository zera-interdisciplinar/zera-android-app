# 06 — Contratos de API

## Configuração base (AdmCoreClient)

`model/remote/client/AdmCoreClient.kt` monta o Retrofit do **ms-administrative-core**:

- **Base URL**: `AdmCoreClient.init(environments)` com `environments.admCoreApiUrl` (`ms-adm-core-url` no `POST /v1/boot`). HTTP puro no QA exige `android:usesCleartextTraffic="true"`.
- **Interceptor**: `apiKey` (`ms-adm-core-api-key`); `Authorization: Bearer` do access token em `SqliteManager` (exceto login/refresh).
- **401**: `TokenRefreshAuthenticator` chama `POST auth/refresh` e repete a request. Ver [07-especificacoes/refresh-de-sessao.md](07-especificacoes/refresh-de-sessao.md).
- **Serialização**: `Json { ignoreUnknownKeys = true }`.
- **Services**: auth, self user, invitations, users, notifications, recycling places, recyclings, telephone.

Fonte adm-core: [Confluence página 5](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55902210).

## Scrapy (ScrapyClient)

Fonte de verdade do contrato: [contrato-scrapy/contrato-scrapy-api.md](contrato-scrapy/contrato-scrapy-api.md). Como o app usa hoje: [contrato-scrapy/contexto.md](contrato-scrapy/contexto.md).

`model/remote/client/ScrapyClient.kt` é um Retrofit **separado** do `AdmCoreClient` (a Scrapy sobe antes de existir URL do adm-core). Interface: `ScrapyService`.

- **Base URL**: `scrapy.api.url` em `local.properties` (`BuildConfig.SCRAPY_API_URL`). O client normaliza o valor (aceita o prefixo Kong sem `/v1` ou a URL direta com `/v1`) e chama `POST v1/boot` e `POST v1/flags`. Em QA o path público é `http://34.95.129.59/qa/scrapy/v1/boot` — o prefixo `/qa/scrapy` é do Kong e entra **antes** do `/v1`; o backend só registra `/v1/boot` e `/v1/flags`, então o Kong precisa stripar o prefixo do serviço. Sem esse strip, a rota cai no fallback da UI (`200` HTML). Isso é roteamento, não desvio de contrato.
- **Interceptor OkHttp**: header `apikey` com `BuildConfig.SCRAPY_API_KEY`. O contrato **não** usa `Authorization: Bearer` nessas rotas; o serviço não lê esse header.
- **Serialização**: o mesmo `Json { ignoreUnknownKeys = true }` do `AdmCoreClient`.

| Método | Endpoint | Body | Resposta | Usado por |
|---|---|---|---|---|
| `POST` | `v1/boot` | nenhum | `Environments` (`ms-adm-core-url`, `ms-adm-core-api-key`, `ms-inventory-url`, `ms-inventory-api-key` opcional) | `Boot.execute()` (splash, antes do login) |
| `POST` | `v1/flags` | `FlagsRequest(attrs)` | `JsonObject` | `LoadFlags.execute(selfUser)` (depois do login) |

`Boot.execute()` grava o resultado em `AppConfig` e chama `AdmCoreClient.init(environments)` e `InventoryClient.init(environments)`. `LoadFlags` manda `user_id`, `role`, `unit_id` e `app_version` em `attrs`.

Erros do contrato nessas rotas: `400` (body malformado em `/v1/flags`), `401 { "error": "missing api key" }` (header `apikey` ausente), `401 { "error": "invalid api key" }` (key inválida/revogada), `429`, `500`. Qualquer corpo não-JSON (HTML) nessas rotas indica que a chamada não chegou no handler.

Caching por `ETag` / `If-None-Match` está no contrato e **não** está implementado no client hoje.

## Inventário (InventoryClient)

Fonte: [Confluence ms-inventory](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55869441). Dashboard no app: [contrato-dashboard/contrato-inventory-dashboard.md](contrato-dashboard/contrato-inventory-dashboard.md) ([contexto](contrato-dashboard/contexto.md)). Catálogo/PDI: [contrato-pdi/contrato-detalhes-itens.md](contrato-pdi/contrato-detalhes-itens.md). Descartes: [contrato-places-api/contrato-descarte.md](contrato-places-api/contrato-descarte.md).

`model/remote/client/InventoryClient.kt` é um `object` Retrofit **separado** (URL própria do boot). Interface: `InventoryService`.

- **Base URL**: `environments.inventoryApiUrl` (`ms-inventory-url`). Se o valor terminar em `/api/v1`, o client remove esse sufixo para não duplicar o path das rotas Retrofit (`api/v1/dashboard/...`).
- **Interceptor OkHttp**:
  - `apiKey`: `ms-inventory-api-key`, ou `ms-adm-core-api-key` se a key de inventory vier em branco. Sem `apiKey` o Kong responde **401**.
  - `Authorization: Bearer <accessToken>` do usuário logado.
  - `X-Unit-Id`: `SqliteManager.getUnitId()` (login). Ausente → **400**. O `unitId` **nunca** vai no corpo.
- **Serialização**: o mesmo `Json { ignoreUnknownKeys = true }`.

| Método | Endpoint | Query | Resposta | Usado por |
|---|---|---|---|---|
| `GET` | `api/v1/dashboard/home` | `page` (default 0), `size` (default 5) | `DashboardHomeResponseDTO` | `GetManagerHome.execute()` (`ManagerHomeViewModel`), `GetEmployeeHome.execute()` (`EmployeeHomeViewModel`) |
| `GET` | `api/v1/dashboard/work-center` | — | `WorkCenterResponseDTO` | `GetEmployeePendencies.execute()` (`EmployeeHomeViewModel`, `WorkingCentralViewModel`) |
| `GET` | `api/v1/dashboard/indicators` | `from`/`to` opcionais (`yyyy-MM-dd`) | `IndicatorsResponseDTO` | `GetIndicators.execute()` (`IndexesViewModel`) |
| `GET` | `api/v1/dashboard/by-category` | — | `DashboardByCategoryResponseDTO` | `GetDashboardByCategory.execute()` (`IndexesViewModel`) |
| `GET` | `api/v1/items` | `status` (omitir = **ativos**, não “todos”), `categoryId`, `modelId`, `q` (o app manda `q`; o texto Confluence às vezes diz `query`), `page`, `size` | `PagedItemsDTO` | `GetItems.execute()` (`ItensViewModel`), `GetModelItems.execute()` (`ModelDetailsViewModel`) |
| `GET` | `api/v1/items/{id}` | path `id` | `ItemResponseDTO` | `GetItemDetails.execute()` (`ItemDetailsViewModel`) |
| `GET` | `api/v1/items/by-barcode/{barcode}` | path `barcode` | `ItemResponseDTO` | `GetItemByBarcode.execute()` (`ScanViewModel`) |
| `POST` | `api/v1/items` | `name`, `modelId`, `barcode?`, `serialNumber`, `condition`, `hasDamages`, `damages`, `notes?`, `acquiredAt`, `manufacturingYear`, `usageIntensity` | `ItemResponseDTO` | `CreateItem.execute()` (`ManualRegisterViewModel`) |
| `GET` | `api/v1/models` | `q`, `page`, `size` (default 20) | `PagedModelsDTO` | `GetModels.execute()` (`ModelsViewModel`, `ManualRegisterViewModel`) |
| `GET` | `api/v1/models/{id}` | path `id` | `ModelResponseDTO` | `GetModel.execute()` (`ModelDetailsViewModel`) |
| `GET` | `api/v1/models/{id}/items` | path `id`, `page`, `size` | `PagedItemsDTO` | `GetModelItems.execute()` (`ModelDetailsViewModel`; fallback `GET /items?modelId=&status=PENDING_APPROVAL`) |
| `PATCH` | `api/v1/models/{id}/name` | `{ "name" }` | `ModelResponseDTO` | `UpdateModel.rename()` |
| `PATCH` | `api/v1/models/{id}/manufacturer` | `{ "manufacturer" }` | `ModelResponseDTO` | `UpdateModel.manufacturer()` |
| `PATCH` | `api/v1/models/{id}/materials` | `{ "materials": [code] }` | `ModelResponseDTO` | `UpdateModel.materials()` |
| `GET` | `api/v1/categories` | — | `List<CategoryResponseDTO>` | `GetCategories.execute()` (`ItensViewModel`, `CategoriesViewModel`, `ModelCreationViewModel`) |
| `POST` | `api/v1/categories` | `name`, `description` opcional. `unitId` vai no header, não no corpo | `CategoryResponseDTO` | `CreateCategory.execute()` (`CategoryCreationViewModel`) |
| `POST` | `api/v1/items/{id}/approve` | path `id`, sem corpo | `ItemResponseDTO` | `ApproveItem.execute()` (`ItemDetailsViewModel`, `ModelDetailsViewModel` — cascata no modelo) |
| `POST` | `api/v1/items/{id}/reject` | path `id`, `{ "reason" }` | `ItemResponseDTO` | `RejectItem.execute()` (`ItemDetailsViewModel`, `ModelDetailsViewModel`) |
| `PATCH` | `api/v1/items/{id}` | parcial: `name`, `condition`, `hasDamages`, `damages`, `notes`, `serialNumber`, `acquiredAt`, `manufacturingYear`, `usageIntensity` | `ItemResponseDTO` | `UpdateItem.execute()` (`ItemDetailsViewModel`; a PDI só envia `name` ou `condition`) |

`GET /api/v1/dashboard/home` alimenta ocupação e `recentItems` na home do Gestor e na home do Operário. `GET /api/v1/dashboard/work-center` alimenta a seção Pendências da home (resumo por bucket) e a Central de Trabalho (um card por item). O recorte do work-center é o usuário do token (sem query).

## Autenticação (AuthService)

`model/remote/service/AuthService.kt`. JWT **RS256** emitido só pelo adm-core; o inventory só valida.

| Método | Endpoint | Body | Resposta | Usado por |
|---|---|---|---|---|
| `POST` | `auth/login` | `SingInRequestDTO(email, password)` | `SingInResponseDTO` | `SingIn.execute()` |
| `POST` | `auth/refresh` | `{ refreshToken }` | mesmo formato do login | `RefreshSession` / `TokenRefreshAuthenticator` |
| `POST` | `auth/logout` | — | — | `SignOut.execute()` (logout no perfil) |

`auth/logout` revoga o refresh token e é autenticado: leva `Authorization: Bearer`, mas um `401` nele não dispara refresh nem re-login (`isCredentialRequest`). `SignOut` desregistra o device token, chama o logout (timeout de 5s, falhas ignoradas) e só então limpa a sessão local (`SingIn.clearLocalSession`). Expiração de sessão, Splash e falhas de login continuam só com `clearSession()`, sem revogar.
| `POST` | `auth/password-reset/request` | `PasswordResetRequestDTO(email)` | `202` sem corpo, exista a conta ou não | `RequestPasswordReset` (`PasswordRecoveryEmailViewModel`) |
| `POST` | `auth/password-reset/confirm` | `PasswordResetConfirmDTO(token, newPassword)` | `204` sem corpo; token inválido/usado/expirado → `400` (sem distinguir o motivo) | `ConfirmPasswordReset` (`NewPasswordViewModel`) |

Os dois endpoints de recuperação de senha são públicos (não enviam `Authorization`). O token vem no e-mail como deep link `zera://reset-password?token=<token>` (validade de 30 min, uso único); a confirmação revoga os refresh tokens do usuário, então o login é um passo à parte.

## Convite (InvitationService)

`model/remote/service/InvitationService.kt`.

| Método | Endpoint | Body | Resposta | Usado por |
|---|---|---|---|---|
| `POST` | `invitations/redeem` | `RedeemRequestDTO(code, name, email, rawPassword)` | `RedeemResponseDTO` | `InvitationUseCase.execute()` (`SignUpViewModel.signUp()`) |

Depois do redeem, o cadastro chama `SingIn.execute` para persistir tokens, `unitId` e flags.

## Usuário (SelfUserService)

`model/remote/service/SelfUserService.kt`.

| Método | Endpoint | Resposta | Usado por |
|---|---|---|---|
| `GET` | `users/{userId}` | `SelfUserResponseDTO` | `GetSelfUser` — no login (`SingIn.execute`) para `role` + `unitId`; na home do gestor, para nome/papel |
| `POST` | `users/me/device-tokens` | `{ token, platform: "ANDROID" }` | `RegisterDeviceToken` — login e `onNewToken` |
| `DELETE` | `users/me/device-tokens` | `{ token }` | `UnregisterDeviceToken` — `SingIn.clearSession` |

## Tratamento de erros

Inventory/adm-core usam RFC 9457 `ProblemDetail`. O app em geral ainda não desserializa esse corpo: `HttpException`/`IOException` no ViewModel. `409` em item = transição inválida (recarregar). `422` no submit traz `missingFields`.

Refresh no 401: [refresh-de-sessao.md](07-especificacoes/refresh-de-sessao.md). `403` de papel não dispara refresh.

## O que o Confluence descreve e o app ainda não chama

Não é backlog de produto inventado: a API v1 já tem, o app pode não ter tela. Exemplos: `POST /items/{id}/submit`, foto, manutenção, `GET /materials`, `GET /unit-settings`, regras `/rules`, `GET /disposals/{id}/report`, analytics do adm-core. Não documentar `POST /users` — usuário entra só por convite.
