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
- **`ItemSummaryCard(itemId, itemName, itemSubtitle, status, modifier, icon)`** — card de destaque no topo de uma tela de detalhe de item: miniatura à esquerda ("ID {itemId}" + nome + subtítulo) e um `ItemStatusTag` à direita; composto sobre `ZeraBox(style = ZeraColorFamily.Blue)`. `status: ItemStatus` (ver seção "Outros componentes visuais").
- **`ApprovedItemCard(itemName, itemSubtitle, itemId, status, modifier)`** — card branco simples (sem miniatura) com o resumo de um item: título ("nome · subtítulo") + legenda "ID {itemId}" + `ItemStatusTag`; usado na tela de confirmação de aprovação (`ItemApprovedScreen`), diferente do `ItemSummaryCard` (card azul com miniatura).

```kotlin
ShortcutCard(label = "Itens", value = state.totalItems, labelIcon = ZeraIcon.Box, onClick = { /* navegar */ })
StockOccupationCard(itemCount = 300, occupation = 0.6f)
Notification(label = "Recusado pelo gestor", text = "Chip controlador", style = ZeraColorFamily.Red)
ItemSummaryCard(itemId = "265964", itemName = "Placa de vídeo", itemSubtitle = "Notebook Mac", status = ItemStatus.PendingApproval)
ApprovedItemCard(itemName = "Placa de vídeo", itemSubtitle = "Notebook Mac", itemId = "265964", status = ItemStatus.PendingApproval)
```

## Listas

Arquivo: `view/components/lists/`.

- **`NotificationItem(id, label, text, style)`** + **`NotificationList(notifications, modifier, onItemClick, contentPadding, emptyContent)`** — lista rolável de `Notification`; ocupa só a altura do conteúdo (use `Modifier.heightIn(max = ...)` dentro de um container que já rola).
- **`ProductItem(id, name, icon)`** + **`ProductList(products, onItemClick, modifier, contentPadding, emptyContent)`** — lista rolável de `ProductListItem`.
- **`ProductListItem(itemName, itemId, onClick, modifier, icon)`** — item individual (ícone + nome + ID + seta); reutilizável fora de `ProductList`.
- **`EditableFieldRow(label, value, modifier, onEditClick)`** — linha de detalhe de campo (rótulo + valor em negrito + lápis de editar opcional à direita); usada em telas de detalhe (ex.: "Detalhes do Item"), diferente de `ProductListItem` (que representa um item de lista de produtos). `onEditClick = null` (padrão) esconde o lápis.
- **`EmployeeItem(id, name, role, isPending)`** + **`EmployeeList(employees, onItemClick, modifier, contentPadding, emptyContent)`** — lista rolável de `EmployeeListItem`.
- **`EmployeeListItem(name, role, isPending, onClick, modifier)`** — item individual (avatar circular + nome + "cargo · status" + seta); reutilizável fora de `EmployeeList`. `isPending = true` mostra "Pendente" em laranja (`ZeraColorFamily.Yellow`) em vez de "Ativo" em cinza, no texto e no avatar. Ainda não recebe foto de perfil — o avatar é só um círculo colorido.

```kotlin
NotificationList(notifications = state.notifications, onItemClick = { /* abrir alerta */ }, modifier = Modifier.heightIn(max = 400.dp))
ProductList(products = state.latestProducts, onItemClick = { /* abrir item */ })
EditableFieldRow(label = "Categoria", value = state.category, onEditClick = { viewModel.onEditClick() })
EmployeeList(employees = state.employees, onItemClick = { /* abrir colaborador */ }, modifier = Modifier.weight(1f))
```

## Navegação (componentes visuais)

Arquivo: `view/components/navigation/`. Estes componentes são **apenas visuais** — não conhecem `Route` nem `ZeraNavigator` diretamente (exceção: `BottomNavBar`, que já recebe `Route` para destacar o item ativo, mas ainda não dispara navegação real — ver "Inconsistências conhecidas" em [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md)).

- **`UpperNavBar(title, modifier, goBack, onBackClick, showActions)`** — barra superior com título, (opcional) botão "Voltar" e os atalhos de notificações/perfil/menu à direita (perfil e menu ainda sem ação real, só visuais). `showActions = false` esconde os três atalhos à direita — use em telas que só precisam do "Voltar" (ex.: "Perfil").
- **`BottomNavBar(modifier, currentRoute)`** — barra inferior com 4 atalhos + botão central de escanear (`ZeraIcon.QrCode`); usa `ShortCutButton` internamente.
- **`ManagerScaffold(title, modifier, goBack, onBackClick, fabIcon, onFabClick, scrollable, content: @Composable ColumnScope.() -> Unit)`** (em `view/screens/manager/ManagerScaffold.kt`, não em `view/components/` — é específico das telas do Gestor) — casca compartilhada com `UpperNavBar` + `Column` (rolável quando `scrollable = true`, padrão) + `BottomNavBar` fixado em `Route.ManagerHome` + FAB de assistente virtual. `goBack = true` exibe o botão "Voltar" na `UpperNavBar` — use em telas acessadas por navegação (ex.: "Colaboradores", aberta a partir de um atalho do Home), diferente das telas raiz do `BottomNavBar` (ex.: `ManagerHomeScreen`).

```kotlin
ManagerScaffold(title = "Visão geral", onFabClick = { /* abrir chatbot */ }) {
    BodyText(text = "Conteúdo da tela")
}
```

## Overlays

Arquivo: `view/components/overlays/`.

- **`BottomSheet(onDismissRequest, modifier, sheetState, scrimColor, contentPadding, content: @Composable ColumnScope.() -> Unit)`** (`BottomSheet.kt`) — base de um bottom sheet modal (`ModalBottomSheet` do Material 3 com *drag handle*, cantos e espaçamento padrão já resolvidos via `Radius`/`Spacing`). `scrimColor` (padrão `BottomSheetDefaults.ScrimColor`) escurece o restante da tela atrás do sheet; tocar nessa área também dispara `onDismissRequest`. Não define conteúdo próprio; sem prefixo `Zera` por não ser parametrizado por `ZeraColorFamily`. O chamador controla a visibilidade compondo o `BottomSheet` condicionalmente (ex.: `if (showSheet) { ... }`).
- **`PopupDialog(onDismissRequest, modifier, contentPadding, content: @Composable ColumnScope.() -> Unit)`** (`PopupDialog.kt`) — base de um popup modal centralizado (usa o `Dialog` do Compose, que já resolve scrim + fechar ao tocar fora/voltar) com um botão de fechar (X) fixo no canto superior direito. Mesma convenção de visibilidade do `BottomSheet`: o chamador guarda um campo no `State` do `ViewModel` (`String?`/`Boolean`) e só compõe o `PopupDialog` com `if (...) { ... }` — o `ViewModel` nunca chama Compose diretamente, só atualiza esse campo.
```kotlin
if (showDamageSheet) {
    BottomSheet(onDismissRequest = { showDamageSheet = false }) {
        TitleText(text = "Quais danos o item possui?")
        ZeraChipsGroup(options = danos, selected = selecionado, onSelectedChange = { selecionado = it }, stacked = true)
        ZeraButton(text = "Confirmar danos", onClick = {}, fillMaxWidth = true)
    }
}
```

## Ícones

Arquivo: `view/theme/icons/`.

- **`ZeraIcon`** (enum) — catálogo de ícones (`Bell`, `Box`, `Chatbot`, `Check`, `Close`, `Crate`, `DownArrow`, `Download`, `Edit`, `Exclamation`, `Gear`, `Graph`, `Group`, `Home`, `InfoCircle`, `LeftArrow`, `RightArrow`, `LeftUTurn`, `Location`, `Megaphone`, `Menu`, `Placeholder`, `Plus`, `ProceedArrow`, `Profile`, `QrCode`, `Recycle`, `RightUTurn`, `Screen`, `Send`, `Truck`, `Wrench`), cada um apontando para um drawable vetorial.
- **`ZeraIcon(icon, contentDescription, modifier, size, tint)`** (composable) — desenha o ícone; `size` padrão 24dp, `tint` padrão `LocalContentColor.current` (ver `ZeraIconDefaults`).

```kotlin
ZeraIcon(ZeraIcon.Bell, contentDescription = "Notificações")
ZeraIcon(ZeraIcon.Check, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
```

## Outros componentes visuais

- **`Logo(modifier)`** (`view/components/logo/Logo.kt`) — logo do app com proporção fixa (595:154); controle de tamanho via `Modifier.width(...)`.
- **`SplashBackground()`** (`view/components/outros/SplashBackground.kt`) — arte decorativa de fundo da splash/telas de auth (faixas coloridas via `Canvas`).
- **`Tag(text, modifier, style, contentPadding)`** (`view/components/outros/Tag.kt`) — pílula de status curta, sempre no par (container, conteúdo) de uma `ZeraColorFamily`.
- **`ItemStatus`** (enum, `view/components/outros/ItemStatus.kt`) — status de um item, espelhando parte do enum do backend; cada valor já carrega `label` (texto) e `colorFamily`: `PendingApproval` ("Pendente", Yellow), `Rejected` ("Recusado", Red), `InStock` ("Aprovado", Green), `InMaintenance` ("Em manutenção", Yellow), `AwaitingEvaluation` ("Em aprovação", Yellow), `Disposed` ("Descartado", Red). O backend também tem `DRAFT`/`REMOVED`, ainda não modelados aqui (sem chip definido). **Escopo atual**: usado só por `ItemSummaryCard`/`ApprovedItemCard` (`ItemDetailsScreen`/`ItemApprovedScreen`) — `ProductItem`/`ProductListItem` (listas em `ItensScreen`/`ManagerHomeScreen`) continuam com `statusText`/`statusStyle` soltos, por decisão explícita de manter o escopo do enum restrito por enquanto.
- **`ItemStatusTag(status, modifier)`** (mesmo arquivo) — `Tag` pré-preenchida com o `label`/`colorFamily` de um `ItemStatus`; quem desenha o chip só passa o `status`, sem duplicar texto/cor.
- **`Avatar(initials, modifier, photoUrl, size, style)`** (`view/components/outros/Avatar.kt`) — avatar circular do usuário; mostra as `initials` (calculadas por quem chama, ex.: no `ViewModel`) quando `photoUrl` é `null` (padrão). Quando `photoUrl` é informada, **ainda não carrega a foto de verdade** — não há lib de carregamento de imagem (ex.: Coil) no projeto ainda; por enquanto só troca as iniciais por um ícone de placeholder (`TODO` no componente).
- **`ProgressBar(modifier, progress)`** (`view/components/progressbars/ProgressBar.kt`) — barra linear; `progress = null` → indeterminada.
- **`CircularProgressBar(progress, label, modifier, color)`** (`view/components/progressbars/CircularProgressBar.kt`) — indicador circular com porcentagem central e rótulo opcional.

## Gráficos

Arquivo: `view/components/graphs/`.

- **`rankedBarGraphColors(items, valueOf)`** (`GraphColorCycle.kt`, `internal`) — lógica de cor compartilhada pelos dois gráficos de barra abaixo: ordena `items` por `valueOf` decrescente e devolve um mapa índice-original → `ZeraColorFamily`, ciclando `BarGraphColorCycle` = `[Blue, Yellow, Green]` (1º maior valor = azul, 2º = amarelo, 3º = verde, 4º = azul de novo...). Não depende da ordem de `items`, só do valor.
- **`BarGraphItem(label, percentage)`** + **`HorizontalBarGraph(items, modifier, labelWeight, barHeight)`** (`HorizontalBarGraph.kt`) — gráfico de barras horizontais dentro de um card branco (`Surface` própria, cantos `Radius.medium`), uma linha por `BarGraphItem`: rótulo à esquerda, barra preenchida proporcionalmente a `percentage` (`0f`..`1f`, **já calculado por quem chama** — ver TODO de regra de negócio abaixo) e a porcentagem por extenso à direita, ambos na cor atribuída por `rankedBarGraphColors`. `TODO` no componente: animar o preenchimento da barra (ex.: `animateFloatAsState`).

```kotlin
HorizontalBarGraph(
    items = listOf(
        BarGraphItem(label = "Eletrônicos", percentage = 0.5f), // maior % → Azul
        BarGraphItem(label = "Plásticos", percentage = 0.42f),  // 2º maior → Amarelo
        BarGraphItem(label = "Outros", percentage = 0.2f),      // 3º maior → Verde
    ),
)
```

- **`VerticalBarGraphItem(label, value)`** + **`VerticalBarGraph(items, modifier, title, barWidth, maxBarHeight)`** (`VerticalBarGraph.kt`) — gráfico de colunas verticais dentro de um card branco (mesma `Surface`/cantos do `HorizontalBarGraph`), com `title` opcional no topo (`LabelText` em negrito). Uma coluna por `VerticalBarGraphItem`: valor acima (formatado sem casas decimais quando é inteiro), barra com altura proporcional ao maior `value` da lista (a de maior valor preenche `maxBarHeight` por completo) e rótulo abaixo — cor automática igual ao `HorizontalBarGraph`, via `rankedBarGraphColors`. Diferente de `percentage`, `value` é um valor bruto (ex.: kg descartados) — a normalização (maior = barra cheia) é feita dentro do próprio componente, não por quem chama. `TODO` no componente: animar a altura da barra (ex.: `animateDpAsState`).

```kotlin
VerticalBarGraph(
    title = "Materiais descartados (kg)",
    items = listOf(
        VerticalBarGraphItem(label = "Mai", value = 7f),
        VerticalBarGraphItem(label = "Jun", value = 10f),
        VerticalBarGraphItem(label = "Jul", value = 29f), // maior valor → Azul, barra cheia
        VerticalBarGraphItem(label = "Ago", value = 12f),
    ),
)
```

> **TODO (regra de negócio):** o cálculo dos valores exibidos (`BarGraphItem.percentage` / `VerticalBarGraphItem.value`) a partir dos dados brutos (ex.: contagem de itens por categoria, kg descartados por mês) deve ser feito no `ViewModel` da tela que usar esses componentes, não nos componentes em si. Documentar essa regra em `05-regras-de-negocio/` quando o primeiro caso de uso real for implementado (hoje não há tela consumindo `HorizontalBarGraph`/`VerticalBarGraph` ainda).


Ao introduzir o segundo ou terceiro caso de uso com necessidades em comum (ex.: tratamento de erro padronizado, retry, cache), avalie formalizar essas abstrações e registre a decisão em [08-decisoes-arquiteturais/](08-decisoes-arquiteturais/).
