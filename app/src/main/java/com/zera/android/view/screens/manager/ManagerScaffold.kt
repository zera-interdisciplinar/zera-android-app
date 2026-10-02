package com.zera.android.view.screens.manager

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.containers.ZeraGradientBox
import com.zera.android.view.components.navigation.ManagerBottomNavBar
import com.zera.android.view.components.navigation.ManagerSideBar
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.NoColor
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.transition.SharedElementKeys
import com.zera.android.view.transition.sharedTransition

private val TopFadeHeight = 15.dp
private val BottomFadeHeight = 25.dp
private val SideBarWidth = 280.dp

/**
 * Casca (Scaffold) compartilhada pelas telas da área do gestor.
 *
 * Já monta a [UpperNavBar] (com o [title] da tela), o [ManagerBottomNavBar] e o botão
 * flutuante de assistente virtual — com os insets de status bar / navigation bar já
 * aplicados. Cada tela só precisa passar o [title], a própria [currentRoute] e o
 * [content], que é desenhado dentro de uma [Column] rolável.
 *
 * @param title título exibido na [UpperNavBar].
 * @param currentRoute rota da própria tela, repassada ao [ManagerBottomNavBar] para
 *   destacar e desabilitar o atalho que levaria para a tela atual.
 * @param modifier modificador externo opcional, aplicado ao [Scaffold].
 * @param goBack quando `true`, exibe o botão "Voltar" na [UpperNavBar] — use em telas
 *   acessadas por navegação (ex.: um atalho do Home), diferente das telas raiz do
 *   [ManagerBottomNavBar] (ex.: [ManagerHomeScreen]).
 * @param onBackClick ação do botão "Voltar". Só é usada quando [goBack] é `true`.
 * @param fabIcon ícone do botão flutuante. Quando `null`, nenhum FAB é exibido.
 * @param onFabClick ação do botão flutuante. Só é usada quando [fabIcon] não for `null`.
 * @param scrollable quando `true` (padrão), a [Column] do conteúdo rola inteira. Use
 *   `false` quando [content] já tiver seu próprio elemento rolável (ex.: uma lista longa
 *   em [com.zera.android.view.components.lists.ProductList] com `Modifier.weight(1f)`) —
 *   caso contrário, um `LazyColumn` dentro de uma `Column` rolável quebra em tempo de execução.
 * @param backgroundVariant quando `true`, troca o fundo do Scaffold (corpo, gradientes e
 *   título/ícones da [UpperNavBar]) para [MaterialTheme.colorScheme.primary]/`onPrimary`,
 *   em vez do fundo neutro padrão. Não afeta a [ManagerBottomNavBar], que mantém sempre
 *   o mesmo estilo em qualquer tela.
 * @param edgeFade quando `true` (padrão), mostra o gradiente de fade no topo/fim do
 *   conteúdo. Com [scrollable] `true`, ele só aparece enquanto houver mais conteúdo a
 *   rolar naquela direção; com [scrollable] `false`, fica sempre visível — use nesse caso
 *   apenas quando [content] tiver seu próprio elemento rolável (ex.: uma lista com
 *   `Modifier.weight(1f)`). Passe `false` para desligar o fade por completo (ex.: telas
 *   sem nenhum scroll, como um mapa).
 * @param content conteúdo da tela, desenhado dentro da [Column] do Scaffold.
 *
 * O botão de menu da [UpperNavBar] abre a [ManagerSideBar] por cima da tela (com scrim). Ela
 * fecha ao tocar fora dela ou no botão voltar do sistema.
 */
@Composable
fun ManagerScaffold(
    title: String,
    currentRoute: Route,
    modifier: Modifier = Modifier,
    goBack: Boolean = false,
    onBackClick: () -> Unit = { ZeraNavigator.goBack() },
    fabIcon: ZeraIcon? = ZeraIcon.Chatbot,
    onFabClick: () -> Unit = {},
    scrollable: Boolean = true,
    contentPadding: Dp = Spacing.medium,
    backgroundVariant: Boolean = false,
    edgeFade: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val backgroundColor = if (backgroundVariant) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.background
    }

    var showSideBar by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = showSideBar) { showSideBar = false }

    Box(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = backgroundColor,
            topBar = {
                UpperNavBar(
                    title = title,
                    goBack = goBack,
                    onBackClick = onBackClick,
                    backgroundVariant = backgroundVariant,
                    onSideBarClick = { showSideBar = true },
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = Spacing.small),
                )
            },
            bottomBar = {
                ManagerBottomNavBar(
                    currentRoute = currentRoute,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = Spacing.medium, vertical = Spacing.small)
                        .sharedTransition(SharedElementKeys.ManagerBottomNavBar),
                )
            },
            floatingActionButton = {
                if (fabIcon != null) {
                    IconButton(
                        icon = fabIcon,
                        onClick = onFabClick,
                        contentDescription = "Assistente virtual",
                        size = 56.dp,
                    )
                }
            },
        ) { innerPadding ->
            val scrollState = rememberScrollState()
            val density = LocalDensity.current
            val topFadePx = with(density) { TopFadeHeight.toPx() }
            val bottomFadePx = with(density) { BottomFadeHeight.toPx() }

            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .then(if (scrollable) Modifier.verticalScroll(scrollState) else Modifier)
                        .padding(horizontal = contentPadding, vertical = Spacing.small)
                        .background(color = backgroundColor),
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                    content = content,
                )
                ZeraGradientBox(
                    brush = Brush.verticalGradient(
                        colors = listOf(backgroundColor, NoColor)
                    ),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .height(TopFadeHeight)
                        .graphicsLayer {
                            alpha = when {
                                !edgeFade -> 0f
                                // Sem scroll próprio (scrollable = false) o gradiente fica sempre visível.
                                !scrollable -> 1f
                                else -> (scrollState.value / topFadePx).coerceIn(0f, 1f)
                            }
                        }
                ){}
                ZeraGradientBox(
                    brush = Brush.verticalGradient(
                        colors = listOf(NoColor, backgroundColor)
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .height(BottomFadeHeight)
                        .graphicsLayer {
                            alpha = when {
                                !edgeFade -> 0f
                                !scrollable -> 1f
                                else -> ((scrollState.maxValue - scrollState.value) / bottomFadePx).coerceIn(0f, 1f)
                            }
                        }
                ){}
            }
        }

        AnimatedVisibility(
            visible = showSideBar,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { showSideBar = false },
                    ),
            )
        }

        AnimatedVisibility(
            visible = showSideBar,
            modifier = Modifier.align(Alignment.CenterEnd),
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
        ) {
            ManagerSideBar(
                currentRoute = currentRoute,
                modifier = Modifier
                    .width(SideBarWidth)
                    // Absorve toques na área da sidebar para não vazarem até o scrim e fecharem o menu.
                    .pointerInput(Unit) {}
                    .statusBarsPadding()
                    .navigationBarsPadding(),
            )
        }
    }
}

@Preview(heightDp = 600)
@Composable
private fun ManagerScaffoldPreview() {
    ZeraTheme {
        ManagerScaffold(title = "Visão geral", currentRoute = Route.ManagerHome) {
            BodyText(text = "Conteúdo da tela")
        }
    }
}