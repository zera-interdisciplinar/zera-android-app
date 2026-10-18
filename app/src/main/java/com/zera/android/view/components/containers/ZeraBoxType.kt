package com.zera.android.view.components.containers

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.palette
import com.zera.android.view.theme.zeraColors

/** Hierarquia visual do [ZeraBox] dentro de uma [ZeraColorFamily]. */
enum class ZeraBoxType {
    Primary,
    Secondary,
}

/**
 * Par (fundo, conteúdo) do [style] para o [type] escolhido.
 *
 * @param style família de cor. Ver [ZeraColorFamily].
 * @param type hierarquia visual dentro do estilo. Ver [ZeraBoxType].
 */
@Composable
internal fun boxColorPair(
    style: ZeraColorFamily,
    type: ZeraBoxType,
): Pair<Color, Color> {
    val palette = style.palette()
    val zeraColors = MaterialTheme.zeraColors
    return when (type) {
        // O azul sólido usa o brandContainer: igual ao primary no claro, e a cor dos cards no escuro.
        ZeraBoxType.Primary -> if (style == ZeraColorFamily.Blue) {
            zeraColors.brandContainer to zeraColors.onBrandContainer
        } else {
            palette.base to palette.onBase
        }
        ZeraBoxType.Secondary -> palette.container to palette.onContainer
    }
}

/**
 * Borda do box para o [type] escolhido.
 *
 * Boxes [ZeraBoxType.Secondary] recebem uma borda na cor base do [style], que
 * contrasta com o fundo (container) mais claro. Demais tipos não têm borda.
 *
 * @param style família de cor. Ver [ZeraColorFamily].
 * @param type hierarquia visual dentro do estilo. Ver [ZeraBoxType].
 */
@Composable
internal fun boxBorder(
    style: ZeraColorFamily,
    type: ZeraBoxType,
): BorderStroke? {
    return when (type) {
        ZeraBoxType.Secondary -> BorderStroke(2.dp, style.palette().base.copy(alpha = 0.5f))
        else -> null
    }
}
