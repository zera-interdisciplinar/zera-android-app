package com.zera.android.view.components.carousels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.components.texts.OverlineText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** Ciclo de cores do [HintCarousel]: azul, verde e amarelo, nessa ordem, repetindo. */
private val HintCarouselColorCycle = listOf(ZeraColorFamily.Blue, ZeraColorFamily.Green, ZeraColorFamily.Yellow)

/**
 * Carousel de dicas: alterna automaticamente entre [hints], cada uma dentro de um card
 * de cor sólida que segue o ciclo azul → verde → amarelo. [label] é fixo e se repete em
 * todos os ciclos — só a cor de fundo e o texto da dica mudam.
 *
 * Construído sobre [Carousel].
 *
 * @param label rótulo curto exibido em todos os ciclos, acima da dica.
 * @param hints textos de dica exibidos em loop; precisa conter ao menos 1 elemento.
 * @param modifier modificador externo opcional.
 * @param autoAdvanceInterval tempo em que cada dica permanece visível antes de avançar.
 */
@Composable
fun HintCarousel(
    label: String,
    hints: List<String>,
    modifier: Modifier = Modifier,
    autoAdvanceInterval: Duration = 6.seconds,
) {
    Carousel(
        items = hints,
        modifier = modifier.fillMaxWidth(),
        autoAdvanceInterval = autoAdvanceInterval,
    ) { index, hint ->
        val style = HintCarouselColorCycle[index % HintCarouselColorCycle.size]
        ZeraBox(
            modifier = Modifier.fillMaxWidth(),
            style = style,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                OverlineText(text = label, color = LocalContentColor.current)
                TitleText(
                    text = hint,
                    color = LocalContentColor.current,
                    bold = true,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HintCarouselPreview() {
    ZeraTheme {
        HintCarousel(
            label = "Descarte consciente",
            hints = listOf(
                "Encontre o destino certo para cada material",
                "Separe recicláveis antes de descartar",
                "Pilhas e baterias têm coleta própria",
            ),
            autoAdvanceInterval = 2.seconds,
        )
    }
}
