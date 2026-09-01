package com.zera.android.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.theme.TransparentBlue17
import com.zera.android.view.theme.TransparentBlue40
import com.zera.android.view.theme.TransparentYellow27
import com.zera.android.view.theme.ZeraTheme

/**
 * Arte decorativa de fundo da splash: faixas azuis no canto superior direito
 * e faixas amarelas no canto inferior esquerdo.
 */
@Composable
fun SplashBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    topStripeColor: Color = TransparentBlue40,
    topStripeColorSecondary: Color = TransparentBlue17,
    bottomStripeColor: Color = TransparentYellow27,
) {
    Canvas(
        modifier = modifier
            .background(backgroundColor)
            .clearAndSetSemantics {},
    ) {
        val w = size.width
        val h = size.height

        // faixa superior direita
        val topPath = Path().apply {
            moveTo(w * 0.55f, h * 0.03f)
            lineTo(w, h * 0.03f)
            lineTo(w, h * 0.07f)
            lineTo(w * 0.42f, h * 0.07f)
            close()
        }
        drawPath(topPath, color = topStripeColor)

        val topPath2 = Path().apply {
            moveTo(w * 0.68f, h * 0.10f)
            lineTo(w, h * 0.10f)
            lineTo(w, h * 0.14f)
            lineTo(w * 0.55f, h * 0.14f)
            close()
        }
        drawPath(topPath2, color = topStripeColorSecondary)

        // faixas inferiores esquerdas
        val bottomPath = Path().apply {
            moveTo(w * 0.0f, h * 0.8f)
            lineTo(w * 0.5f, h * 1f)
            lineTo(w * 0f, h * 1f)
            close()
        }
        drawPath(bottomPath, color = bottomStripeColor)

        val bottomPath2 = Path().apply {
            moveTo(w * 0.0f, h * 0.7f)
            lineTo(w * 0.0f, h * 0.75f)
            lineTo(w * 0.6f, h * 1f)
            lineTo(w * 0.7f, h * 1f)
            close()
        }
        drawPath(bottomPath2, color = bottomStripeColor)
    }
}

@Preview
@Composable
private fun SplashBackgroundPreview() {
    ZeraTheme {
        SplashBackground(modifier = Modifier.fillMaxSize())
    }
}
