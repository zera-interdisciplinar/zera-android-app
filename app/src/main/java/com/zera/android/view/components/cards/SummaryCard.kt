package com.zera.android.view.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme

/**
 * Card de resumo em destaque: um rótulo pequeno e, abaixo, o texto principal em negrito.
 * Ex.: "Resumo do turno" / "3 pendências · 25 em manutenção".
 *
 * @param label rótulo pequeno exibido no topo do card.
 * @param text texto principal do card.
 * @param modifier modificador externo opcional. O card já ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 * @param style família de cor do card. Ver [ZeraColorFamily].
 */
@Composable
fun SummaryCard(
    label: String,
    text: String,
    modifier: Modifier = Modifier,
    style: ZeraColorFamily = ZeraColorFamily.Blue,
) {
    ZeraBox(
        modifier = modifier.fillMaxWidth(),
        style = style,
        shape = RoundedCornerShape(Radius.xLarge),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            LabelText(text = label, bold = true, color = LocalContentColor.current)
            TitleText(text = text, bold = true, color = LocalContentColor.current)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SummaryCardPreview() {
    ZeraTheme {
        SummaryCard(
            label = "Resumo do turno",
            text = "3 pendências · 25 em manutenção",
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
