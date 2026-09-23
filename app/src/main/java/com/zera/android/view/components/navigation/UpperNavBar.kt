package com.zera.android.view.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

/**
 * Barra superior das telas internas do app.
 *
 * Ocupa a largura toda, com os cantos de baixo arredondados, e divide o conteúdo
 * em dois blocos: à esquerda o botão "Voltar" (opcional) com o título da tela
 * logo abaixo; à direita os atalhos de notificações e de perfil.
 *
 * Este componente é apenas visual — ele não conhece rotas nem navegação. Passe as
 * ações pelos callbacks ([onBackClick] e, ainda como `TODO`, notificações/perfil/menu).
 *
 * @param title título da tela, exibido abaixo do botão "Voltar". Quando [goBack] é
 *   `false`, ele ocupa também o espaço que seria do botão.
 * @param modifier modificador externo opcional.
 * @param goBack quando `true`, exibe o botão "Voltar" acima do título.
 * @param onBackClick ação do botão "Voltar". Só é usada quando [goBack] é `true`.
 * @param showActions quando `true` (padrão), exibe os atalhos de notificações/perfil/menu
 *   à direita. Use `false` em telas que só precisam do botão "Voltar" (ex.: "Perfil").
 */
@Composable
fun UpperNavBar(
    title: String,
    modifier: Modifier = Modifier,
    goBack: Boolean = false,
    onBackClick: () -> Unit = { ZeraNavigator.goBack() },
    showActions: Boolean = true,
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(Spacing.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.micro),
            ) {
                if (goBack) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            icon = ZeraIcon.LeftUTurn,
                            onClick = onBackClick,
                            contentDescription = "Voltar",
                            type = ZeraButtonType.Tertiary,
                        )
                        BodyText(
                            text = "Voltar",
                            bold = true,
                        )
                    }
                }
                TitleText(
                    text = title,
                    bold = true,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (showActions) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        icon = ZeraIcon.Bell,
                        onClick = { /* TODO: adicionar rota para notificações */ },
                        contentDescription = "Notificações",
                        type = ZeraButtonType.Secondary,
                    )
                    IconButton(
                        icon = ZeraIcon.Profile,
                        onClick = { /* TODO: adicionar rota para perfil */ },
                        contentDescription = "Perfil",
                        type = ZeraButtonType.Primary,
                    )
                    IconButton(
                        icon = ZeraIcon.Menu,
                        onClick = {},
                        contentDescription = "Menu",
                        type = ZeraButtonType.Tertiary
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun UpperNavBarWithBackPreview() {
    ZeraTheme {
        UpperNavBar(title = "Modelos", goBack = true)
    }
}

@Preview(showBackground = true)
@Composable
private fun UpperNavBarWithoutBackPreview() {
    ZeraTheme {
        UpperNavBar(title = "Modelos")
    }
}

@Preview(showBackground = true)
@Composable
private fun UpperNavBarWithoutActionsPreview() {
    ZeraTheme {
        UpperNavBar(title = "Perfil", goBack = true, showActions = false)
    }
}
