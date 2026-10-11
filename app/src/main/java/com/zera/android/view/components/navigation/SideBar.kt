package com.zera.android.view.components.navigation

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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.logo.Logo
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

private val SideBarWidth = 280.dp

/**
 * Atalho exibido na [SideBar].
 *
 * @param label texto do botão.
 * @param icon ícone do botão.
 * @param route rota de destino. Quando `null`, a tela de destino ainda não existe: o botão
 *   aparece, mas só fecha a sidebar (nunca fica destacado como "tela atual").
 */
data class SideBarItem(
    val label: String,
    val icon: ZeraIcon,
    val route: Route? = null,
)

/**
 * Atalhos fixos, iguais para qualquer fluxo (gestor e operário). Ficam sempre no rodapé da
 * [SideBar] e são desenhados por ela mesma, sem precisar vir em `items`.
 */
private val FixedSideBarItems = listOf(
    SideBarItem(label = "Central de Ajuda", icon = ZeraIcon.InfoCircle), // TODO: rota da Central de Ajuda
    SideBarItem(label = "Fale com o Zé", icon = ZeraIcon.Chatbot), // TODO: rota do chatbot
    SideBarItem(label = "Configurações", icon = ZeraIcon.Gear), // TODO: rota de Configurações
)

/**
 * Sidebar de navegação, usada pelos fluxos do gestor e do operário.
 *
 * Mostra a logo, a lista [items] (rolável) e, no rodapé, os atalhos fixos de Central de Ajuda,
 * Fale com o Zé e Configurações — esses três sempre aparecem, não importa o fluxo.
 *
 * Funciona como as barras inferiores: recebe a [currentRoute] da tela em que está e destaca
 * (e desabilita) o botão que levaria para ela.
 *
 * @param items atalhos específicos do fluxo, na ordem em que devem aparecer. Ver
 *   [ManagerSideBarItems] e [EmployeeSideBarItems].
 * @param currentRoute rota da tela atual, usada para destacar o botão correspondente.
 * @param modifier modificador externo opcional.
 * @param onButtonClicked chamado após qualquer clique em um botão (ex.: para fechar a sidebar).
 */
@Composable
fun SideBar(
    items: List<SideBarItem>,
    currentRoute: Route,
    modifier: Modifier = Modifier,
    onButtonClicked: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(topStart = Radius.xLarge, bottomStart = Radius.xLarge),
            )
            .padding(horizontal = Spacing.medium, vertical = Spacing.large)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        Box(
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Logo(
                modifier = Modifier
                    .padding(start = Spacing.small, bottom = Spacing.medium)
                    .width(120.dp),
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            items.forEach { item ->
                SideBarButton(item = item, currentRoute = currentRoute, onButtonClicked = onButtonClicked)
            }
        }
        Column {
            FixedSideBarItems.forEach { item ->
                SideBarButton(item = item, currentRoute = currentRoute, onButtonClicked = onButtonClicked)
            }
        }
    }
}

/**
 * Camada que exibe a [SideBar] por cima da tela, com scrim escuro, deslizando da direita.
 * Fecha ao tocar fora dela ou no botão voltar do sistema.
 *
 * Deve ser chamada dentro de um `Box` que cubra a tela toda (ver `ManagerScaffold` e
 * `EmployeeScaffold`).
 *
 * @param visible se a sidebar está aberta.
 * @param onDismiss chamado para fechar a sidebar (toque no scrim, botão voltar ou clique em um atalho).
 * @param items atalhos específicos do fluxo, repassados à [SideBar].
 * @param currentRoute rota da tela atual, repassada à [SideBar].
 */
@Composable
fun BoxScope.SideBarOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    items: List<SideBarItem>,
    currentRoute: Route,
) {
    BackHandler(enabled = visible, onBack = onDismiss)

    AnimatedVisibility(
        visible = visible,
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
                    onClick = onDismiss,
                ),
        )
    }

    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.align(Alignment.CenterEnd),
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
    ) {
        SideBar(
            items = items,
            currentRoute = currentRoute,
            onButtonClicked = onDismiss,
            modifier = Modifier
                .width(SideBarWidth)
                // Absorve toques na área da sidebar para não vazarem até o scrim e fecharem o menu.
                .pointerInput(Unit) {}
                .statusBarsPadding()
                .navigationBarsPadding(),
        )
    }
}

/**
 * Botão da [SideBar]: ícone + rótulo em linha. Quando é a tela atual, vira uma pílula
 * clara com conteúdo escuro e fica desabilitado (a tela atual não precisa de navegação).
 */
@Composable
private fun SideBarButton(
    item: SideBarItem,
    currentRoute: Route,
    onButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = item.route != null && item.route == currentRoute
    val contentColor =
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onPrimary

    Button(
        onClick = {
            if (item.route != null && item.route != currentRoute) {
                ZeraNavigator.push(item.route)
            }
            onButtonClicked()
        },
        modifier = modifier.fillMaxWidth(),
        enabled = !selected,
        contentPadding = PaddingValues(horizontal = Spacing.small, vertical = Spacing.small),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
            disabledContentColor = MaterialTheme.colorScheme.primary,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ZeraIcon(
                icon = item.icon,
                contentDescription = "Botão para ${item.label}",
                tint = contentColor,
            )
            LabelText(item.label, color = contentColor, bold = selected, maxLines = 2)
        }
    }
}

@Composable
@Preview(heightDp = 900)
fun ManagerSideBarPreview() {
    ZeraTheme {
        SideBar(items = ManagerSideBarItems, currentRoute = Route.Profile)
    }
}

@Composable
@Preview(heightDp = 900)
fun EmployeeSideBarPreview() {
    ZeraTheme {
        SideBar(items = EmployeeSideBarItems, currentRoute = Route.EmployeeItems)
    }
}
