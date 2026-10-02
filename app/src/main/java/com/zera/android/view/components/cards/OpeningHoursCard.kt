package com.zera.android.view.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Faixa de horário de funcionamento exibida em [OpeningHoursCard].
 *
 * @param days dias a que o horário se aplica (ex.: "Seg–Sex", "Sábado").
 * @param hours intervalo de funcionamento, já formatado por quem chama (ex.: "08h–18h").
 */
data class OpeningHoursItem(
    val days: String,
    val hours: String,
)

/**
 * Grade de horários de funcionamento: um card branco com uma linha por
 * [OpeningHoursItem], dias à esquerda e horário em destaque à direita.
 *
 * Aceita qualquer quantidade de linhas — o card cresce com o conteúdo. Com [items]
 * vazio, não desenha nada.
 *
 * @param items faixas de horário, na ordem em que devem aparecer.
 * @param modifier modificador externo opcional. O card ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 */
@Composable
fun OpeningHoursCard(
    items: List<OpeningHoursItem>,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.large),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier.padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.large),
        ) {
            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabelText(
                        text = item.days,
                        bold = true,
                        modifier = Modifier.weight(1f),
                    )
                    BodyText(
                        text = item.hours,
                        bold = true,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OpeningHoursCardPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            OpeningHoursCard(
                items = listOf(
                    OpeningHoursItem(days = "Seg–Sex", hours = "08h–18h"),
                    OpeningHoursItem(days = "Sábado", hours = "09h–14h"),
                ),
            )
            OpeningHoursCard(
                items = listOf(
                    OpeningHoursItem(days = "Seg–Qui", hours = "08h–18h"),
                    OpeningHoursItem(days = "Sexta", hours = "08h–17h"),
                    OpeningHoursItem(days = "Sábado", hours = "09h–14h"),
                    OpeningHoursItem(days = "Domingo", hours = "Fechado"),
                ),
            )
        }
    }
}
