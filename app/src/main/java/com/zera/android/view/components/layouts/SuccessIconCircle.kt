package com.zera.android.view.components.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette

/** Diâmetro do círculo de ícone. */
private val IconCircleSize = 96.dp

/** Círculo verde com um check, usado no topo das telas de sucesso. */
@Composable
fun SuccessIconCircle(modifier: Modifier = Modifier) {
    val checkPalette = ZeraColorFamily.Green.palette()
    Surface(
        modifier = modifier.size(IconCircleSize),
        shape = CircleShape,
        color = checkPalette.container,
        contentColor = checkPalette.onContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            ZeraIcon(icon = ZeraIcon.Check, contentDescription = null, tint = checkPalette.onContainer)
        }
    }
}

@Composable
@Preview
private fun SuccessIconCirclePreview() {
    ZeraTheme {
        SuccessIconCircle()
    }
}
