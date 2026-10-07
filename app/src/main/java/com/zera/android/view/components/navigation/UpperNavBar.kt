package com.zera.android.view.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.transition.ScreenAnimation

/**
 * Barra superior das telas internas do app.
 *
 * Ocupa a largura toda, com os cantos de baixo arredondados, e divide o conteúdo
 * em dois blocos: à esquerda o botão "Voltar" (opcional) com o título da tela
 * logo abaixo; à direita os atalhos de notificações e de perfil.
 *
 * O clique em "Voltar" e em "Perfil" já navega (via [ZeraNavigator]). Notificações e
 * menu ainda estão como `TODO`.
 *
 * @param title título da tela, exibido abaixo do botão "Voltar". Quando [goBack] é
 *   `false`, ele ocupa também o espaço que seria do botão.
 * @param modifier modificador externo opcional.
 * @param goBack quando `true`, exibe o botão "Voltar" acima do título.
 * @param onBackClick ação do botão "Voltar". Só é usada quando [goBack] é `true`.
 * @param showActions quando `true` (padrão), exibe os atalhos de notificações/perfil/menu
 *   à direita. Use `false` em telas que só precisam do botão "Voltar" (ex.: "Perfil").
 * @param backgroundVariant quando `true`, adapta título, "Voltar" e ícones para
 *   [MaterialTheme.colorScheme.onPrimary] — use quando a barra estiver sobre um fundo
 *   [MaterialTheme.colorScheme.primary] (ver [com.zera.android.view.screens.manager.ManagerScaffold]).
 * @param onSideBarClick ação do botão de menu (abre a [ManagerSideBar] no
 *   [com.zera.android.view.screens.manager.ManagerScaffold]).
 */
@Composable
fun UpperNavBar(
    title: String,
    modifier: Modifier = Modifier,
    goBack: Boolean = false,
    onBackClick: () -> Unit = { ZeraNavigator.goBack() },
    showActions: Boolean = true,
    backgroundVariant: Boolean = false,
    onSideBarClick: () -> Unit = {}
) {
    val contentColor = if (backgroundVariant) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier.fillMaxWidth().padding(Spacing.small)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.micro),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (goBack) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TopBarIconButton(
                                icon = ZeraIcon.LeftUTurn,
                                onClick = onBackClick,
                                contentDescription = "Voltar",
                                type = ZeraButtonType.Tertiary,
                                backgroundVariant = backgroundVariant,
                            )
                            BodyText(
                                text = "Voltar",
                                bold = true,
                                color = contentColor,
                            )
                        }
                    }
                }

                Box {
                    if (showActions) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TopBarIconButton(
                                icon = ZeraIcon.Bell,
                                onClick = { /* TODO: adicionar rota para notificações */ },
                                contentDescription = "Notificações",
                                type = ZeraButtonType.Secondary,
                                backgroundVariant = backgroundVariant,
                            )
                            TopBarIconButton(
                                icon = ZeraIcon.Profile,
                                onClick = { ZeraNavigator.push(Route.Profile, animation = ScreenAnimation.SlideHorizontal) },
                                contentDescription = "Perfil",
                                type = if(!backgroundVariant) ZeraButtonType.Primary else ZeraButtonType.Base,
                                backgroundVariant = backgroundVariant,
                            )
                            TopBarIconButton(
                                icon = ZeraIcon.Menu,
                                onClick = onSideBarClick,
                                contentDescription = "Menu",
                                type = if(!backgroundVariant) ZeraButtonType.Tertiary else ZeraButtonType.Primary,
                                backgroundVariant = backgroundVariant,
                            )
                        }
                    }
                }
            }

            TitleText(
                text = title,
                bold = true,
                color = contentColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Ícone de ação da [UpperNavBar].
 *
 * Sem [backgroundVariant], é só o [IconButton] normal (bolinha colorida). Com
 * [backgroundVariant], vira um ícone simples sem fundo, tintado com
 * [MaterialTheme.colorScheme.onPrimary] — o círculo colorido do [IconButton]
 * não faria sentido sobre o próprio fundo primary da barra.
 */
@Composable
private fun TopBarIconButton(
    icon: ZeraIcon,
    onClick: () -> Unit,
    contentDescription: String?,
    type: ZeraButtonType,
    backgroundVariant: Boolean,
    size: Dp = 34.dp,
) {
    IconButton(
        icon = icon,
        onClick = onClick,
        contentDescription = contentDescription,
        type = type,
        size = size,
    )
}


@Preview(showBackground = true)
@Composable
private fun UpperNavBarWithBackPreview() {
    ZeraTheme {
        UpperNavBar(title = "Modelos", goBack = true)
    }
}

@Preview(showBackground = true, backgroundColor = 1)
@Composable
private fun UpperNavBarWithoutBackPreview() {
    ZeraTheme {
        UpperNavBar(title = "Modelos", backgroundVariant = true)
    }
}

@Preview(showBackground = true)
@Composable
private fun UpperNavBarWithoutActionsPreview() {
    ZeraTheme {
        UpperNavBar(title = "Perfil", goBack = true, showActions = false)
    }
}
