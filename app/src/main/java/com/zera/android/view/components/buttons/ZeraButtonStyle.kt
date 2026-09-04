package com.zera.android.view.components.buttons

import androidx.compose.runtime.Composable
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraPalette
import com.zera.android.view.theme.palette

/**
 * Família de cor do [ZeraButton].
 *
 * Espelho de [ZeraColorFamily] para a API dos botões; o [ZeraButtonType] decide
 * qual par do grupo (base ou container) é usado.
 */
enum class ZeraButtonStyle {
    Blue,
    Red,
    Yellow,
    Green,
}

internal fun ZeraButtonStyle.family(): ZeraColorFamily = when (this) {
    ZeraButtonStyle.Blue -> ZeraColorFamily.Blue
    ZeraButtonStyle.Red -> ZeraColorFamily.Red
    ZeraButtonStyle.Yellow -> ZeraColorFamily.Yellow
    ZeraButtonStyle.Green -> ZeraColorFamily.Green
}

@Composable
internal fun ZeraButtonStyle.palette(): ZeraPalette = family().palette()
