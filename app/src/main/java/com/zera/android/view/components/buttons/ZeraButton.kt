package com.zera.android.view.components.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

/**
 * Botão padrão do app.
 *
 * @param text texto exibido dentro do botão.
 * @param onClick ação executada ao tocar no botão.
 * @param modifier modificador externo opcional.
 * @param fillMaxWidth quando `true`, o botão ocupa toda a largura disponível do container.
 * @param style família de cor do botão. Ver [ZeraButtonStyle].
 * @param type hierarquia visual dentro do estilo. Ver [ZeraButtonType].
 * @param enabled habilita ou desabilita a interação.
 * @param icon ícone opcional do catálogo [ZeraIcon]. Quando não for `null`, é exibido
 *   logo ao lado do texto.
 * @param width largura fixa em dp. Quando informado, ignora [fillMaxWidth], [maxWidth] e [minWidth].
 * @param maxWidth largura máxima em dp (útil junto de [fillMaxWidth]). Ignorado se [width] for informado.
 * @param minWidth largura mínima em dp. Ignorado se [width] for informado.
 */
@Composable
fun ZeraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = false,
    style: ZeraButtonStyle = ZeraButtonStyle.Blue,
    type: ZeraButtonType = ZeraButtonType.Primary,
    enabled: Boolean = true,
    icon: ZeraIcon? = null,
    width: Int? = null,
    maxWidth: Int? = null,
    minWidth: Int? = null,
) {
    val (_, content) = buttonColorPair(style, type)
    Button(
        onClick = onClick,
        modifier = modifier
            .then(if (fillMaxWidth && width == null) Modifier.fillMaxWidth() else Modifier)
            .then(if (width != null) Modifier.width(width.dp) else Modifier)
            .then(
                if (width == null && (minWidth != null || maxWidth != null)) {
                    Modifier.widthIn(
                        min = minWidth?.dp ?: Dp.Unspecified,
                        max = maxWidth?.dp ?: Dp.Unspecified,
                    )
                } else Modifier
            ),
        enabled = enabled,
        colors = buttonColors(style, type),
        border = buttonBorder(style, type),
        shape = RoundedCornerShape(Radius.large),

    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.small),
        ){
            BodyText(
                text = text,
                bold = true,
                color = content,
                modifier = Modifier.padding(horizontal = Spacing.small)
            )
            if (icon != null) {
                ZeraIcon(
                    icon = icon,
                    contentDescription = null,
                    size = Spacing.large,
                    tint = content,
                    modifier = Modifier.padding(end = Spacing.medium),
                )
            }
        }
    }
}

@Preview
@Composable
private fun ZeraButtonPreview() {
    ZeraTheme {
        ZeraButton(
            text = "Continuar",
            onClick = {},
            modifier = Modifier.padding(Spacing.medium),
            style = ZeraButtonStyle.Yellow,
        )
    }
}

@Preview
@Composable
private fun ZeraButtonWithIconPreview() {
    ZeraTheme {
        ZeraButton(
            text = "Continuar",
            onClick = {},
            modifier = Modifier.padding(Spacing.medium),
            style = ZeraButtonStyle.Blue,
            icon = ZeraIcon.ProceedArrow,
        )
    }
}

@Preview
@Composable
private fun ZeraButtonSecondaryPreview() {
    ZeraTheme {
        ZeraButton(
            text = "Secundário",
            onClick = {},
            fillMaxWidth = true,
            style = ZeraButtonStyle.Yellow,
            type = ZeraButtonType.Secondary,
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
