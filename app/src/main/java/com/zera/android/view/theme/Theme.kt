package com.zera.android.view.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

// Espelha os papéis do LightColorScheme. Como o app usa `primary` também como cor de texto
// (ver os componentes de texto), no escuro ele vira um azul claro e `onPrimary` o azul escuro.
private val DarkColorScheme = darkColorScheme(
    primary = LightBlue80,
    onPrimary = DarkBlue15,
    primaryContainer = DarkBlue25,
    onPrimaryContainer = DarkBlue25,

    secondary = Orange50,
    onSecondary = DarkBlue15,
    secondaryContainer = DarkYellow20,
    onSecondaryContainer = LightYellow80,

    tertiary = Green50,
    onTertiary = DarkBlue15,
    tertiaryContainer = DarkGreen20,
    onTertiaryContainer = LightGreen80,

    error = Red50,
    onError = White100,
    errorContainer = DarkRed20,
    onErrorContainer = PastelRed85,

    background = DarkBlue15,
    onBackground = White95,
    surface = DarkBlue20,
    surfaceVariant = DarkBlue30,
    surfaceContainerLowest = DarkBlue10,
    surfaceContainerLow = DarkBlue30,
    onSurface = White95,
    onSurfaceVariant = Gray70,
    outline = DarkBlue40,
    scrim = TransparentBlack40,
)

internal val LocalZeraDarkTheme = staticCompositionLocalOf { false }

private val LightColorScheme = lightColorScheme(
    primary = DarkBlue25,
    onPrimary = White100,
    primaryContainer = White95,
    onPrimaryContainer = DarkBlue25,

    secondary = Orange50,
    onSecondary = DarkBlue25,
    secondaryContainer = PastelYellow95,
    onSecondaryContainer = DarkYellow35,

    tertiary = Green50,
    onTertiary = White100,
    tertiaryContainer = LightGreen90,
    onTertiaryContainer = DarkGreen30,


    error = Red50,
    errorContainer = PastelRed85,
    onErrorContainer = DarkRed40,

    background = PastelYellow90,
    surface = White100,
    surfaceContainerLowest = White95,
    surfaceContainerLow = White90,
    onSurface = DarkBlue15,
    onSurfaceVariant = Gray45,
    outline = LightPastelBlue90,
    scrim = TransparentBlue17,

)

@Composable
fun ZeraTheme(
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalZeraColors provides if (darkTheme) DarkZeraColors else LightZeraColors,
        LocalZeraDarkTheme provides darkTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}