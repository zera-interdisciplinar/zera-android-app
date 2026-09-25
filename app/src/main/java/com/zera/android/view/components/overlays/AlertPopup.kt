package com.zera.android.view.components.overlays

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette

/** Diâmetro do círculo de fundo do ícone. */
private val IconCircleSize = 96.dp

/** Tamanho do ícone dentro do círculo. */
private val IconSize = 40.dp

/**
 * Popup de confirmação com duas opções: um botão "Cancelar" à esquerda e um botão de
 * confirmação à direita (ex.: "Excluir"). Construído sobre o [PopupDialog], sem o X de
 * fechar — tocar fora, apertar voltar ou tocar em "Cancelar" chamam [onDismissRequest].
 *
 * O chamador controla a visibilidade e só compõe o [AlertPopup] com `if (...) { ... }`,
 * mesma convenção do [PopupDialog]. O popup não se fecha sozinho ao confirmar: feche-o
 * em [onConfirm].
 *
 * @param title título em destaque (ex.: "Excluir esse item?").
 * @param message texto explicativo abaixo do título.
 * @param confirmButtonText texto do botão da direita (o da esquerda é sempre "Cancelar").
 * @param onConfirm ação executada ao tocar no botão de confirmação.
 * @param onDismissRequest chamado ao tocar em "Cancelar", fora do popup ou em voltar.
 * @param icon ícone exibido no círculo do topo.
 * @param iconStyle família de cor do ícone (base) e do círculo de fundo (container).
 * @param buttonStyle família de cor do botão de confirmação.
 * @param modifier modificador externo opcional.
 */
@Composable
fun AlertPopup(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    icon: ZeraIcon = ZeraIcon.Exclamation,
    iconStyle: ZeraColorFamily = ZeraColorFamily.Red,
    buttonStyle: ZeraColorFamily = ZeraColorFamily.Red,
) {
    val iconPalette = iconStyle.palette()
    PopupDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        hasDismissButton = false,
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
        BodyText(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            ZeraButton(
                text = "Cancelar",
                onClick = onDismissRequest,
                modifier = Modifier.weight(1f),
                fillMaxWidth = true,
                type = ZeraButtonType.Secondary,
            )
            ZeraButton(
                text = confirmButtonText,
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
                fillMaxWidth = true,
                style = buttonStyle,
            )
        }
    }
}

@Preview
@Composable
private fun AlertPopupPreview() {
    ZeraTheme {
        AlertPopup(
            title = "Excluir esse item?",
            message = "Essa decisão poderá ser revisada pelo gestor.",
            confirmButtonText = "Excluir",
            onConfirm = {},
            onDismissRequest = {},
        )
    }
}
