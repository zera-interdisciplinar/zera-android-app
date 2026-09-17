# 03 — Catálogo de Componentes

> Antes de criar um componente novo, verifique se algo abaixo já resolve o caso (ver [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md) para o critério de nomenclatura/pasta). Ao adicionar um componente, documente-o aqui seguindo o mesmo formato: assinatura resumida, parâmetros relevantes e um exemplo de uso.

## Design tokens (tema)

Definidos em `view/theme/`.

| Token | Arquivo | Valores |
|---|---|---|
| `Spacing` | `Spacing.kt` | `none`=0dp, `micro`=4dp, `small`=8dp, `medium`=16dp, `large`=24dp, `xLarge`=32dp, `huge`=64dp |
| `Radius` | `Radius.kt` | `none`=0dp, `small`=4dp, `medium`=8dp, `large`=16dp, `xLarge`=24dp |
| `ZeraColorFamily` | `ZeraPalette.kt` | `Blue`, `Red`, `Yellow`, `Green` — cada uma resolvida via `.palette()` em `(base, onBase, container, onContainer)`, mapeando para `primary/error/secondary/tertiary` do `ColorScheme` |
| `Typography` | `Type.kt` | Type scale padrão do Material 3 com a fonte `Inter` (regular/medium/semibold/bold) |

`ZeraTheme(darkTheme, dynamicColor, content)` monta o `MaterialTheme` do app. `darkTheme` existe como parâmetro mas o `DarkColorScheme` ainda está incompleto (só `primary`/`secondary`/`tertiary` definidos) — não usar tema escuro em produção ainda.

Exemplo de uso de um token de cor num componente novo:

```kotlin
val palette = ZeraColorFamily.Green.palette()
Surface(color = palette.container, contentColor = palette.onContainer) { ... }
```

## Textos

Arquivo: `view/components/texts/Texts.kt`. Todos compartilham a mesma assinatura de parâmetros (`text`, `modifier`, `color`, `alpha`, `textAlign`, `maxLines`, `overflow`, `bold`, `italic`, `underline`, `onClick`) e delegam para um `BaseText` privado.

| Componente | Type scale | Cor padrão | Observação |
|---|---|---|---|
| `HeadlineText` | `headlineLarge` | `primary` | título de tela |
| `TitleText` | `titleLarge` | `primary` | título de seção/card |
| `SubtitleText` | `titleMedium` | `onSurfaceVariant` | complementa um `TitleText` |
| `BodyText` | `bodyLarge` | `primary` | texto corrido |
| `LabelText` | `labelLarge` | `onSurfaceVariant` | 1 linha, `Ellipsis`; rótulos de campo/botão/chip |
| `CaptionText` | `bodySmall` | `onSurfaceVariant` | texto pequeno auxiliar |
| `OverlineText` | `labelSmall` + letter-spacing | `onSurfaceVariant` | 1 linha, `Ellipsis`, texto convertido para maiúsculas |

```kotlin
TitleText(text = "Últimos Itens", bold = true, color = MaterialTheme.colorScheme.onSurface)
LabelText(text = "Ver Todos", bold = true, onClick = { /* navegar */ })
```

## Botões

Arquivo: `view/components/buttons/`.

- **`ZeraButton(text, onClick, modifier, fillMaxWidth, style, type, enabled, icon, width, maxWidth, minWidth)`** — botão retangular com texto e ícone opcional. `style: ZeraColorFamily` (padrão `Blue`), `type: ZeraButtonType` (padrão `Primary`).
- **`ZeraButtonType`**: `Primary` (fundo sólido), `Secondary` (container claro + borda), `Tertiary` (transparente, só texto/ícone na cor base).
- **`IconButton(icon, onClick, contentDescription, modifier, style, type, enabled, size)`** — variante circular só com ícone, reaproveita as mesmas funções de cor de `ZeraButton` (`buttonColorPair`/`buttonColors`/`buttonBorder`, internas ao pacote).
- **`ButtonStates`** (`ButtonStates.kt`) — `sealed interface` com `Idle`/`Loading`, preparado para representar estado de carregamento de uma ação disparada por botão; **ainda não é consumido por `ZeraButton`** (hoje o loading é tratado ad-hoc pela `Screen`, ex.: trocando o texto do botão em `SignInScreen`).

```kotlin
ZeraButton(text = "Entrar", onClick = { viewModel.signIn() }, fillMaxWidth = true)
ZeraButton(text = "Continuar", onClick = {}, style = ZeraColorFamily.Blue, icon = ZeraIcon.ProceedArrow)
IconButton(icon = ZeraIcon.Close, onClick = {}, contentDescription = "Fechar", style = ZeraColorFamily.Red, type = ZeraButtonType.Secondary)
```

## Containers

Arquivo: `view/components/containers/ZeraBox.kt`.

- **`ZeraBox(modifier, style, type, shape, contentPadding, content: @Composable BoxScope.() -> Unit)`** — superfície colorida genérica; `type: ZeraBoxType` (`Primary` sólido / `Secondary` container claro + borda). Propaga a cor de conteúdo via `LocalContentColor` — ícones e `Text` nativo herdam automaticamente, mas os componentes de texto do app (`BodyText`, etc.) têm cor própria e precisam receber `color = LocalContentColor.current` explicitamente quando estiverem sobre um `ZeraBox` colorido.

```kotlin
ZeraBox(style = ZeraColorFamily.Blue, shape = CircleShape, contentPadding = PaddingValues(Spacing.small)) {
    BodyText(text = "71%", bold = true, color = LocalContentColor.current)
}
```

## Inputs

Arquivo: `view/components/inputs/`.

- **`ZeraTextInput(onValueChange, modifier, value, label, placeholder, type: ZeraInputType, imeAction, enabled, isError, errorMessage, width)`** — campo de texto controlado; `ZeraInputType` (`Text/Email/Password/Phone/Number/Decimal`) define teclado, capitalização e mascaramento (`Password` mostra um toggle "Mostrar/Ocultar").
- **`ZeraTokenInput(value, onValueChange, modifier, size=6, label, enabled, isError, errorMessage, onFilled)`** — campo de código/OTP: um único `BasicTextField` invisível desenhando `size` células via `decorationBox`; chama `onFilled` quando atinge `size` dígitos.
- **`ZeraChipsGroup(options, selected, onSelectedChange, modifier, label, style, stacked)`** — grupo de chips de seleção única; `stacked = true` quebra linha (`FlowRow`), `false` (padrão) rola horizontalmente.

```kotlin
ZeraTextInput(label = "Email", placeholder = "Seu email", value = state.email, onValueChange = viewModel::onEmailChange, type = ZeraInputType.Email)
ZeraTokenInput(value = state.token, onValueChange = viewModel::onTokenChange, label = "Código de convite", onFilled = {})
ZeraChipsGroup(label = "Possui danos?", options = listOf("Tela quebrada", "Não liga"), selected = selecionado, onSelectedChange = { selecionado = it }, stacked = true)
```

## Cards

Arquivo: `view/components/cards/`.

- **`ShortcutCard(label, value, onClick, modifier, labelIcon, enabled)`** — atalho clicável: rótulo + valor em destaque + seta. `value` já vem formatado como string pelo chamador.
- **`StockOccupationCard(itemCount, occupation, modifier)`** — card de destaque com total de itens e um `ZeraBox` circular com a porcentagem de ocupação (`occupation` de `0f` a `1f`).
- **`Notification(label, text, modifier, style, redirect)`** — card de alerta/notificação; `redirect` opcional adiciona a ação "Resolver agora →".

```kotlin
ShortcutCard(label = "Itens", value = state.totalItems, labelIcon = ZeraIcon.Box, onClick = { /* navegar */ })
StockOccupationCard(itemCount = 300, occupation = 0.6f)
Notification(label = "Recusado pelo gestor", text = "Chip controlador", style = ZeraColorFamily.Red)
```

## Listas

Arquivo: `view/components/lists/`.

- **`NotificationItem(id, label, text, style)`** + **`NotificationList(notifications, modifier, onItemClick, contentPadding, emptyContent)`** — lista rolável de `Notification`; ocupa só a altura do conteúdo (use `Modifier.heightIn(max = ...)` dentro de um container que já rola).
- **`ProductItem(id, name, icon)`** + **`ProductList(products, onItemClick, modifier, contentPadding, emptyContent)`** — lista rolável de `ProductListItem`.
- **`ProductListItem(itemName, itemId, onClick, modifier, icon)`** — item individual (ícone + nome + ID + seta); reutilizável fora de `ProductList`.

```kotlin
NotificationList(notifications = state.notifications, onItemClick = { /* abrir alerta */ }, modifier = Modifier.heightIn(max = 400.dp))
ProductList(products = state.latestProducts, onItemClick = { /* abrir item */ })
```

## Navegação (componentes visuais)

Arquivo: `view/components/navigation/`. Estes componentes são **apenas visuais** — não conhecem `Route` nem `ZeraNavigator` diretamente (exceção: `BottomNavBar`, que já recebe `Route` para destacar o item ativo, mas ainda não dispara navegação real — ver [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md#inconsistências-conhecidas)).

- **`UpperNavBar(title, modifier, goBack)`** — barra superior com título e (opcional) botão "Voltar"; ações de notificação/perfil ainda são `TODO`.
- **`BottomNavBar(modifier, currentRoute)`** — barra inferior com 4 atalhos + botão central de escanear (`ZeraIcon.QrCode`); usa `ShortCutButton` internamente.
- **`ManagerScaffold(title, modifier, fabIcon, onFabClick, content: @Composable ColumnScope.() -> Unit)`** (em `view/screens/manager/ManagerScaffold.kt`, não em `view/components/` — é específico das telas do Gestor) — casca compartilhada com `UpperNavBar` + `Column` rolável + `BottomNavBar` fixado em `Route.ManagerHome` + FAB de assistente virtual.

```kotlin
ManagerScaffold(title = "Visão geral", onFabClick = { /* abrir chatbot */ }) {
    BodyText(text = "Conteúdo da tela")
}
```

## Ícones

Arquivo: `view/theme/icons/`.

- **`ZeraIcon`** (enum) — catálogo de ícones (`Bell`, `Box`, `Chatbot`, `Check`, `Close`, `Crate`, `DownArrow`, `Download`, `Edit`, `Exclamation`, `Gear`, `Graph`, `Group`, `Home`, `InfoCircle`, `LeftArrow`, `RightArrow`, `LeftUTurn`, `Location`, `Megaphone`, `Placeholder`, `Plus`, `ProceedArrow`, `Profile`, `QrCode`, `Recycle`, `RightUTurn`, `Screen`, `Send`, `Truck`, `Wrench`), cada um apontando para um drawable vetorial.
- **`ZeraIcon(icon, contentDescription, modifier, size, tint)`** (composable) — desenha o ícone; `size` padrão 24dp, `tint` padrão `LocalContentColor.current` (ver `ZeraIconDefaults`).

```kotlin
ZeraIcon(ZeraIcon.Bell, contentDescription = "Notificações")
ZeraIcon(ZeraIcon.Check, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
```

## Outros componentes visuais

- **`Logo(modifier)`** (`view/components/logo/Logo.kt`) — logo do app com proporção fixa (595:154); controle de tamanho via `Modifier.width(...)`.
- **`SplashBackground()`** (`view/components/outros/SplashBackground.kt`) — arte decorativa de fundo da splash/telas de auth (faixas coloridas via `Canvas`).
- **`Tag(text, modifier, style, contentPadding)`** (`view/components/outros/Tag.kt`) — pílula de status curta, sempre no par (container, conteúdo) de uma `ZeraColorFamily`.
- **`ProgressBar(modifier, progress)`** (`view/components/progressbars/ProgressBar.kt`) — barra linear; `progress = null` → indeterminada.
- **`CircularProgressBar(progress, label, modifier, color)`** (`view/components/progressbars/CircularProgressBar.kt`) — indicador circular com porcentagem central e rótulo opcional.


Ao introduzir o segundo ou terceiro caso de uso com necessidades em comum (ex.: tratamento de erro padronizado, retry, cache), avalie formalizar essas abstrações e registre a decisão em [08-decisoes-arquiteturais/](08-decisoes-arquiteturais/).
