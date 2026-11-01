# Spec — Telas de erro

## Metadados

- **Feature:** Telas de erro em tela cheia (conexão e erro fatal) e quando o app deve exibi-las
- **Perfil(is) envolvido(s):** Funcionário, Gestor
- **Status:** planejado — **aguardando confirmação da abordagem** (ver [Decisões pendentes](#decisões-pendentes)). Telas e rotas já existem; o disparo ainda não.
- **Última atualização:** 2026-11-01

## Contexto

Hoje cada `ViewModel` trata erro no próprio `catch` e mostra `errorMessage` na tela (32 ViewModels com `catch (e: Exception)`). Não existe tratamento global, e nenhum fluxo leva para uma tela de erro.

Problema concreto: o `SplashViewModel` chama `boot.execute()` (`ScrapyClient.boot()`, chamada de rede) **sem `try/catch`**. Sem conexão na abertura, a exceção escapa do `viewModelScope` e o app fecha (crash).

O que já existe:

| Peça | Arquivo |
|---|---|
| Template das telas | `view/screens/error/ErrorScreenModel.kt` (`icon`, `iconColor`, `title`, `description`, `actions`) |
| Tela de conexão | `view/screens/error/ConnectionErrorScreen.kt` + `viewmodel/error/ConnectionError.kt` |
| Tela de erro fatal | `view/screens/error/FatalErrorScreen.kt` + `viewmodel/error/FatalError.kt` |
| Rotas | `Route.ConnectionError`, `Route.FatalError`, registradas no `ZeraNavHost` |

Comportamento atual dos botões:

- **ConnectionError — "Tentar novamente":** `pushAndPopAll(Route.Splash)` (refaz boot e restaura a sessão).
- **ConnectionError — "Sair do aplicativo":** fecha a Activity (`state.shouldExitApp` → `activity.finish()` na tela).
- **FatalError — "Ok, entendi":** `pushAndPopAll(Route.Splash)`.

## User story

Como funcionário ou gestor, quero ver uma tela clara quando o app não consegue continuar (sem conexão ou erro inesperado), para entender o que aconteceu e ter uma saída, em vez de o app fechar ou ficar travado.

## Cenários (Given/When/Then)

### Cenário: abrir o app sem conexão

- **Given** o aparelho está sem internet
- **When** o usuário abre o app e a Splash executa o boot
- **Then** o app exibe `ConnectionError` (e não fecha)

### Cenário: tentar novamente após a conexão voltar

- **Given** o usuário está em `ConnectionError` e a conexão voltou
- **When** toca em "Tentar novamente"
- **Then** o app volta para a Splash, refaz o boot e segue o fluxo normal (home do perfil ou Welcome)

### Cenário: tentar novamente ainda sem conexão

- **Given** o usuário está em `ConnectionError` e continua sem internet
- **When** toca em "Tentar novamente"
- **Then** a Splash falha de novo no boot e volta para `ConnectionError`

### Cenário: falha de servidor no boot

- **Given** há conexão, mas o boot responde 5xx ou devolve configuração inválida
- **When** a Splash executa o boot
- **Then** o app exibe `FatalError`

### Cenário: restaurar sessão sem conexão

- **Given** o usuário tem login salvo e o boot passou
- **When** `RestoreSession` falha por falta de conexão (não por 401/403)
- **Then** o app exibe `ConnectionError` em vez de mandar para o Welcome
- **And** a sessão salva **não** é apagada (hoje só 401/403 limpam a sessão; manter assim)

### Cenário: perder a conexão durante o uso

- **Given** o usuário está numa tela qualquer do app
- **When** uma chamada de API falha por falta de conexão (`UnknownHostException`, `ConnectException`, `SocketTimeoutException`)
- **Then** o app exibe `ConnectionError` **uma única vez**, mesmo que várias chamadas falhem ao mesmo tempo

### Cenário: erro de validação ou regra de negócio

- **Given** o usuário está numa tela com formulário ou ação
- **When** a API responde 400, 409 ou 422
- **Then** o erro continua sendo mostrado na própria tela (`errorMessage`), **sem** tela de erro

### Cenário: crash inesperado

- **Given** o app está em uso
- **When** ocorre uma exceção não tratada
- **Then** o app reabre direto em `FatalError` em vez de simplesmente fechar

## Abordagem proposta (a confirmar)

Duas camadas, porque falta de conexão e crash são problemas diferentes:

1. **Splash (prioridade, corrige o crash):** envolver `boot.execute()` em `try/catch`. Falha de conexão → `pushAndPop(Route.ConnectionError)`; demais falhas → `pushAndPop(Route.FatalError)`. Em `navigateAfterBoot`, separar falha de conexão de 401/403.
2. **Conexão durante o uso — interceptor OkHttp compartilhado:** `AdmCoreClient`, `InventoryClient`, `AiClient` e `ScrapyClient` já usam `addInterceptor`. Um interceptor comum captura `IOException` de conexão, chama `ZeraNavigator.push(Route.ConnectionError)` e relança a exceção (o `catch` de cada tela continua rodando).
   - Precisa de trava para não empilhar várias `ConnectionError` (ex.: flag "já exibindo", liberada ao sair da tela).
   - Complemento possível: `ConnectivityManager.NetworkCallback` para saber o estado da rede sem depender de uma chamada falhar.
3. **Crash — `Thread.setDefaultUncaughtExceptionHandler` no `ZeraApp`:** reabrir a `MainActivity` com um extra indicando erro fatal, encerrar o processo antigo, e o `ZeraNavHost` começar em `Route.FatalError` quando o extra existir.
   - Repassar a exceção para o handler anterior (não quebrar ferramentas de crash report).
   - Evitar loop: se o crash acontecer de novo logo ao abrir, não reabrir indefinidamente.

## Decisões pendentes

> Nada abaixo foi implementado. Confirmar com o time antes de começar.

- [ ] **Escopo:** implementar as três camadas ou só a Splash por enquanto?
- [ ] **Conexão durante o uso:** sempre tela cheia, ou manter a mensagem na tela e usar `ConnectionError` só na abertura do app?
- [ ] **Interceptor vs. `NetworkCallback`:** detectar pela falha da chamada, pelo estado da rede, ou os dois?
- [ ] **"Tentar novamente":** reiniciar pela Splash (atual) ou voltar para a tela anterior (`goBack`) e refazer só a chamada que falhou?
- [ ] **"Ok, entendi" (FatalError):** reiniciar pela Splash (atual) ou fechar o app?
- [ ] **Botão voltar do sistema nas telas de erro:** hoje volta para a tela anterior. Bloquear?
- [ ] **Modo offline:** o texto da `ConnectionError` diz "Seus dados serão enviados quando a conexão voltar", mas não existe fila offline. Ajustar o texto ou planejar o modo offline?
- [ ] **5xx durante o uso:** tela `FatalError` ou mensagem na tela com "Tentar de novo"?
- [ ] **Handler de crash:** vale o risco/complexidade agora, ou deixar para depois junto com uma ferramenta de crash report?

## Critérios de aceite

- [ ] Abrir o app sem conexão não causa crash e exibe `ConnectionError`.
- [ ] "Tentar novamente" com conexão restabelecida leva ao fluxo normal da Splash.
- [ ] Falha de conexão ao restaurar sessão não apaga a sessão salva.
- [ ] Erros 400/409/422 continuam como mensagem na tela.
- [ ] Várias falhas simultâneas exibem `ConnectionError` só uma vez.
- [ ] (Se aprovado) crash não tratado reabre o app em `FatalError`, sem loop.

## Edge cases considerados

- Várias chamadas paralelas falhando ao mesmo tempo (ex.: home do gestor carrega vários cards).
- `ZeraNavigator.navigationStatus(false)` ativo durante um carregamento: comandos de navegação são ignorados, então o push para a tela de erro também seria. Liberar a navegação antes de navegar para erro.
- Deep links pendentes (`consumePendingAlertDeepLink`, `consumePendingPasswordResetDeepLink`) quando o boot falha: não devem ser perdidos ao tentar de novo.
- Timeout (`SocketTimeoutException`) pode significar servidor lento, não falta de internet.
- Falha no upload de foto (Supabase) não passa pelos clientes Retrofit.

## Fora de escopo desta spec

- Popups de confirmação para ações de risco (aprovar, recusar, cancelar agendamento etc.) — spec separada.
- Modo offline / fila de envio.
- Integração com ferramenta de crash report.

## Referências

- Telas: `view/screens/error/`
- ViewModels: `viewmodel/error/`
- Splash: `viewmodel/Splash.kt`, `model/usecase/config/Boot.kt`
- Clientes HTTP: `model/remote/client/`
- Navegação: `../01-arquitetura.md`
- Sessão: [refresh-de-sessao.md](refresh-de-sessao.md)
