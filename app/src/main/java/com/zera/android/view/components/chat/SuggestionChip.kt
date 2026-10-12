package com.zera.android.view.components.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette

private val ChipBorderWidth = 1.dp
private const val ChipBorderAlpha = 0.4f

/**
 * Pílula clicável com uma sugestão de pergunta para o chat.
 *
 * Usa o par (container, conteúdo) de uma [ZeraColorFamily], com borda na cor base.
 *
 * @param text texto da sugestão.
 * @param onClick ação executada ao tocar na sugestão.
 * @param modifier modificador externo opcional.
 * @param style família de cor da pílula. Ver [ZeraColorFamily].
 */
@Composable
fun SuggestionChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ZeraColorFamily = ZeraColorFamily.Yellow,
) {
    val palette = style.palette()
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        color = palette.container,
        contentColor = palette.onContainer,
        border = BorderStroke(ChipBorderWidth, palette.base.copy(alpha = ChipBorderAlpha)),
    ) {
        LabelText(
            text = text,
            bold = true,
            color = palette.onContainer,
            modifier = Modifier.padding(horizontal = Spacing.medium, vertical = Spacing.small),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SuggestionChipPreview() {
    ZeraTheme {
        Row(
            modifier = Modifier.padding(Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            SuggestionChip(text = "Como cadastrar um produto?", onClick = {})
            SuggestionChip(text = "Falar com o suporte", onClick = {}, style = ZeraColorFamily.Blue)
        }
    }
}
