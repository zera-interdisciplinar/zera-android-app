# 02 — Padrões e Convenções

## Convenções de nomenclatura

- **Pacotes**: `com.zera.android.<camada>.<subpacote>`, camada primeiro (`model`, `viewmodel`, `view`), domínio depois (`auth`, `manager`, `employee`, `user`). Ver estrutura completa em [01-arquitetura.md](01-arquitetura.md).
- **DTOs**: sufixo `DTO`, `data class` anotada `@Serializable` (kotlinx.serialization). Ex.: `SingInRequestDTO`, `SelfUserResponseDTO`.
- **Casos de uso**: nome no infinitivo/ação, sem sufixo (`SingIn`, não `SingInUseCase`), com um único ponto de entrada `suspend fun execute(...)`.
- **ViewModels**: arquivo nomeado pela feature (`viewmodel/auth/SignIn.kt`), classe com sufixo `ViewModel` (`SingInViewModel`). O nome do arquivo não repete o sufixo.
- **State**: `data class <Feature>State` com todos os campos tendo valor padrão (strings vazias, `false`, `null`, `emptyList()`), permitindo instanciar o `ViewModel`/`State` sem argumentos (útil para `@Preview`).
- **Screens**: `view/screens/<domínio>/<Feature>Screen.kt`, função `@Composable fun <Feature>Screen(viewModel: <Feature>ViewModel = viewModel())`. Telas sem domínio específico ficam soltas em `view/screens/` (ex.: `SplashScreen`).
- **Rotas**: `sealed interface Route` com `data object` `@Serializable`, nome curto sem sufixo `Screen`/`Route` (`Route.SignIn`, não `Route.SignInScreen`). Grupos de rotas por perfil são marcados por comentário (`// ROTAS DE GESTOR`, `// ROTAS DE OPERARIO`).
- **Componentes do Design System**: prefixo `Zera` reservado para os componentes mais "fundamentais"/parametrizáveis por família de cor (`ZeraButton`, `ZeraBox`, `ZeraTextInput`, `ZeraTokenInput`, `ZeraChipsGroup`, `ZeraIcon`). Componentes compostos de mais alto nível não usam o prefixo (`IconButton`, `Notification`, `Tag`, `ShortcutCard`, `StockOccupationCard`, `ProductList`, `NotificationList`, `UpperNavBar`, `BottomNavBar`, `ProgressBar`). Ao criar um componente novo, siga esse critério (fundamentação/reuso amplo → prefixo `Zera`; composição específica → sem prefixo) e documente a escolha em [03-catalogo-componentes.md](03-catalogo-componentes.md).
- **Textos**: sufixo `Text` mapeando direto para um papel do type scale Material 3 (`HeadlineText`, `TitleText`, `SubtitleText`, `BodyText`, `LabelText`, `CaptionText`, `OverlineText`).
- **Enums de variante visual**: usados para família de cor e hierarquia visual (`ZeraColorFamily`, `ZeraButtonType`, `ZeraBoxType`, `ZeraInputType`), sempre como parâmetro `style`/`type` com valor padrão.
- **Previews**: função privada, sufixo `Preview`, mesmo arquivo do componente, sempre envolvida em `ZeraTheme { ... }`.

## Estrutura de pastas por camada/feature

Regra ao adicionar um domínio novo (ex.: "inventário"):

- `model/entity/inventario/` — DTOs de rede desse domínio.
- `model/usecase/inventario/` — casos de uso desse domínio.
- `viewmodel/inventario/` — um `ViewModel` por tela/feature do domínio.
- `view/screens/inventario/` — telas Compose do domínio.

Componentes reutilizáveis (usados por mais de um domínio) vão em `view/components/<família>` — famílias hoje existentes: `buttons`, `cards`, `containers`, `images`, `inputs`, `lists`, `logo`, `navigation`, `outros`, `overlays`, `progressbars`, `texts`. `outros` é um bucket genérico (hoje contém `Tag` e `SplashBackground`); antes de adicionar um terceiro componente ali, avalie se ele não define uma família própria (ex.: um componente de status poderia justificar uma pasta `status/`).

Componentes usados por uma única feature (ex.: `ManagerScaffold`) ficam junto da tela, em `view/screens/<domínio>/`, não em `view/components/`.

## Estilo de código Kotlin

- Indentação de 4 espaços; vírgula à direita (trailing comma) em listas de parâmetros de múltiplas linhas.
- Funções de composição simples usam corpo de expressão (`= BaseText(...)`) em vez de bloco com `return`.
- KDoc (`/** ... */` com `@param`) é usado consistentemente nos componentes públicos do Design System (`view/components`, `view/theme`) — inclusive explicando *por que* uma decisão de API foi tomada, não só o que o parâmetro faz. Esse padrão ainda não é seguido em `ViewModel`, `usecase` e `Screen` — ao escrever KDoc novo nessas camadas, siga o mesmo nível de detalhe do Design System.
- Comentários e textos de UI ficam em **português** (idioma do domínio/negócio); identificadores de código (classes, funções, parâmetros) ficam em **inglês**. É uma convenção deliberada — mantenha essa mistura, não traduza identificadores nem passe comentários para inglês.
- `TODO:` em português é o marcador padrão para lógica pendente (ex.: integração de backend não implementada, navegação placeholder) — prefira isso a deixar um método vazio sem explicação.
- Magic numbers de layout que não fazem sentido como token global (ex.: diâmetro de um círculo específico) viram uma `private val` nomeada no topo do arquivo do componente (ex.: `private val OccupationCircleSize = 84.dp` em `StockOccupationCard.kt`), em vez de valor solto inline.

## Convenções de Composables

- **Componentes controlados**: recebem `value`/`selected` + `onValueChange`/`onSelectedChange` e não guardam o valor "de negócio" internamente — só estado puramente visual e efêmero (ex.: `secretVisible` em `ZeraTextInput` para mostrar/ocultar senha). Cabe ao chamador (`ViewModel`/`State`) manter o valor.
- **Família de cor + hierarquia visual**: componentes coloridos recebem `style: ZeraColorFamily = ZeraColorFamily.Blue` (ou `.Yellow`, dependendo do componente) e, quando têm mais de uma hierarquia visual, `type` (`ZeraButtonType`/`ZeraBoxType`) com padrão `Primary`. A resolução de cor passa sempre pela extensão `ZeraColorFamily.palette()` — nunca acesse `MaterialTheme.colorScheme` diretamente para essas cores dentro de um componente novo.
- **Zero medida livre (regra estrita)**: é **proibido** usar valor solto de cor, espaçamento ou raio de borda em qualquer tela ou componente. Cor sempre via `ZeraColorFamily`/`.palette()` (ou um papel do `MaterialTheme.colorScheme` quando não há família de cor aplicável); espaçamento sempre via `Spacing.*`; raio de borda sempre via `Radius.*`. A única exceção tolerada é para `width`/`height` pontuais de um elemento específico (ex.: diâmetro de um círculo, largura de um ícone maior que o padrão) — e mesmo essa exceção deve virar uma `private val` nomeada no topo do arquivo (ver "magic numbers" acima), nunca um número solto inline. Essa regra vale tanto para componentes do catálogo quanto para o corpo de uma `Screen`.
- **Slots de conteúdo**: containers/scaffolds recebem `content: @Composable <Scope>.() -> Unit` (ex.: `ZeraBox` com `BoxScope`, `ManagerScaffold` com `ColumnScope`) em vez de uma lista fechada de parâmetros.
- **Listas**: um `data class Item` com campo `id` (chave de recomposição do `LazyColumn`) ao lado do composable de lista (`NotificationItem`/`NotificationList`, `ProductItem`/`ProductList`). O item individual (`ProductListItem`) é um composable separado, reaproveitável fora da lista.
- **Preview obrigatório**: todo componente novo do Design System deve ter ao menos um `@Preview` demonstrando o caso de uso mais comum; variantes relevantes (erro, vazio, com/sem ícone) merecem previews adicionais.

## Convenções de ViewModel e State

- Um `ViewModel` por tela (ou por feature de tela), com um único `data class State` associado.
- `_state = mutableStateOf(State())` privado; exposto como `val state = _state` (ver ressalva em "Inconsistências conhecidas").
- Métodos de intenção nomeados `on<Campo>Change(novoValor)`, que fazem `_state.value = _state.value.copy(<campo> = novoValor)`.
- Ações assíncronas (`signIn()`, `signUp()`) seguem o mesmo roteiro: setar `isLoading = true, errorMessage = null`, validar sincronamente o que der (client-side), then `viewModelScope.launch { try { ... } catch (e: Exception) { erro } }`.
- Navegação pós-ação é disparada pelo próprio `ViewModel` via `ZeraNavigator`, nunca pela `Screen`.
- Feature ainda não integrada ao backend: `ViewModel` e `State` já existem com a forma final esperada, e o método de ação fica com corpo vazio + `TODO:` (ver `SignUpViewModel.signUp()`), em vez de removido ou implementado com dado fake silenciosamente.

## Inconsistências conhecidas

> Registradas aqui para não serem confundidas com padrão intencional. Corrija apenas mediante decisão explícita (pode exigir migração de contrato de API) — não são "bugs simples" a arrumar de passagem.

1. **Erro de digitação "Sing" vs. "Sign"**: todo o domínio de login usa `Sing` (`SingInViewModel`, `SingInState`, caso de uso `SingIn`, `SingInRequestDTO`, `SingInResponseDTO`), enquanto o domínio de cadastro usa a grafia correta `Sign` (`SignUpViewModel`, `SignUpScreen`, `SignInScreen`). Renomear afeta nomes usados em serialização indireta (os campos JSON não usam esses nomes de classe, então o risco é baixo, mas o rename deve ser feito de uma vez em todos os usos).
2. **`ViewModel.state` expõe `MutableState<T>`, não `State<T>`**: `val state = _state` não converte para somente-leitura, então nada impede (a nível de tipo) que um consumidor externo escreva em `viewModel.state.value` diretamente, quebrando o fluxo unidirecional. Nenhuma tela faz isso hoje, mas o tipo permite.
3. **Contrato de cadastro incompleto**: `SignUpScreen`/`SignUpViewModel` coletam `name` e `token` (código de convite) além de `email`/`password`, mas `AuthService.signUp()` reutiliza `SingInRequestDTO` (só `email` + `password`) para `POST invitations/redeem`. O formato real do payload de cadastro ainda não está definido — ver [06-contratos-api.md](06-contratos-api.md).
4. **Navegação placeholder em `BottomNavBar`**: todos os atalhos exceto o de escanear (`QrCode`) navegam para `Route.Welcome` com um `TODO` indicando que as rotas reais ainda não existem.
5. **Mapeamento de `ItemStatus` do item mockado em `ItemDetailsScreen`/`ItemApprovedScreen` ainda não confirmado**: o item de exemplo (ID `265964`, "aguardando o gestor aprovar o cadastro") usa `ItemStatus.PendingApproval` (rótulo "Pendente") hoje, escolhido por ser semanticamente o mais próximo da KDoc desse valor — mas o mesmo fluxo poderia ser `ItemStatus.AwaitingEvaluation` (rótulo "Em aprovação"), que é o texto que a tela mostrava antes do `ItemStatus` existir. Qual valor é o correto para "item recém-cadastrado, aguardando aprovação do gestor" depende de uma decisão de regra de negócio ainda pendente (não pode ser resolvida só no `ViewModel`) — ver [ItemStatus.kt](../app/src/main/java/com/zera/android/view/components/outros/ItemStatus.kt).
