package com.zera.android.view.components.containers

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.palette

/**
 * Família de cor do [ZeraBox].
 *
 * Espelho de [ZeraColorFamily] para a API dos containers; o [ZeraBoxType] decide
 * qual par do grupo (base ou container) é usado.
 */
enum class ZeraBoxStyle {
    Blue,
    Red,
    Yellow,
    Green,
}

/** Hierarquia visual do [ZeraBox] dentro de um [ZeraBoxStyle]. */
enum class ZeraBoxType {
    Primary,
    Secondary,
}

internal fun ZeraBoxStyle.family(): ZeraColorFamily = when (this) {
    ZeraBoxStyle.Blue -> ZeraColorFamily.Blue
    ZeraBoxStyle.Red -> ZeraColorFamily.Red
    ZeraBoxStyle.Yellow -> ZeraColorFamily.Yellow
    ZeraBoxStyle.Green -> ZeraColorFamily.Green
}

/**
 * Par (fundo, conteúdo) do [style] para o [type] escolhido.
 *
 * @param style família de cor. Ver [ZeraBoxStyle].
 * @param type hierarquia visual dentro do estilo. Ver [ZeraBoxType].
 */
@Composable
internal fun boxColorPair(
    style: ZeraBoxStyle,
    type: ZeraBoxType,
): Pair<Color, Color> {
    val palette = style.family().palette()
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
 * @param style família de cor. Ver [ZeraBoxStyle].
 * @param type hierarquia visual dentro do estilo. Ver [ZeraBoxType].
 */
@Composable
internal fun boxBorder(
    style: ZeraBoxStyle,
    type: ZeraBoxType,
): BorderStroke? {
    return when (type) {
        ZeraBoxType.Secondary -> BorderStroke(2.dp, style.family().palette().base.copy(alpha = 0.5f))
        else -> null
    }
}
