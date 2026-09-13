package com.zera.android.view.theme.icons

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Valores padrão usados pelo composable `ZeraIcon(...)`. */
object ZeraIconDefaults {

    /** Tamanho padrão (largura e altura). Segue o padrão Material de 24dp. */
    val Size: Dp = 24.dp

    /**
     * Cor padrão do tint: a cor de conteúdo herdada do container
     * (`LocalContentColor`), igual ao comportamento do [Icon] do Material.
     */
    val Tint: Color
        @Composable get() = LocalContentColor.current
}
