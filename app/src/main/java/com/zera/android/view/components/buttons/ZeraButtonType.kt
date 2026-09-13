package com.zera.android.view.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.palette

/** Hierarquia visual do [ZeraButton] dentro de uma [ZeraColorFamily]. */
enum class ZeraButtonType {
    Primary,
    Secondary,
    Tertiary,
}

/**
 * Par (fundo, conteúdo) do [style] para o [type] escolhido.
 *
 * @param style família de cor. Ver [ZeraColorFamily].
 * @param type hierarquia visual dentro do estilo. Ver [ZeraButtonType].
 */
@Composable
internal fun buttonColorPair(
    style: ZeraColorFamily,
    type: ZeraButtonType,
): Pair<Color, Color> {
    val palette = style.palette()
    return when (type) {
        ZeraButtonType.Primary -> palette.base to palette.onBase
        ZeraButtonType.Secondary -> palette.container to palette.onContainer
        ZeraButtonType.Tertiary -> Color.Transparent to palette.base
    }
}

/**
 * Borda do botão para o [type] escolhido.
 *
 * Botões [ZeraButtonType.Secondary] recebem uma borda na cor base do [style],
 * que contrasta com o fundo (container) mais claro. Demais tipos não têm borda.
 *
 * @param style família de cor. Ver [ZeraColorFamily].
 * @param type hierarquia visual dentro do estilo. Ver [ZeraButtonType].
 */
@Composable
internal fun buttonBorder(
    style: ZeraColorFamily,
    type: ZeraButtonType,
): BorderStroke? {
    return when (type) {
        ZeraButtonType.Secondary -> BorderStroke(2.dp, style.palette().base.copy(alpha = 0.5f))
        else -> null
    }
}

/**
 * [ButtonColors] (fundo + conteúdo) do [style] para o [type] escolhido, prontas
 * para o parâmetro `colors` do [androidx.compose.material3.Button].
 *
 * @param style família de cor. Ver [ZeraColorFamily].
 * @param type hierarquia visual dentro do estilo. Ver [ZeraButtonType].
 */
@Composable
internal fun buttonColors(
    style: ZeraColorFamily,
    type: ZeraButtonType,
): ButtonColors {
    val (container, content) = buttonColorPair(style, type)
    return ButtonDefaults.buttonColors(
        containerColor = container,
        contentColor = content,
    )
}
