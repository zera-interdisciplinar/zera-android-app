package com.zera.android.view.components.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.graphs.PieGraph
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette

/** Diâmetro do gráfico de porcentagem. */
private val OccupationCircleSize = 84.dp

/** Opacidade da parte livre do gráfico, sobre o fundo do card. */
private const val OccupationTrackAlpha = 0.25f

/**
 * Card de destaque com a ocupação do estoque: itens ativos e capacidade total à esquerda
 * e a porcentagem de ocupação num [PieGraph] à direita.
 *
 * @param itemCount quantidade de itens ativos no estoque.
 * @param capacity capacidade total usada no cálculo da porcentagem; `null` quando a unidade
 *   não tem capacidade configurada.
 * @param occupation ocupação do estoque, de 0f a 1f. Exibida como porcentagem inteira.
 * @param modifier modificador externo opcional. O card já ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 * @param onClick ação ao tocar no card. Quando `null` (padrão), o card não é clicável.
 */
@Composable
fun StockOccupationCard(
    itemCount: Int,
    capacity: Int?,
    occupation: Float,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(Radius.xLarge)

    ZeraBox(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it },
        style = ZeraColorFamily.Blue,
        shape = shape,
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
                BodyText(
                    text = if (capacity != null) "Ocupação total: $capacity" else "Ocupação total não configurada",
                    color = LocalContentColor.current,
                )
            }
            PieGraph(
                progress = occupation,
                label = "${(occupation * 100).toInt()}%",
                color = ZeraColorFamily.Yellow.palette().base,
                trackColor = LocalContentColor.current.copy(alpha = OccupationTrackAlpha),
                labelColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(OccupationCircleSize),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StockOccupationCardPreview() {
    ZeraTheme {
        StockOccupationCard(
            itemCount = 300,
            capacity = 500,
            occupation = 0.6f,
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
