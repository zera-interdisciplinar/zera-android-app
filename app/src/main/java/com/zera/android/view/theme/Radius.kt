package com.zera.android.view.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Escala de raio de canto (corner radius) do app.
 *
 * Use estes tokens com `RoundedCornerShape(Radius.medium)` em vez de valores
 * `x.dp` soltos, mantendo o arredondamento consistente entre componentes.
 * Para formas totalmente circulares/pílula, use `CircleShape` diretamente.
 */
object Radius {
    /** 0dp — canto reto. */
    val none: Dp = 0.dp

    /** 4dp */
    val small: Dp = 4.dp

    /** 8dp */
    val medium: Dp = 8.dp

    /** 16dp */
    val large: Dp = 16.dp

    /** 24dp */
    val xLarge: Dp = 24.dp
}
