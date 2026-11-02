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
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette

private val DismissButtonSize = 32.dp

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
 * @param redirect ação exibida ao final do card (por padrão "Resolver agora →"). Quando
 *   `null` (padrão), esse texto clicável não é exibido.
 * @param redirectLabel texto da ação de [redirect].
 * @param neutral quando `true`, ignora o [style] e usa o fundo de superfície, sem borda
 *   colorida, com a ação na cor primária — visual das notificações antigas, já sem urgência.
 * @param onDismiss ação do X no canto do card. Quando `null` (padrão), o X não é exibido.
 */
@Composable
fun Notification(
    label: String,
    modifier: Modifier = Modifier,
    style: ZeraColorFamily = ZeraColorFamily.Yellow,
    text: String? = null,
    redirect: (() -> Unit)? = null,
    redirectLabel: String = "Resolver agora",
    neutral: Boolean = false,
    onDismiss: (() -> Unit)? = null,
) {
    val palette = style.palette()
    val containerColor = if (neutral) MaterialTheme.colorScheme.surface else palette.container
    val labelColor = if (neutral) MaterialTheme.colorScheme.onSurface else palette.onContainer
    val actionColor = if (neutral) MaterialTheme.colorScheme.primary else palette.onContainer
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Radius.xLarge),
        color = containerColor,
        contentColor = labelColor,
        border = if (neutral) null else BorderStroke(1.dp, palette.base.copy(alpha = 0.4f)),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            if (onDismiss != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    BodyText(
                        text = label,
                        bold = true,
                        color = labelColor,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(
                        icon = ZeraIcon.Close,
                        onClick = onDismiss,
                        contentDescription = "Dispensar alerta",
                        style = style,
                        type = ZeraButtonType.Tertiary,
                        size = DismissButtonSize,
                    )
                }
            } else {
                BodyText(text = label, bold = true, color = labelColor)
            }
            if(text!=null){
                BodyText(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (redirect != null) {
                Row(
                    modifier = Modifier.clickable(onClick = redirect),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.micro),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabelText(text = redirectLabel, bold = true, color = actionColor)
                    ZeraIcon(
                        icon = ZeraIcon.ProceedArrow,
                        contentDescription = null,
                        size = Spacing.medium,
                        tint = actionColor,
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
            Notification(
                label = "Convite de Carol expira hoje",
                redirect = {},
                redirectLabel = "Reenviar convite",
                neutral = true,
            )
            Notification(
                label = "Item aprovado pelo gestor",
                style = ZeraColorFamily.Green,
                onDismiss = {},
            )
        }
    }
}
