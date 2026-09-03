package com.zera.android.view.components.buttons

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ZeraButtonType {
    Primary,
    Secondary,
}

/** Par (fundo, conteúdo) do [style] para o [type] escolhido. */
@Composable
internal fun buttonColorPair(
    style: ZeraButtonStyle,
    type: ZeraButtonType,
): Pair<Color, Color> {
    val palette = style.palette()
    return when (type) {
        ZeraButtonType.Primary -> palette.base to palette.onBase
        ZeraButtonType.Secondary -> palette.container to palette.onContainer
    }
}

@Composable
internal fun buttonColors(
    style: ZeraButtonStyle,
    type: ZeraButtonType,
): ButtonColors {
    val (container, content) = buttonColorPair(style, type)
    return ButtonDefaults.buttonColors(
        containerColor = container,
        contentColor = content,
    )
}
