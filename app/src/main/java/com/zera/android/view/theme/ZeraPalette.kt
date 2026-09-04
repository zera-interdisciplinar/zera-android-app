package com.zera.android.view.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Família de cor semântica do app, compartilhada por vários componentes (botões,
 * containers, ...). Cada valor aponta para um grupo de papéis do
 * [androidx.compose.material3.ColorScheme]: uma cor "base" mais forte e um
 * "container" mais claro, cada uma com sua cor de conteúdo.
 *
 * É o ponto único onde o mapa `família -> cores do tema` vive. Componentes
 * coloridos (`ZeraButton`, `ZeraBox`, `Tag`, ...) recebem [ZeraColorFamily]
 * diretamente como parâmetro `style`, sem enums espelho próprios.
 */
enum class ZeraColorFamily {
    Blue,
    Red,
    Yellow,
    Green,
}

/** Par (base, container) + cores de conteúdo resolvido de uma [ZeraColorFamily]. */
data class ZeraPalette(
    val base: Color,
    val onBase: Color,
    val container: Color,
    val onContainer: Color,
)

/** Resolve a [ZeraColorFamily] para cores concretas do tema atual. */
@Composable
fun ZeraColorFamily.palette(): ZeraPalette {
    val scheme = MaterialTheme.colorScheme
    return when (this) {
        ZeraColorFamily.Blue -> ZeraPalette(
            base = scheme.primary,
            onBase = scheme.onPrimary,
            container = scheme.primaryContainer,
            onContainer = scheme.onPrimaryContainer,
        )

        ZeraColorFamily.Red -> ZeraPalette(
            base = scheme.error,
            onBase = scheme.onError,
            container = scheme.errorContainer,
            onContainer = scheme.onErrorContainer,
        )

        ZeraColorFamily.Yellow -> ZeraPalette(
            base = scheme.secondary,
            onBase = scheme.onSecondary,
            container = scheme.secondaryContainer,
            onContainer = scheme.onSecondaryContainer,
        )

        ZeraColorFamily.Green -> ZeraPalette(
            base = scheme.tertiary,
            onBase = scheme.onTertiary,
            container = scheme.tertiaryContainer,
            onContainer = scheme.onTertiaryContainer,
        )
    }
}
