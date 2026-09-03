package com.zera.android.view.components.buttons

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Família de cor do [ZeraButton].
 *
 * Cada valor aponta para um grupo de papéis do [androidx.compose.material3.ColorScheme]
 * (cor base + container). O [ZeraButtonType] decide qual par do grupo é usado.
 */
enum class ZeraButtonStyle {
    Blue,
    Red,
    Yellow,
    Green,
}
internal data class ZeraButtonPalette(
    val base: Color,
    val onBase: Color,
    val container: Color,
    val onContainer: Color,
)
@Composable
internal fun ZeraButtonStyle.palette(): ZeraButtonPalette {
    val scheme = MaterialTheme.colorScheme
    return when (this) {
        ZeraButtonStyle.Blue -> ZeraButtonPalette(
            base = scheme.primary,
            onBase = scheme.onPrimary,
            container = scheme.primaryContainer,
            onContainer = scheme.onPrimaryContainer,
        )

        ZeraButtonStyle.Red -> ZeraButtonPalette(
            base = scheme.error,
            onBase = scheme.onError,
            container = scheme.errorContainer,
            onContainer = scheme.onErrorContainer,
        )

        ZeraButtonStyle.Yellow -> ZeraButtonPalette(
            base = scheme.secondary,
            onBase = scheme.onSecondary,
            container = scheme.secondaryContainer,
            onContainer = scheme.onSecondaryContainer,
        )

        ZeraButtonStyle.Green -> ZeraButtonPalette(
            base = scheme.tertiary,
            onBase = scheme.onTertiary,
            container = scheme.tertiaryContainer,
            onContainer = scheme.onTertiaryContainer,
        )
    }
}
