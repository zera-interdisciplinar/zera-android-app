package com.zera.android.view.screens.manager

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.containers.ZeraGradientBox
import com.zera.android.view.components.navigation.ManagerBottomNavBar
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.NoColor
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

private val TopFadeHeight = 15.dp
private val BottomFadeHeight = 25.dp

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
 * @param content conteúdo da tela, desenhado dentro da [Column] do Scaffold.
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
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = title,
                goBack = goBack,
                onBackClick = onBackClick,
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
                    .padding(horizontal = Spacing.medium, vertical = Spacing.small),
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
                    .padding(horizontal = Spacing.medium, vertical = Spacing.small)
                    .background(color = MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                content = content,
            )
            ZeraGradientBox(
                brush = Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.background, NoColor)
                ),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .height(TopFadeHeight)
                    .graphicsLayer {
                        // Sem scroll próprio (scrollable = false) o gradiente fica sempre visível.
                        alpha = if (scrollable) (scrollState.value / topFadePx).coerceIn(0f, 1f) else 1f
                    }
            ){}
            ZeraGradientBox(
                brush = Brush.verticalGradient(
                    colors = listOf(NoColor, MaterialTheme.colorScheme.background)
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .height(BottomFadeHeight)
                    .graphicsLayer {
                        alpha = if (scrollable) {
                            ((scrollState.maxValue - scrollState.value) / bottomFadePx).coerceIn(0f, 1f)
                        } else {
                            1f
                        }   
                    }
            ){}
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
