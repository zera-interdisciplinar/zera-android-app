package com.zera.android.view.components.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.overlays.PopupDialog
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette

/** Diâmetro do círculo de fundo do ícone. */
private val IconCircleSize = 96.dp

/** Tamanho do ícone dentro do círculo. */
private val IconSize = 40.dp

/**
 * Popup de feedback (ex.: "Alteração salva!"): ícone no topo, título e subtítulo opcional,
 * sem botões de ação. Construído sobre o [PopupDialog], com o X de fechar — tocar no X,
 * fora do popup ou apertar voltar chamam [onDismissRequest].
 *
 * O chamador controla a visibilidade e só compõe o [FeedbackPopup] com `if (...) { ... }`,
 * mesma convenção do [PopupDialog].
 *
 * @param title título em destaque (ex.: "Alteração salva!").
 * @param onDismissRequest chamado ao tocar no X, fora do popup ou em voltar.
 * @param subtitle texto explicativo abaixo do título; omitido quando `null`.
 * @param icon ícone exibido no círculo do topo.
 * @param iconStyle família de cor do ícone (base) e do círculo de fundo (container).
 * @param modifier modificador externo opcional.
 */
@Composable
fun FeedbackPopup(
    modifier: Modifier = Modifier,
    title: String,
    onDismissRequest: () -> Unit,
    subtitle: String? = null,
    icon: ZeraIcon = ZeraIcon.Check,
    iconStyle: ZeraColorFamily = ZeraColorFamily.Green,
) {
    val iconPalette = iconStyle.palette()
    PopupDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .size(IconCircleSize)
                .background(iconPalette.container, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            ZeraIcon(
                icon = icon,
                contentDescription = null,
                size = IconSize,
                tint = iconPalette.base,
            )
        }
        TitleText(
            text = title,
            bold = true,
            textAlign = TextAlign.Center,
        )
        if (subtitle != null) {
            BodyText(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
private fun FeedbackPopupPreview() {
    ZeraTheme {
        FeedbackPopup(
            title = "Alteração salva!",
            subtitle = "Seu item voltará a ficar em aprovação e seu gestor será notificado",
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
private fun FeedbackPopupWithoutSubtitlePreview() {
    ZeraTheme {
        FeedbackPopup(
            title = "Alteração salva!",
            onDismissRequest = {},
        )
    }
}
