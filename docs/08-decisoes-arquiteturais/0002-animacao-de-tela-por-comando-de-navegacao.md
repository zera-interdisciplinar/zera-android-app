# ADR-0002 — Animação de tela escolhida no comando de navegação

## Status

Aceito e implementado.

## Contexto

As telas precisam de animações de entrada e saída, e a mesma rota pode querer animações diferentes conforme quem navegou para ela (ex.: `SignIn` subindo a partir da Splash, mas deslizando de lado a partir de outra tela). O Navigation Compose só aceita transições fixas por `composable<>` ou lambdas por origem/destino no `NavHost`; não há suporte nativo para escolher a animação por chamada de `navigate`. Além disso, o "voltar" (inclusive o do sistema e o gesto de voltar) não passa pelo `ZeraNavigator`, então não pode depender de um parâmetro passado no `goBack()`.

## Decisão

A animação é um parâmetro opcional dos comandos que empilham telas (`ZeraNavigator.push`, `pushAndPop`, `pushAndPopAll` → `NavCommand.*.animation`), do tipo `ScreenAnimation` (`Fade`, `SlideHorizontal`, `SlideVertical`, `None`). Cada valor define as quatro transições do `NavHost`: `enter`, `exit`, `popEnter` e `popExit`.

Um `ScreenAnimationRegistry`, criado em `ZeraNavHost`, guarda a animação de cada `NavBackStackEntry` pelo `id`. `prepare()` limpa do mapa as telas que saíram da pilha e marca a animação pendente antes do `navigate`; `of(entry)` associa a pendente à tela nova na primeira consulta. O `NavHost` usa `of(targetState)` em `enterTransition`/`exitTransition` e `of(initialState)` em `popEnterTransition`/`popExitTransition`, de modo que o "voltar" reverte a animação com que a tela foi empilhada, sem parâmetro no `goBack()`.

## Alternativas consideradas

- **`enterTransition`/`exitTransition` fixos em cada `composable<>`** — rejeitada por repetição (uma configuração por rota) e por não permitir animação diferente para a mesma rota conforme o contexto.
- **Regras centralizadas por par origem/destino no `NavHost`** — rejeitada por gerar um `when` que cresce e quebra a cada rota nova, e por ainda não permitir escolha no momento da chamada.
- **Animação como um estado global mutável no `NavHost` (última animação usada)** — rejeitada porque o `goBack()` reaproveitaria a animação de avanço com a direção errada, e o back do sistema usaria uma animação velha.

## Consequências

- Quem navega decide a animação, e o "voltar" (inclusive do sistema) reverte automaticamente, pois a animação está associada à tela e não à chamada.
- Se um `navigate` não gerar transição (ex.: `launchSingleTop` na mesma tela), a animação pendente vaza para a próxima navegação.
- O mapa vive em memória; após process death as telas restauradas usam `Fade`.
- Toda animação nova exige implementar as quatro transições em `ScreenAnimation`.
- Shared elements continuam exigindo que o `composable<>` forneça `LocalAnimatedVisibilityScope` (ver [01-arquitetura.md](../01-arquitetura.md)).
