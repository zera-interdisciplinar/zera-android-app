package com.zera.android.view.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.containers.ZeraGradientBox
import com.zera.android.view.components.navigation.EmployeeBottomNavBar
import com.zera.android.view.components.navigation.EmployeeSideBarItems
import com.zera.android.view.components.navigation.SideBarOverlay
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

/**
 * Casca (Scaffold) compartilhada pelas telas da área do operário.
 *
 * Monta a [UpperNavBar] (com o [title] da tela), o [EmployeeBottomNavBar] e o botão
 * flutuante de assistente virtual, com os insets de status bar / navigation bar já
 * aplicados. O [content] é desenhado dentro de uma [Column] rolável.
 *
 * O botão de menu da [UpperNavBar] abre a [com.zera.android.view.components.navigation.SideBar]
 * (com os atalhos do operário) por cima da tela, com scrim. Ela fecha ao tocar fora dela ou
 * no botão voltar do sistema.
 *
 * @param title título exibido na [UpperNavBar].
 * @param currentRoute rota da própria tela, repassada ao [EmployeeBottomNavBar] e à sidebar
 *   para destacar o atalho da tela atual.
 * @param modifier modificador externo opcional, aplicado ao [Box] que envolve o [Scaffold].
 * @param goBack quando `true`, exibe o botão "Voltar" na [UpperNavBar].
 * @param onBackClick ação do botão "Voltar". Só é usada quando [goBack] é `true`.
 * @param showActions quando `true` (padrão), exibe os atalhos de notificações/perfil/menu
 *   na [UpperNavBar]. Use `false` em telas que só precisam do "Voltar" (ex.: "Scanear item").
 * @param fabIcon ícone do botão flutuante. Quando `null`, nenhum FAB é exibido.
 * @param onFabClick ação do botão flutuante. Só é usada quando [fabIcon] não for `null`.
 * @param scrollable quando `true` (padrão), a [Column] do conteúdo rola inteira. Use
 *   `false` quando [content] já tiver seu próprio elemento rolável.
 * @param contentPadding espaçamento horizontal do conteúdo.
 * @param edgeFade quando `true` (padrão), mostra o gradiente de fade no topo/fim do conteúdo.
 * @param content conteúdo da tela, desenhado dentro da [Column] do Scaffold.
 */
@Composable
fun EmployeeScaffold(
    title: String,
    currentRoute: Route,
    modifier: Modifier = Modifier,
    goBack: Boolean = false,
    onBackClick: () -> Unit = { ZeraNavigator.goBack() },
    showActions: Boolean = true,
    fabIcon: ZeraIcon? = ZeraIcon.Chatbot,
    onFabClick: () -> Unit = {},
    scrollable: Boolean = true,
    contentPadding: Dp = Spacing.medium,
    edgeFade: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val backgroundColor = MaterialTheme.colorScheme.background

    var showSideBar by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = backgroundColor,
            topBar = {
                UpperNavBar(
                    title = title,
                    goBack = goBack,
                    onBackClick = onBackClick,
                    showActions = showActions,
                    onSideBarClick = { showSideBar = true },
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = Spacing.small),
                )
            },
            bottomBar = {
                EmployeeBottomNavBar(
                    currentRoute = currentRoute,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = Spacing.medium, vertical = Spacing.small)
                        .sharedTransition(SharedElementKeys.EmployeeBottomNavBar),
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
                    brush = Brush.verticalGradient(colors = listOf(backgroundColor, NoColor)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .height(TopFadeHeight)
                        .graphicsLayer {
                            alpha = when {
                                !edgeFade -> 0f
                                !scrollable -> 1f
                                else -> (scrollState.value / topFadePx).coerceIn(0f, 1f)
                            }
                        },
                ) {}
                ZeraGradientBox(
                    brush = Brush.verticalGradient(colors = listOf(NoColor, backgroundColor)),
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
                        },
                ) {}
            }
        }

        SideBarOverlay(
            visible = showSideBar,
            onDismiss = { showSideBar = false },
            items = EmployeeSideBarItems,
            currentRoute = currentRoute,
        )
    }
}

@Preview(heightDp = 600)
@Composable
private fun EmployeeScaffoldPreview() {
    ZeraTheme {
        EmployeeScaffold(title = "Visão geral", currentRoute = Route.EmployeeHome) {
            BodyText(text = "Conteúdo da tela")
        }
    }
}
