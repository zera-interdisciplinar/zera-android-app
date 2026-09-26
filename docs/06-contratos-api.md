# 06 — Contratos de API

## Configuração base (ApiClient)

`model/remote/client/ApiClient.kt` é um `object` que monta uma única instância de `Retrofit` (`by lazy` nos `service`s expostos):

- **Base URL**: definida em `ApiClient.init(environments)` com `environments.admCoreApiUrl` (`ms-adm-core-url` no JSON do `POST /v1/boot` da Scrapy). HTTP puro no QA exige `android:usesCleartextTraffic="true"` no `AndroidManifest.xml`.
- **Interceptor OkHttp** aplicado a toda requisição:
  - header `apiKey` lido de `adm.core.api.key` no `local.properties` (`BuildConfig.ADM_CORE_API_KEY`).
  - header `Authorization: Bearer <accessToken>`, quando há um `accessToken` salvo em `SharedPreferencesManager` (ausente apenas na primeira tela de login). Esse Bearer é do **adm-core** (sessão do usuário), não da Scrapy.
- **Serialização**: `kotlinx.serialization.json.Json { ignoreUnknownKeys = true }` via `retrofit2-kotlinx-serialization-converter`.
- **Services expostos**: `authService` (`AuthService`), `selfUserService` (`SelfUserService`), `invitationService` (`InvitationService`).

## Scrapy (ScrapyClient)

Fonte de verdade do contrato: [contrato/contrato-scrapy-api.md](contrato/contrato-scrapy-api.md). Como o app usa hoje: [contrato/contexto.md](contrato/contexto.md).

`model/remote/client/ScrapyClient.kt` é um `object` Retrofit **separado** do `ApiClient` (a Scrapy sobe antes de existir URL do adm-core). Interface: `ScrapyService`.

- **Base URL**: `scrapy.api.url` em `local.properties` (`BuildConfig.SCRAPY_API_URL`). O client normaliza o valor (aceita o prefixo Kong sem `/v1` ou a URL direta com `/v1`) e chama `POST v1/boot` e `POST v1/flags`. Em QA o path público é `http://34.95.129.59/qa/scrapy/v1/boot` — o prefixo `/qa/scrapy` é do Kong e entra **antes** do `/v1`; o backend só registra `/v1/boot` e `/v1/flags`, então o Kong precisa stripar o prefixo do serviço. Sem esse strip, a rota cai no fallback da UI (`200` HTML). Isso é roteamento, não desvio de contrato.
- **Interceptor OkHttp**: header `apikey` com `BuildConfig.SCRAPY_API_KEY`. O contrato **não** usa `Authorization: Bearer` nessas rotas; o serviço não lê esse header.
- **Serialização**: o mesmo `Json { ignoreUnknownKeys = true }` do `ApiClient`.

| Método | Endpoint | Body | Resposta | Usado por |
|---|---|---|---|---|
| `POST` | `v1/boot` | nenhum | `Environments` (`ms-adm-core-url`) | `Boot.execute()` (splash, antes do login) |
| `POST` | `v1/flags` | `FlagsRequest(attrs)` | `JsonObject` | `LoadFlags.execute(selfUser)` (depois do login) |

`Boot.execute()` grava o resultado em `AppConfig` e chama `ApiClient.init(environments)`. `LoadFlags` manda `user_id`, `role`, `unit_id` e `app_version` em `attrs`.

Erros do contrato nessas rotas: `400` (body malformado em `/v1/flags`), `401 { "error": "missing api key" }` (header `apikey` ausente), `401 { "error": "invalid api key" }` (key inválida/revogada), `429`, `500`. Qualquer corpo não-JSON (HTML) nessas rotas indica que a chamada não chegou no handler.

Caching por `ETag` / `If-None-Match` está no contrato e **não** está implementado no client hoje.

## Autenticação (AuthService)

`model/remote/service/AuthService.kt`.

| Método | Endpoint | Body | Resposta | Usado por |
|---|---|---|---|---|
| `POST` | `auth/login` | `SingInRequestDTO(email, password)` | `SingInResponseDTO` | `SingIn.execute()` (login) |
| `POST` | `invitations/redeem` | `SingInRequestDTO(email, password)` | `SingInResponseDTO` | ainda não chamado por nenhum `usecase` |

> **Contrato em aberto:** `SignUpScreen`/`SignUpViewModel` coletam `name`, `email`, `password` e `token` (código de convite), mas `signUp()` no `AuthService` reaproveita `SingInRequestDTO`, que só tem `email`/`password` — não há campo para `name` nem para o código de convite. O formato real do payload de `invitations/redeem` precisa ser confirmado com o backend antes de implementar `SignUpViewModel.signUp()` (hoje um `TODO`). Ver também "Inconsistências conhecidas" em [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md).

## Usuário (SelfUserService)

`model/remote/service/SelfUserService.kt`.

| Método | Endpoint | Resposta | Usado por |
|---|---|---|---|
| `GET` | `users/{userId}` | `SelfUserResponseDTO` | `SingIn.execute()`, chamado logo após o login (com o `userId` retornado por `auth/login`) para descobrir o `role` e decidir a tela de destino |

## Tratamento de erros

Não há tratamento de erro estruturado hoje:

- Não existe um DTO de erro (ex.: `{ "message": ... }`) sendo desserializado — falhas de rede/HTTP chegam ao `ViewModel` como exceções genéricas do Retrofit/OkHttp (`HttpException`, `IOException`, etc.), capturadas por um `catch (e: Exception)` amplo.
- A mensagem exibida ao usuário é `e.message` diretamente — não há tradução para mensagens amigáveis em português, nem distinção entre erro de credencial inválida (401), erro de servidor (5xx) ou falta de conexão.
- Não há fluxo de **refresh de token**: embora `SingInResponseDTO` retorne um `refreshToken`, nada no código hoje o utiliza para renovar a sessão quando o `accessToken` expira — uma resposta 401 em qualquer chamada autenticada hoje resultaria no mesmo tratamento genérico de erro, não num refresh automático.

Ao adicionar tratamento de erro estruturado (DTO de erro, mensagens amigáveis, refresh de token), registre a decisão de formato em [08-decisoes-arquiteturais/](08-decisoes-arquiteturais/), já que impacta todos os `usecase`s existentes e futuros.

## Endpoints esperados (backlog, ainda não implementados)

Resumo leve dos contratos que o escopo de produto vai exigir (sem especificar payload aqui, já que dependem de definição com o backend): CRUD de categoria/modelo/produto, consulta de produto por código de barras, envio de resultado de triagem, geração e confirmação de relatório de descarte, e registro/consulta de manutenção.
