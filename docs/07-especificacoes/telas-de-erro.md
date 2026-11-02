# Spec — Telas de erro e feedback de erro

## Metadados

- **Feature:** Telas de erro em tela cheia (conexão e erro fatal), popup de feedback para erros dentro das telas, e quando cada um deve aparecer
- **Perfil(is) envolvido(s):** Funcionário, Gestor
- **Status:** em andamento — abordagem confirmada, execução pelo [Checklist de execução](#checklist-de-execução)
- **Última atualização:** 2026-11-01

## Contexto

Hoje cada `ViewModel` trata erro no próprio `catch` e mostra `errorMessage` na tela como `CaptionText` vermelho (cerca de 25 telas). Não existe tratamento global, e nenhum fluxo leva para uma tela de erro. Vários `ViewModel`s mostram `e.message` direto, o que expõe textos como "HTTP 500 Internal Server Error" ao usuário.

Problema concreto: o `SplashViewModel` chama `boot.execute()` (`ScrapyClient.boot()`, chamada de rede) **sem `try/catch`**. Sem conexão na abertura, a exceção escapa do `viewModelScope` e o app fecha (crash).

Outro problema: nas listas paginadas, quando a próxima página falha, o `LaunchedEffect` da lista reinicia (porque `isLoadingMore` mudou), vê que o usuário continua no fim e pede a página de novo, em loop.

O que já existe:

| Peça | Arquivo |
|---|---|
| Template das telas de erro | `view/screens/error/ErrorScreenModel.kt` (`icon`, `iconColor`, `title`, `description`, `actions`) |
| Tela de conexão | `view/screens/error/ConnectionErrorScreen.kt` + `viewmodel/error/ConnectionError.kt` |
| Tela de erro fatal | `view/screens/error/FatalErrorScreen.kt` + `viewmodel/error/FatalError.kt` |
| Rotas | `Route.ConnectionError`, `Route.FatalError`, registradas no `ZeraNavHost` |
| Popup de feedback | `view/components/feedback/FeedbackPopup.kt` (ainda não usado em nenhuma tela) |
| Paginação | `onEndReached` em `ProductList`, `OptionList`, `SelectBoxList` e `AlertCentralScreen` |

## User story

Como funcionário ou gestor, quero ver uma mensagem clara quando algo dá errado (sem conexão, erro inesperado ou falha ao carregar/enviar), para entender o que aconteceu e ter uma saída, em vez de o app fechar, travar ou mostrar um código técnico.

## Regras de exibição

| Situação | Como mostrar |
|---|---|
| Validação de campo (vazio, e-mail inválido etc.) | Inline, embaixo do input (como hoje) |
| Erros de login, cadastro e recuperação de senha (SignIn, SignUp, PasswordRecovery, NewPassword) | Inline, como hoje (pode ser revisto depois) |
| Falha ao enviar formulário ou ação (400, 409, 422, 5xx) | `ErrorFeedbackPopup` |
| Falha ao carregar lista, detalhe ou dashboard | `ErrorFeedbackPopup` |
| Falta de conexão, na abertura ou durante o uso | Tela cheia `ConnectionError`, **sem** popup junto |
| Falha no boot (5xx ou configuração inválida) | Tela cheia `FatalError` |

### Texto das mensagens

- O popup de erro tem título fixo **"Algo deu errado"** e a descrição explica o que falhou, em linguagem simples (ex.: "Houve uma instabilidade e não conseguimos carregar os itens.", "Não conseguimos salvar a categoria. Tente novamente.").
- **Nunca** mostrar código HTTP, nome de exceção ou `e.message` cru ao usuário, nem no popup nem inline. Mensagens de regra de negócio já tratadas pelos helpers de cada `ViewModel` (ex.: "Já existe uma categoria com esse nome") podem ser usadas como descrição.
- Mensagens de sucesso (ex.: "Perfil atualizado.") ficam como estão; esta spec trata só de erro.

### Listas

- Falha ao carregar a primeira página ou a tela inteira: popup de erro, **sem** botão "Tentar novamente". A área da lista fica vazia, sem o texto "Nenhum ... encontrado", que seria enganoso. Para tentar de novo, o usuário sai e volta para a tela.
- Falha ao carregar a próxima página: popup de erro. A lista **não** tenta de novo sozinha; só pede a página outra vez quando o usuário sobe um pouco e desce de novo até o fim.

## Cenários (Given/When/Then)

### Cenário: abrir o app sem conexão

- **Given** o aparelho está sem internet
- **When** o usuário abre o app e a Splash executa o boot
- **Then** o app exibe `ConnectionError` (e não fecha)

### Cenário: tentar novamente após a conexão voltar (abertura)

- **Given** o usuário está em `ConnectionError`, aberta pela Splash, e a conexão voltou
- **When** toca em "Tentar novamente"
- **Then** o app volta para a Splash, refaz o boot e segue o fluxo normal (home do perfil ou Welcome)
- **And** deep links pendentes (alerta, redefinição de senha) continuam sendo tratados

### Cenário: tentar novamente ainda sem conexão

- **Given** o usuário está em `ConnectionError` e continua sem internet
- **When** toca em "Tentar novamente"
- **Then** o app checa a conexão (com loading no botão), vê que continua offline e **permanece** em `ConnectionError`, sem navegar

### Cenário: falha de servidor no boot

- **Given** há conexão, mas o boot responde 5xx ou devolve configuração inválida
- **When** a Splash executa o boot
- **Then** o app exibe `FatalError`

### Cenário: confirmar o erro fatal

- **Given** o usuário está em `FatalError`
- **When** toca em "Ok, entendi" ou no voltar do sistema
- **Then** o app fecha

### Cenário: restaurar sessão sem conexão

- **Given** o usuário tem login salvo e o boot passou
- **When** `RestoreSession` falha por falta de conexão (não por 401/403)
- **Then** o app exibe `ConnectionError` em vez de mandar para o Welcome
- **And** a sessão salva **não** é apagada (só 401/403 limpam a sessão; manter assim)

### Cenário: perder a conexão durante o uso

- **Given** o usuário está numa tela qualquer do app
- **When** uma chamada de API falha por falta de conexão (`UnknownHostException`, `ConnectException`, `SocketTimeoutException`)
- **Then** o app exibe `ConnectionError` **uma única vez**, mesmo que várias chamadas falhem ao mesmo tempo
- **And** a tela de origem **não** mostra popup de erro por essa falha

### Cenário: tentar novamente durante o uso

- **Given** o usuário está em `ConnectionError`, aberta durante o uso
- **When** toca em "Tentar novamente" ou no voltar do sistema
- **Then** o app checa a conexão antes de sair; ainda offline, continua em `ConnectionError`
- **And** com a conexão de volta, o app volta para a tela anterior, que mantém o que já estava preenchido
- **And** o usuário repete a ação na tela (a chamada que falhou não é refeita sozinha)

### Cenário: falha ao enviar formulário

- **Given** o usuário está numa tela com formulário ou ação
- **When** a API responde 400, 409, 422 ou 5xx
- **Then** o app mostra o popup "Algo deu errado" com uma descrição simples, sem código de erro
- **And** os dados preenchidos continuam no formulário

### Cenário: falha ao carregar uma lista

- **Given** o usuário abre uma tela de lista
- **When** a primeira página falha (sem ser por conexão)
- **Then** o app mostra o popup "Algo deu errado" com a descrição "Houve uma instabilidade e não conseguimos carregar os itens." (adaptada ao conteúdo da tela)

### Cenário: falha ao carregar a próxima página

- **Given** o usuário está no fim de uma lista paginada
- **When** a próxima página falha
- **Then** o app mostra o popup uma vez e **não** tenta de novo sozinho
- **When** o usuário sobe um pouco e desce de novo até o fim
- **Then** a lista pede a página outra vez

### Cenário: crash inesperado

Fora de escopo desta spec (ver [Fora de escopo](#fora-de-escopo-desta-spec)).

## Abordagem

1. **Classificador de erro:** `Throwable.isConnectionError()` em `model/remote/client/util.kt` (`UnknownHostException`, `ConnectException`, `SocketTimeoutException`). Usado pela Splash, pelo interceptor e pelos `ViewModel`s (para não mostrar popup quando a tela cheia de conexão já aparece).
2. **Splash:** `try/catch` no `boot.execute()`. Conexão → `pushAndPop(Route.ConnectionError(fromBoot = true))`; demais falhas → `pushAndPop(Route.FatalError)`. Em `navigateAfterBoot`, falha de conexão → `ConnectionError(fromBoot = true)` sem limpar a sessão; 401/403 seguem limpando e indo para o Welcome.
3. **Telas de erro:**
   - `Route.ConnectionError` vira `data class ConnectionError(val fromBoot: Boolean = false)`.
   - "Tentar novamente": primeiro checa a conexão (`NetworkStatus.isOnline`: rede ativa com `NET_CAPABILITY_INTERNET` e `NET_CAPABILITY_VALIDATED`, permissão `ACCESS_NETWORK_STATE`), com loading no botão; ainda offline, a tela continua. Online: `fromBoot = true` → `pushAndPopAll(Route.Splash)`; `fromBoot = false` → `goBack()`. O voltar do sistema faz o mesmo que "Tentar novamente" quando `fromBoot = false`; com `fromBoot = true`, fecha o app (pilha vazia, comportamento atual).
   - Texto da `ConnectionError`: trocar "Seus dados serão enviados quando a conexão voltar." (não existe fila offline) por "Verifique sua conexão com a internet e tente novamente."
   - `FatalError`: "Ok, entendi" e o voltar do sistema fecham o app (mesmo mecanismo do `shouldExitApp` da `ConnectionError`).
4. **Interceptor de conexão:** `ConnectionErrorInterceptor` compartilhado. Captura `IOException` de conexão, libera a navegação (`navigationStatus(true)`), faz `push(Route.ConnectionError())` e relança a exceção. Uma trava atômica (`ConnectionErrorGate`) garante uma tela só; ela é liberada no `onCleared` do `ConnectionErrorViewModel`. Adicionado em `AdmCoreClient` (cliente principal e `refreshClient`), `InventoryClient`, `AiClient`, `ScrapyClient` e `SupabaseStorage`. O boot também passa por ele, mas a Splash suprime o interceptor (`gate.suppress(true)`) enquanto roda o boot e o restore, e ela mesma navega com `fromBoot = true`. A trava é liberada no `onCleared` da `ConnectionError` aberta pelo interceptor.
5. **`ErrorFeedbackPopup`:** componente em `view/components/feedback/` sobre o `FeedbackPopup`, com título fixo "Algo deu errado", ícone `Exclamation` e cor `Red`; recebe só a descrição e o `onDismissRequest`. Cada `ViewModel` migrado ganha `onErrorDismiss()`, que limpa o erro.
6. **Paginação:** `ProductList`, `OptionList`, `SelectBoxList` e `AlertCentralScreen` passam a chamar `onEndReached` só na transição "longe do fim" → "perto do fim". Depois de uma página carregada com sucesso, se o usuário continua no fim, a próxima é pedida (como hoje).
7. **Migração das telas:** por área, trocando o `CaptionText` de erro (e o snackbar de erro do Perfil e da Central de alertas) pelo `ErrorFeedbackPopup`, revisando as mensagens para não expor `e.message`/códigos e ignorando erros de conexão.

## Decisões

- [x] **Escopo:** Splash, telas de erro, interceptor e popup de erro. Handler de crash fica para depois, junto com uma ferramenta de crash report.
- [x] **Conexão durante o uso:** tela cheia `ConnectionError`, uma vez só, sem popup na tela de origem.
- [x] **Interceptor vs. `NetworkCallback`:** só o interceptor para detectar a queda; o "Tentar novamente" faz uma checagem pontual com `ConnectivityManager` antes de sair da tela.
- [x] **"Tentar novamente":** só sai da tela com a conexão de volta; pela Splash quando a tela veio do boot; `goBack()` quando veio do uso.
- [x] **"Ok, entendi" (FatalError):** fecha o app.
- [x] **Botão voltar do sistema:** `ConnectionError` do uso → igual a "Tentar novamente"; `ConnectionError` do boot e `FatalError` → fecham o app.
- [x] **Modo offline:** não existe; ajustar o texto da `ConnectionError`.
- [x] **5xx durante o uso:** popup de erro na tela, não `FatalError`.
- [x] **Timeout:** `SocketTimeoutException` conta como falha de conexão.
- [x] **Erros em formulários e listas:** `ErrorFeedbackPopup` com título fixo "Algo deu errado" e descrição simples, nunca com código de erro.
- [x] **Login/cadastro/recuperação de senha:** continuam inline.
- [x] **Perfil e Central de alertas:** erros saem do snackbar e vão para o popup; mensagens de sucesso ficam no snackbar.
- [x] **Retry de listas:** sem botão; próxima página é pedida de novo quando o usuário sobe e desce até o fim; primeira página, saindo e voltando para a tela.

## Critérios de aceite

- [ ] Abrir o app sem conexão não causa crash e exibe `ConnectionError`.
- [ ] "Tentar novamente" com conexão restabelecida leva ao fluxo normal da Splash.
- [ ] Falha de conexão ao restaurar sessão não apaga a sessão salva.
- [ ] Várias falhas simultâneas exibem `ConnectionError` só uma vez, sem popup na tela de origem.
- [ ] `ConnectionError` aberta durante o uso volta para a tela anterior sem perder o formulário.
- [ ] `FatalError` fecha o app.
- [ ] Falhas de envio e de carga mostram o popup "Algo deu errado", sem código HTTP nem `e.message`.
- [ ] Validação de campo e erros de login/cadastro continuam inline.
- [ ] Falha na próxima página não entra em loop; subir e descer de novo tenta outra vez.

## Edge cases considerados

- Várias chamadas paralelas falhando ao mesmo tempo (ex.: home do gestor carrega vários cards): a trava do interceptor garante uma tela só, e os `ViewModel`s ignoram erros de conexão.
- `ZeraNavigator.navigationStatus(false)` ativo durante um carregamento: o push para a tela de erro seria ignorado. O interceptor libera a navegação antes de navegar.
- Deep links pendentes (`consumePendingAlertDeepLink`, `consumePendingPasswordResetDeepLink`) quando o boot falha: só são consumidos em `navigateAfterBoot`, então continuam pendentes ao tentar de novo pela Splash.
- Timeout (`SocketTimeoutException`) pode significar servidor lento, não falta de internet; aceito tratar como conexão.
- Upload de foto (Supabase) não passa pelo Retrofit, mas usa `OkHttpClient`; recebe o mesmo interceptor.
- Várias falhas na mesma tela (ex.: duas cargas em paralelo sem ser por conexão): o popup mostra só uma mensagem por vez (o estado guarda uma só).

## Fora de escopo desta spec

- Handler de crash (`Thread.setDefaultUncaughtExceptionHandler`) reabrindo o app em `FatalError`.
- Popups de confirmação para ações de risco (aprovar, recusar, cancelar agendamento etc.) — spec separada.
- Modo offline / fila de envio.
- Integração com ferramenta de crash report.
- Pull-to-refresh nas listas.
- Mudar os erros inline de login/cadastro para popup.

## Checklist de execução

Uma etapa por vez, com revisão antes da próxima.

1. [x] Atualizar esta spec com as decisões e o checklist
2. [x] `Throwable.isConnectionError()` + testes
3. [x] Telas de erro: `Route.ConnectionError(fromBoot)`, "Tentar novamente" e voltar do sistema, texto da `ConnectionError`, `FatalError` fechando o app
4. [x] Splash: `try/catch` no boot e separação de conexão vs. 401/403 no restore
5. [x] `ConnectionErrorInterceptor` + `ConnectionErrorGate` nos clientes HTTP + testes
6. [x] Componente `ErrorFeedbackPopup`
7. [x] Paginação sem loop em `ProductList`, `OptionList`, `SelectBoxList` e `AlertCentralScreen`
8. [x] Migração — Categorias (`CategoriesScreen`, `CategoryCreationScreen`)
9. [x] Migração — Funcionários (`EmployeesScreen`, `EmployeeDetailsScreen`, `EmployeeRegisterScreen`)
10. [x] Migração — Home do gestor e Índices (`ManagerHomeScreen`, `IndexesScreen`)
11. [x] Migração — Itens (`ItensScreen`, `EmployeeItemsScreen`, `ItemDetailsScreen`)
12. [x] Migração — Modelos (`ModelsScreen`, `ModelDetailsScreen`, `ModelItemsScreen`, `ModelCreationScreen`)
13. [x] Migração — Reciclagem (`ItensSelectionScreen`, `RecyclingResumeScreen`, `ScheduledDisposalsScreen`, `SchedulingDetailsScreen`, `SchedulingSuccessScreen`, locais próximos)
14. [x] Migração — Cadastro (`ManualRegisterScreen`, Scan)
15. [x] Migração — Chat, Central de alertas e Perfil
16. [ ] Auth: só revisar as mensagens inline (sem `e.message` nem códigos) e ignorar erros de conexão
17. [ ] Atualizar docs (`01-arquitetura.md`, `03-catalogo-componentes.md`, status desta spec)

## Referências

- Telas: `view/screens/error/`
- ViewModels: `viewmodel/error/`
- Popup: `view/components/feedback/FeedbackPopup.kt`
- Listas paginadas: `view/components/lists/ProductList.kt`, `view/components/lists/OptionList.kt`, `view/components/inputs/SelectBoxList.kt`
- Splash: `viewmodel/Splash.kt`, `model/usecase/config/Boot.kt`
- Clientes HTTP: `model/remote/client/`
- Navegação: `../01-arquitetura.md`
- Sessão: [refresh-de-sessao.md](refresh-de-sessao.md)
