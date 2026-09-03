package com.zera.android.view.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Escala de espaçamento do app, baseada num grid de 4/8dp.
 *
 * Use estes tokens em vez de valores `x.dp` soltos para `padding`, `Spacer`,
 * `Arrangement.spacedBy` e afins, mantendo o espaçamento consistente entre telas.
 */
object Spacing {
    /** 0dp — remove espaçamento herdado. */
    val none: Dp = 0.dp

    /** 4dp */
    val micro: Dp = 4.dp

    /** 8dp */
    val small: Dp = 8.dp

    /** 16dp */
    val medium: Dp = 16.dp

    /** 24dp */
    val large: Dp = 24.dp

    /** 32dp */
    val xLarge: Dp = 32.dp

    /** 64dp */
    val huge: Dp = 64.dp
}
