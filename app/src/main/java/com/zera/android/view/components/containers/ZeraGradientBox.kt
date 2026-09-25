package com.zera.android.view.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.theme.DarkBlue15
import com.zera.android.view.theme.DarkBlue25
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.White100
import com.zera.android.view.theme.NoColor
import com.zera.android.view.theme.ZeraTheme

/**
 * Container com fundo em gradiente.
 *
 * O [brush] controla as cores e a direção do gradiente. Use [shape] para definir
 * o recorte do fundo e [contentPadding] para o espaçamento interno.
 *
 * @param brush gradiente usado como fundo do container.
 * @param modifier modificador externo opcional.
 * @param shape forma do container.
 * @param contentPadding espaçamento interno entre a borda e o conteúdo.
 * @param contentColor cor de conteúdo disponibilizada via [LocalContentColor].
 * @param content conteúdo desenhado dentro do container.
 */

@Composable
fun ZeraGradientBox(
    brush: Brush,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(Radius.none),
    contentPadding: PaddingValues = PaddingValues(Spacing.large),
    contentColor: Color = White100,
    content: @Composable BoxScope.() -> Unit,
) {
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier = modifier
                .clip(shape)
                .background(brush)
                .padding(contentPadding),
            content = content,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ZeraGradientBoxPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            ZeraGradientBox(
                brush = Brush.horizontalGradient(
                    colors = listOf(DarkBlue15, DarkBlue25),
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                HeadlineText(
                    text = "Indicadores",
                    bold = true,
                    color = LocalContentColor.current,
                )
            }
        }
    }
}

@Composable
@Preview
private fun ZeraGradientBoxPreview2(){
    ZeraTheme {
        ZeraGradientBox(
            brush = Brush.verticalGradient(
                colors = listOf(MaterialTheme.colorScheme.background, NoColor)
            ),
            modifier = Modifier.size(200.dp)
        ){}
    }
}
