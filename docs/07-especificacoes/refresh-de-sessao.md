# Spec — Refresh de sessão

## Metadados

- **Feature:** Renovação de access token via refresh
- **Perfil(is) envolvido(s):** Funcionário, Gestor
- **Status:** planejado
- **Última atualização:** 2026-10-08

## Contexto

O login (`POST auth/login`) devolve `accessToken`, `refreshToken`, `tokenType` e `expiresIn`. `SingIn` grava access, refresh, `userId`, e-mail e senha em `SqliteManager` (`zera.db`, tabela `session`). `expiresIn` não é persistido.

Nas chamadas seguintes, `ApiClient`, `InventoryClient` e `AiClient` só leem o access token e colocam `Authorization: Bearer`. O refresh token fica parado. `ScrapyClient` não usa JWT de usuário (só `apikey`).

A sessão “restaurada” no splash não reutiliza token: `RestoreSession` relê e-mail e senha e chama `auth/login` de novo. `401`/`403` nessa chamada apagam a sessão e mandam para `Route.Welcome`. Nas outras telas, `401` cai no `catch` genérico do ViewModel e vira mensagem de erro. A request original não é repetida.

## Problema

O access token expira e o app não tem como trocá-lo. O refresh token já está no SQLite e não participa de nenhum fluxo. O usuário autenticado leva erro de sessão (ou é deslogado no splash) mesmo com refresh válido. A única renovação hoje é um login completo com a senha guardada no banco.

## Solução

Renovar só quando uma chamada autenticada receber **401** e a request original já levava `Authorization: Bearer`.

1. Um único refresh em voo (`Mutex`): várias requests 401 ao mesmo tempo esperam o mesmo `POST` e repetem com o access novo.
2. O refresh chama o adm-core, não o inventory nem a IA.
3. Contrato adotado por esta spec (mesmo base URL do `AuthService`, que já usa `auth/login`):
   - `POST auth/refresh`
   - header `apiKey` (o do adm-core). Sem `Authorization`.
   - body `{ "refreshToken": "<valor em SQLite>" }`
   - `200` com o mesmo formato de `SingInResponseDTO` (`accessToken`, `refreshToken`, `tokenType`, `expiresIn`, `userId`)
4. Sucesso: gravar `accessToken` e `refreshToken` do `SingInResponseDTO` no SQLite, na mesma transação. Repetir a request original **uma vez** com o access novo. `userId`, `unitId` e e-mail não mudam.
5. Refresh com `401` ou `403`: `clearSession()` e `ZeraNavigator.pushAndPop(Route.Welcome)`. Uma única navegação, mesmo com várias requests esperando.
6. Falha de rede no refresh: não apagar a sessão. A request original falha como hoje (o ViewModel mostra o erro).
7. Segundo `401` depois do retry: não chamar refresh de novo. Tratar como falha de refresh (`401`): limpar sessão e ir para Welcome.
8. Splash: se existir `refresh_token`, restaurar com o refresh e seguir o pós-login de hoje (`GetSelfUser`, `saveUnitId`, `LoadFlags`). Não chamar `auth/login` com a senha. `401`/`403` no refresh limpam a sessão. Outro erro mantém o SQLite e vai para Welcome, igual ao splash atual fora de `401`/`403`.
9. Instalação antiga sem `refresh_token` e com e-mail/senha: o splash continua no login com senha **uma vez**. O `200` grava o refresh e os próximos boots usam o passo 8.

`403` na request de negócio não dispara refresh. É falta de permissão, não token vencido. `ScrapyClient` fica fora: `401` lá é `apikey`.

## Mudança/arquitetura

Camada `model/`. Nenhuma tela nova. ViewModels de feature não ganham lógica de refresh: o OkHttp repete a call e o `usecase` devolve o resultado da repetição.

| Peça | Papel |
|---|---|
| `AuthService.refresh` | `POST auth/refresh` |
| `RefreshSession` (`model/usecase/auth`) | Lê o refresh no SQLite, chama o endpoint, grava o par novo. Não manda Bearer. |
| `TokenRefreshAuthenticator` (`model/remote/client`) | `okhttp3.Authenticator` compartilhado por `ApiClient`, `InventoryClient` e `AiClient`. No `401` com Bearer, chama `RefreshSession` e devolve a request com o access novo. Ignora `auth/login` e `auth/refresh`. |
| Cliente HTTP do refresh | OkHttp/Retrofit **sem** esse authenticator, para o `401` do próprio refresh não entrar em loop. |
| `SqliteManager` | Método que atualiza só `access_token` e `refresh_token`, sem regravar senha. |
| `RestoreSession` | Prefere refresh. Login com senha só no legado sem refresh token. `hasSavedLogin()` passa a ser “tem refresh token” **ou** (e-mail e senha). |
| `SplashViewModel` | Mantém o destino por `role` e a limpeza em falha de auth. A diferença é qual chamada o `RestoreSession` faz. |

O authenticator é o ponto único de retry. Interceptor que lê o body de erro em cada client duplicaria a regra em três bases URL.

`expiresIn` continua sem timer. A renovação é reativa ao `401`, como pedido.

## DOD

- [ ] `POST auth/refresh` existe em `AuthService` e o `200` atualiza `access_token` e `refresh_token` no SQLite.
- [ ] `401` com Bearer em `ApiClient`, `InventoryClient` ou `AiClient` dispara um refresh e repete a request uma vez com o access novo, sem o ViewModel saber.
- [ ] Requests paralelas com `401` geram um único `POST auth/refresh`.
- [ ] `401`/`403` do refresh limpa a sessão e navega para `Route.Welcome` uma vez.
- [ ] Erro de rede no refresh não apaga o SQLite.
- [ ] `403` de regra de negócio, `auth/login` e `ScrapyClient` não disparam refresh.
- [ ] Splash com `refresh_token` não envia a senha. Sem refresh token e com senha salva, faz login uma vez e passa a ter refresh.
- [ ] Teste de unidade do authenticator/coordenador: `401` → um refresh → retry; segundo `401` não chama refresh de novo; ausência de refresh token não chama a API; falha de auth no refresh pede `clearSession`.

## Fora de escopo (OOS)

- Refresh antes de expirar (`expiresIn`, `WorkManager`, relógio local).
- Criptografar o SQLite ou mover o token para Android Keystore.
- Apagar a chave `password` das instalações que já a gravaram, e qualquer tela de “esqueci a senha”.
- DTO de erro padronizado e mensagens amigáveis (o `06-contratos-api.md` trata isso à parte).
- Tratar `401` de `apiKey` do Kong como caso distinto: se o refresh também voltar `401`, a sessão cai. Boot com `apiKey` válida é a premissa.
- Logout explícito na UI. `clearSession()` já existe; esta spec só o usa quando o refresh morre.

## Referências

- Login atual: [login.md](login.md) (refresh automático listado como fora de escopo de lá)
- Contrato e o gap de hoje: [../06-contratos-api.md](../06-contratos-api.md), seções Autenticação e Tratamento de erros
- Persistência: `SqliteManager.saveSession` / `getRefreshToken`
