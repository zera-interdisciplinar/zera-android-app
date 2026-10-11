package com.zera.android.view.components.chat

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

private val DotSize = 8.dp
private const val DotCount = 3
private const val DotMinAlpha = 0.25f
private const val DotFadeMillis = 400
private const val DotDelayMillis = 150

/**
 * Bolha do assistente com reticências animadas, indicando que a resposta está sendo preparada.
 *
 * Segue o estilo do [MessageContainer] de [MessageRole.Assistant], sem nenhum conteúdo.
 * Deve ser sempre o último item do chat — o [ChatScroll] já garante essa ordem.
 *
 * @param modifier modificador externo opcional.
 */
@Composable
fun LoadingMessageContainer(modifier: Modifier = Modifier) {
    val dotColor = MaterialTheme.colorScheme.onSurfaceVariant
    val transition = rememberInfiniteTransition(label = "loadingDots")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = Spacing.huge),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        AssistantAvatar(modifier = Modifier.padding(end = Spacing.small))
        Surface(
            shape = RoundedCornerShape(Radius.large),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = Spacing.medium, vertical = Spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(Spacing.micro),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(DotCount) { index ->
                    // Cada ponto começa um pouco depois do anterior, formando a "onda".
                    val dotAlpha by transition.animateFloat(
                        initialValue = DotMinAlpha,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = DotFadeMillis),
                            repeatMode = RepeatMode.Reverse,
                            initialStartOffset = StartOffset(index * DotDelayMillis),
                        ),
                        label = "dot$index",
                    )
                    Box(
                        modifier = Modifier
                            .size(DotSize)
                            .graphicsLayer { alpha = dotAlpha }
                            .background(color = dotColor, shape = CircleShape),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingMessageContainerPreview() {
    ZeraTheme {
        LoadingMessageContainer(modifier = Modifier.padding(Spacing.medium))
    }
}
