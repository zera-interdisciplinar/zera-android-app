package com.zera.android.view.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme

/** Diâmetro do círculo de porcentagem. */
private val OccupationCircleSize = 84.dp

/**
 * Card de destaque com a ocupação do estoque: total de itens cadastrados à esquerda
 * e a porcentagem de ocupação num círculo à direita.
 *
 * @param itemCount quantidade de itens cadastrados.
 * @param occupation ocupação do estoque, de 0f a 1f. Exibida como porcentagem inteira.
 * @param modifier modificador externo opcional. O card já ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 */
@Composable
fun StockOccupationCard(
    itemCount: Int,
    occupation: Float,
    modifier: Modifier = Modifier,
) {
    ZeraBox(
        modifier = modifier.fillMaxWidth(),
        style = ZeraColorFamily.Blue,
        shape = RoundedCornerShape(Radius.xLarge),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                LabelText(
                    text = "Ocupação do Estoque",
                    bold = true,
                    color = LocalContentColor.current,
                )
                HeadlineText(
                    text = itemCount.toString(),
                    bold = true,
                    color = LocalContentColor.current,
                )
                BodyText(text = "Itens cadastrados", color = LocalContentColor.current)
            }
            ZeraBox(
                modifier = Modifier.size(OccupationCircleSize),
                style = ZeraColorFamily.Yellow,
                shape = CircleShape,
                contentPadding = PaddingValues(Spacing.none),
            ) {
                TitleText(
                    text = "${(occupation * 100).toInt()}%",
                    bold = true,
                    color = LocalContentColor.current,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StockOccupationCardPreview() {
    ZeraTheme {
        StockOccupationCard(
            itemCount = 300,
            occupation = 0.6f,
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
