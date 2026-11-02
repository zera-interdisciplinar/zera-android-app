package com.zera.android.view.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.zera.android.view.theme.ZeraTheme

/** Renderiza [content] dentro do [ZeraTheme], como nas telas do app. */
fun ComposeContentTestRule.setZeraContent(content: @Composable () -> Unit) {
    setContent { ZeraTheme(content = content) }
}
