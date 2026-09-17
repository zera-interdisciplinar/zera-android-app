# ADR-0001 — Navegação centralizada via ZeraNavigator

## Status

Aceito e implementado.

## Contexto

Jetpack Navigation Compose exige uma referência ao `NavHostController` para disparar navegação (`navController.navigate(...)`). O app segue a regra de que a `View` não deve conter lógica (ver "Separação entre View e lógica (regra estrita)" em [01-arquitetura.md](../01-arquitetura.md)) e que é o `ViewModel` quem decide, na maioria dos casos, para onde navegar (ex.: `SingInViewModel.signIn()` decide a rota de destino a partir do `role` do usuário). Um `ViewModel`, porém, não deveria depender de um `NavHostController` (um objeto de Compose/Navigation), sob risco de acoplar lógica de negócio a um tipo de UI e dificultar testes.

## Decisão

Centralizar toda navegação em `ZeraNavigator` (`view/navigation/ZeraNavigator.kt`), um `object` que expõe operações simples (`push`, `pushAndPop`, `pushAndPopAll`, `goBack`) e internamente envia um `NavCommand` para um `Channel`. `ZeraNavHost` é o único ponto que de fato possui o `NavHostController`: ele coleta o `Flow<NavCommand>` (`ZeraNavigator.commands`) num `LaunchedEffect(Unit)` e traduz cada comando numa chamada real de `navController.navigate(...)`/`popBackStack()`.

Qualquer camada — tipicamente o `ViewModel`, e excepcionalmente uma `View` sem lógica associada (`SplashScreen`, `WelcomeScreen`) — pode chamar `ZeraNavigator` diretamente, sem receber o `NavHostController` por parâmetro ou injeção.

## Alternativas consideradas

- **Passar o `NavHostController` para o `ViewModel`** (via construtor ou `SavedStateHandle`) — rejeitada por acoplar o `ViewModel` a um tipo do Compose/Navigation, indo contra a separação de camadas.
- **`ViewModel` expõe um evento de navegação (`SharedFlow`/callback), e a própria `Screen` observa e chama `navController`** — rejeitada por deixar uma decisão de "quando navegar" espalhada na `View`, ainda que a rota em si viesse do `ViewModel`.
- **`NavHostController` estático/global**, chamado diretamente por quem precisar — descartada por acoplar diretamente ao tipo concreto do Navigation Compose; o `Channel` de comandos evita esse acoplamento e permite testar o remetente sem instanciar navegação real.

## Consequências

- `ViewModels` (e as duas `Views` sem `ViewModel` citadas acima) navegam sem depender de `NavHostController`, mantendo a regra de separação View/lógica.
- Introduz uma camada de indireção (`NavCommand`) que precisa ser mantida em paralelo às definições de `Route`.
- Comandos enviados quando `ZeraNavHost` ainda não está compondo (ou não há coletor ativo) podem ser perdidos — o `Channel` é `BUFFERED`, mas depende de haver exatamente um coletor vivo.
- A navegação não é diretamente verificável num teste unitário de `ViewModel` sem também observar o `Flow` de comandos (não há um "fake NavHostController" simples de inspecionar).
