package com.zera.android.view.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Montar esquema do tema escuro depois
private val DarkColorScheme = darkColorScheme(

    primary = DarkBlue25,
    secondary = Orange50,
    tertiary = Green50
)

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
    scrim = TransparentBlue17
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}