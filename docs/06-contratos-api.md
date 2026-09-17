# 06 — Contratos de API

## Configuração base (ApiClient)

`model/remote/client/ApiClient.kt` é um `object` que monta uma única instância de `Retrofit` (`by lazy` nos `service`s expostos):

- **Base URL** (ambiente QA): `http://35.247.253.238/qa/administrative/api/v1/` — HTTP puro, por isso o `AndroidManifest.xml` declara `android:usesCleartextTraffic="true"`.
- **Interceptor OkHttp** aplicado a toda requisição:
  - header `apiKey` fixo (`""`) — valor hardcoded no código-fonte, não em config/BuildConfig. Considerar mover para um mecanismo de configuração por ambiente antes de builds de produção.
  - header `Authorization: Bearer <accessToken>`, quando há um `accessToken` salvo em `SharedPreferencesManager` (ausente apenas na primeira tela de login).
- **Serialização**: `kotlinx.serialization.json.Json { ignoreUnknownKeys = true }` via `retrofit2-kotlinx-serialization-converter`.
- **Services expostos**: `authService` (`AuthService`), `selfUserService` (`SelfUserService`).

## Autenticação (AuthService)

`model/remote/service/AuthService.kt`.

| Método | Endpoint | Body | Resposta | Usado por |
|---|---|---|---|---|
| `POST` | `auth/login` | `SingInRequestDTO(email, password)` | `SingInResponseDTO` | `SingIn.execute()` (login) |
| `POST` | `invitations/redeem` | `SingInRequestDTO(email, password)` | `SingInResponseDTO` | ainda não chamado por nenhum `usecase` |

> **Contrato em aberto:** `SignUpScreen`/`SignUpViewModel` coletam `name`, `email`, `password` e `token` (código de convite), mas `signUp()` no `AuthService` reaproveita `SingInRequestDTO`, que só tem `email`/`password` — não há campo para `name` nem para o código de convite. O formato real do payload de `invitations/redeem` precisa ser confirmado com o backend antes de implementar `SignUpViewModel.signUp()` (hoje um `TODO`). Ver também [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md#inconsistências-conhecidas).

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
