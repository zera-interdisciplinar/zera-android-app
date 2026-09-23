package com.zera.android.view.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette

/**
 * Card de notificação: um rótulo em destaque, um texto de apoio e, opcionalmente,
 * uma ação de "Resolver agora" ao final.
 *
 * A cor segue um dos [ZeraColorFamily] do [com.zera.android.view.theme.ZeraPalette]:
 * fundo e borda no tom container, rótulo e ação na cor de conteúdo desse container.
 *
 * @param label texto em destaque no topo do card.
 * @param text texto de apoio exibido abaixo do [label].
 * @param modifier modificador externo opcional.
 * @param style família de cor da notificação. Ver [ZeraColorFamily].
 * @param redirect ação de "Resolver agora →" exibida ao final do card. Quando `null`
 *   (padrão), esse texto clicável não é exibido.
 */
@Composable
fun Notification(
    label: String,
    text: String,
    modifier: Modifier = Modifier,
    style: ZeraColorFamily = ZeraColorFamily.Yellow,
    redirect: (() -> Unit)? = null,
) {
    val palette = style.palette()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Radius.xLarge),
        color = palette.container,
        contentColor = palette.onContainer,
        border = BorderStroke(1.dp, palette.base.copy(alpha = 0.4f)),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            BodyText(text = label, bold = true, color = palette.onContainer)
            BodyText(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (redirect != null) {
                Row(
                    modifier = Modifier.clickable(onClick = redirect),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.micro),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabelText(text = "Resolver agora", bold = true, color = palette.onContainer)
                    ZeraIcon(
                        icon = ZeraIcon.ProceedArrow,
                        contentDescription = null,
                        size = Spacing.medium,
                        tint = palette.onContainer,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Notification(
                label = "Completar cadastro",
                text = "Teclado mecânico · faltam danos",
                style = ZeraColorFamily.Yellow,
                redirect = {},
            )
            Notification(
                label = "Recusado pelo gestor",
                text = "Chip controlador",
                style = ZeraColorFamily.Red,
            )
            Notification(
                label = "Enviar para manutenção",
                text = "Mouse sem fio · aguardando destino",
                style = ZeraColorFamily.Blue,
                redirect = {},
            )
        }
    }
}
