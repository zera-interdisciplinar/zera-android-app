package com.zera.android.view.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.containers.ZeraBoxType
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
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
 * ações pelos callbacks ([onBackClick], [onNotificationsClick], [onProfileClick]).
 *
 * @param title título da tela, exibido abaixo do botão "Voltar". Quando [goBack] é
 *   `false`, ele ocupa também o espaço que seria do botão.
 * @param modifier modificador externo opcional.
 * @param goBack quando `true`, exibe o botão "Voltar" acima do título.
 * @param onBackClick ação do botão "Voltar". Só é usada quando [goBack] é `true`.
 * @param onNotificationsClick ação do botão de notificações.
 * @param onProfileClick ação do botão de perfil.
 */
@Composable
fun UpperNavBar(
    title: String,
    modifier: Modifier = Modifier,
    goBack: Boolean = false,
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(Spacing.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
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
                            onClick = { /*TODO: adicionar goBack*/},
                            contentDescription = "Voltar",
                            type = ZeraButtonType.Tertiary,
                        )
                        BodyText(
                            text = "Voltar",
                            bold = true,
                        )
                    }
                }
                HeadlineText(
                    text = title,
                    bold = true,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = Spacing.small),
                )
            }

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
