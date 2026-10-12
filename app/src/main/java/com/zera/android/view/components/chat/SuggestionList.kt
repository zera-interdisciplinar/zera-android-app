package com.zera.android.view.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme

/** Famílias de cor usadas em ciclo nas sugestões, na ordem em que aparecem. */
private val SuggestionColorCycle = listOf(ZeraColorFamily.Yellow, ZeraColorFamily.Blue)

/**
 * Bloco "Sugestões": um título e as [suggestions] como [SuggestionChip]s, quebrando linha
 * quando não cabem na largura. Não desenha nada quando [suggestions] está vazia.
 *
 * @param suggestions textos das sugestões, na ordem de exibição.
 * @param onSuggestionClick chamado com o texto da sugestão tocada.
 * @param modifier modificador externo opcional.
 * @param title título exibido acima das sugestões.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuggestionList(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Sugestões",
) {
    if (suggestions.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        BodyText(text = title, bold = true, color = MaterialTheme.colorScheme.onSurface)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            suggestions.forEachIndexed { index, suggestion ->
                SuggestionChip(
                    text = suggestion,
                    onClick = { onSuggestionClick(suggestion) },
                    style = SuggestionColorCycle[index % SuggestionColorCycle.size],
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SuggestionListPreview() {
    ZeraTheme {
        SuggestionList(
            suggestions = listOf("Como cadastrar um produto?", "Falar com o suporte"),
            onSuggestionClick = {},
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
