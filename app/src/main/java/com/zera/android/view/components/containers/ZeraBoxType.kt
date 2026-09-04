package com.zera.android.view.components.containers

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.palette

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
    return when (type) {
        ZeraBoxType.Primary -> palette.base to palette.onBase
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
