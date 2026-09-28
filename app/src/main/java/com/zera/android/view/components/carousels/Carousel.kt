package com.zera.android.view.components.carousels

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Carousel genérico: alterna automaticamente entre [items] a cada [autoAdvanceInterval],
 * com uma transição de crossfade entre o item atual e o próximo.
 *
 * Não define aparência — quem chama decide, via [content], como desenhar cada item.
 * Serve de base para carousels específicos, como [HintCarousel].
 *
 * @param items itens exibidos em loop; precisa conter ao menos 1 elemento.
 * @param modifier modificador externo opcional.
 * @param autoAdvanceInterval tempo em que cada item permanece visível antes de avançar para o próximo.
 * @param content conteúdo desenhado para o item atual, recebendo seu índice em [items] e o próprio item.
 */
@Composable
fun <T> Carousel(
    items: List<T>,
    modifier: Modifier = Modifier,
    autoAdvanceInterval: Duration = 6.seconds,
    content: @Composable (index: Int, item: T) -> Unit,
) {
    require(items.isNotEmpty()) { "Carousel precisa de ao menos 1 item em items." }

    var currentIndex by remember(items) { mutableIntStateOf(0) }

    LaunchedEffect(items, autoAdvanceInterval) {
        while (true) {
            delay(autoAdvanceInterval)
            currentIndex = (currentIndex + 1) % items.size
        }
    }

    Crossfade(targetState = currentIndex, modifier = modifier, label = "Carousel") { index ->
        content(index, items[index])
    }
}

@Preview(showBackground = true)
@Composable
private fun CarouselPreview() {
    ZeraTheme {
        Carousel(
            items = listOf("Primeiro item", "Segundo item", "Terceiro item"),
            autoAdvanceInterval = 2.seconds,
        ) { _, item ->
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                BodyText(
                    text = item,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(Spacing.medium),
                )
            }
        }
    }
}
