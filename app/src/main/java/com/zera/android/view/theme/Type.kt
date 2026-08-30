package com.zera.android.view.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.zera.android.R

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
)

// Material 3 type scale, keeping the default metrics and swapping only the font family.
private val default = Typography()

val Typography = Typography(
    displayLarge = default.displayLarge.copy(fontFamily = Inter),
    displayMedium = default.displayMedium.copy(fontFamily = Inter),
    displaySmall = default.displaySmall.copy(fontFamily = Inter),
    headlineLarge = default.headlineLarge.copy(fontFamily = Inter),
    headlineMedium = default.headlineMedium.copy(fontFamily = Inter),
    headlineSmall = default.headlineSmall.copy(fontFamily = Inter),
    titleLarge = default.titleLarge.copy(fontFamily = Inter),
    titleMedium = default.titleMedium.copy(fontFamily = Inter),
    titleSmall = default.titleSmall.copy(fontFamily = Inter),
    bodyLarge = default.bodyLarge.copy(fontFamily = Inter),
    bodyMedium = default.bodyMedium.copy(fontFamily = Inter),
    bodySmall = default.bodySmall.copy(fontFamily = Inter),
    labelLarge = default.labelLarge.copy(fontFamily = Inter),
    labelMedium = default.labelMedium.copy(fontFamily = Inter),
    labelSmall = default.labelSmall.copy(fontFamily = Inter),
)
