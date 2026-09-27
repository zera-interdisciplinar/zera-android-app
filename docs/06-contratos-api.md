# 06 — Contratos de API

## Configuração base (ApiClient)

`model/remote/client/ApiClient.kt` é um `object` que monta uma única instância de `Retrofit` (`by lazy` nos `service`s expostos):

- **Base URL**: definida em `ApiClient.init(environments)` com `environments.admCoreApiUrl` (`ms-adm-core-url` no JSON do `POST /v1/boot` da Scrapy). HTTP puro no QA exige `android:usesCleartextTraffic="true"` no `AndroidManifest.xml`.
- **Interceptor OkHttp** aplicado a toda requisição:
  - header `apiKey` com `environments.admCoreApiKey` (`ms-adm-core-api-key` no boot). Sem esse header o Kong responde 401.
  - header `Authorization: Bearer <accessToken>`, quando há um `accessToken` salvo em `SharedPreferencesManager` (ausente apenas na primeira tela de login). Esse Bearer é do **adm-core** (sessão do usuário), não da Scrapy.
- **Serialização**: `kotlinx.serialization.json.Json { ignoreUnknownKeys = true }` via `retrofit2-kotlinx-serialization-converter`.
- **Services expostos**: `authService` (`AuthService`), `selfUserService` (`SelfUserService`), `invitationService` (`InvitationService`).

## Scrapy (ScrapyClient)

Fonte de verdade do contrato: [contrato-scrapy/contrato-scrapy-api.md](contrato-scrapy/contrato-scrapy-api.md). Como o app usa hoje: [contrato-scrapy/contexto.md](contrato-scrapy/contexto.md).

`model/remote/client/ScrapyClient.kt` é um `object` Retrofit **separado** do `ApiClient` (a Scrapy sobe antes de existir URL do adm-core). Interface: `ScrapyService`.

- **Base URL**: `scrapy.api.url` em `local.properties` (`BuildConfig.SCRAPY_API_URL`). O client normaliza o valor (aceita o prefixo Kong sem `/v1` ou a URL direta com `/v1`) e chama `POST v1/boot` e `POST v1/flags`. Em QA o path público é `http://34.95.129.59/qa/scrapy/v1/boot` — o prefixo `/qa/scrapy` é do Kong e entra **antes** do `/v1`; o backend só registra `/v1/boot` e `/v1/flags`, então o Kong precisa stripar o prefixo do serviço. Sem esse strip, a rota cai no fallback da UI (`200` HTML). Isso é roteamento, não desvio de contrato.
- **Interceptor OkHttp**: header `apikey` com `BuildConfig.SCRAPY_API_KEY`. O contrato **não** usa `Authorization: Bearer` nessas rotas; o serviço não lê esse header.
- **Serialização**: o mesmo `Json { ignoreUnknownKeys = true }` do `ApiClient`.

| Método | Endpoint | Body | Resposta | Usado por |
|---|---|---|---|---|
| `POST` | `v1/boot` | nenhum | `Environments` (`ms-adm-core-url`, `ms-adm-core-api-key`, `ms-inventory-url`, `ms-inventory-api-key` opcional) | `Boot.execute()` (splash, antes do login) |
| `POST` | `v1/flags` | `FlagsRequest(attrs)` | `JsonObject` | `LoadFlags.execute(selfUser)` (depois do login) |

`Boot.execute()` grava o resultado em `AppConfig` e chama `ApiClient.init(environments)` e `InventoryClient.init(environments)`. `LoadFlags` manda `user_id`, `role`, `unit_id` e `app_version` em `attrs`.

Erros do contrato nessas rotas: `400` (body malformado em `/v1/flags`), `401 { "error": "missing api key" }` (header `apikey` ausente), `401 { "error": "invalid api key" }` (key inválida/revogada), `429`, `500`. Qualquer corpo não-JSON (HTML) nessas rotas indica que a chamada não chegou no handler.

Caching por `ETag` / `If-None-Match` está no contrato e **não** está implementado no client hoje.

## Inventário (InventoryClient)

Fonte de verdade do dashboard: [contrato-ms-inventory/contrato-inventory-dashboard.md](contrato-ms-inventory/contrato-inventory-dashboard.md) ([contexto](contrato-ms-inventory/contexto.md)). Catálogo e PDI: [contrato-pdi/contrato-detalhes-itens.md](contrato-pdi/contrato-detalhes-itens.md) ([contexto](contrato-pdi/contexto.md)).

`model/remote/client/InventoryClient.kt` é um `object` Retrofit **separado** (URL própria do boot). Interface: `InventoryService`.

- **Base URL**: `environments.inventoryApiUrl` (`ms-inventory-url`). Se o valor terminar em `/api/v1`, o client remove esse sufixo para não duplicar o path das rotas Retrofit (`api/v1/dashboard/...`).
- **Interceptor OkHttp**:
  - `apiKey`: `ms-inventory-api-key`, ou `ms-adm-core-api-key` se a key de inventory vier em branco. Sem `apiKey` o Kong responde **401**.
  - `Authorization: Bearer <accessToken>` do usuário logado.
  - `X-Unit-Id`: `SharedPreferencesManager.getUnitId()` (gravado no login). Ausente → **400** no backend.
- **Serialização**: o mesmo `Json { ignoreUnknownKeys = true }`.

| Método | Endpoint | Query | Resposta | Usado por |
|---|---|---|---|---|
| `GET` | `api/v1/dashboard/home` | `page` (default 0), `size` (default 5) | `DashboardHomeResponseDTO` | `GetManagerHome.execute()` (`ManagerHomeViewModel`) |
| `GET` | `api/v1/dashboard/indicators` | `from`/`to` opcionais (`yyyy-MM-dd`) | `IndicatorsResponseDTO` | `GetIndicators.execute()` (`IndexesViewModel`) |
| `GET` | `api/v1/items` | `status`, `categoryId`, `q`, `page`, `size` (default 20) | `PagedItemsDTO` | `GetItems.execute()` (`ItensViewModel`) |
| `GET` | `api/v1/items/{id}` | path `id` | `ItemResponseDTO` | `GetItemDetails.execute()` (`ItemDetailsViewModel`) |
| `GET` | `api/v1/categories` | — | `List<CategoryResponseDTO>` | `GetCategories.execute()` (`ItensViewModel`) |
| `POST` | `api/v1/items/{id}/approve` | path `id`, sem corpo | `ItemResponseDTO` | `ApproveItem.execute()` (`ItemDetailsViewModel`) |
| `POST` | `api/v1/items/{id}/reject` | path `id`, `{ "reason" }` | `ItemResponseDTO` | `RejectItem.execute()` (`ItemDetailsViewModel`) |
| `PATCH` | `api/v1/items/{id}` | parcial: `name`, `condition`, `hasDamages`, `damages`, `notes`, `serialNumber`, `acquiredAt`, `manufacturingYear`, `usageIntensity` | `ItemResponseDTO` | `UpdateItem.execute()` (`ItemDetailsViewModel`; a PDI só envia `name` ou `condition`) |

`GET /api/v1/dashboard/work-center` existe no contrato e **não** tem método no `InventoryService`.

## Autenticação (AuthService)

`model/remote/service/AuthService.kt`.

| Método | Endpoint | Body | Resposta | Usado por |
|---|---|---|---|---|
| `POST` | `auth/login` | `SingInRequestDTO(email, password)` | `SingInResponseDTO` | `SingIn.execute()` (login) |

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

## Tratamento de erros

Não há tratamento de erro estruturado hoje:

- Não existe um DTO de erro (ex.: `{ "message": ... }` ou RFC 7807 `ProblemDetail` do inventory) sendo desserializado — falhas de rede/HTTP chegam ao `ViewModel` como exceções genéricas do Retrofit/OkHttp (`HttpException`, `IOException`, etc.), capturadas por um `catch (e: Exception)` amplo.
- Na home e nos indicadores a mensagem exibida é `e.message` diretamente. Cadastro já traduz alguns HTTP codes.
- Não há fluxo de **refresh de token**: embora `SingInResponseDTO` retorne um `refreshToken`, nada no código hoje o utiliza para renovar a sessão quando o `accessToken` expira — uma resposta 401 em qualquer chamada autenticada hoje resultaria no mesmo tratamento genérico de erro, não num refresh automático.

Ao adicionar tratamento de erro estruturado (DTO de erro, mensagens amigáveis, refresh de token), registre a decisão de formato em [08-decisoes-arquiteturais/](08-decisoes-arquiteturais/), já que impacta todos os `usecase`s existentes e futuros.

## Endpoints esperados (backlog, ainda não implementados)

Resumo leve dos contratos que o escopo de produto vai exigir (sem especificar payload aqui, já que dependem de definição com o backend): CRUD de categoria/modelo/produto, consulta de produto por código de barras, envio de resultado de triagem, geração e confirmação de relatório de descarte, registro/consulta de manutenção, e total de funcionários da unidade.
