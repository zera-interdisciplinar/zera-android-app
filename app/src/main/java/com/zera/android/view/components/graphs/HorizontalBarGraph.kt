package com.zera.android.view.components.graphs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette

/**
 * Uma categoria exibida em [HorizontalBarGraph].
 *
 * @param label nome da categoria (ex.: "Eletrônicos").
 * @param percentage participação da categoria, de `0f` a `1f` (ex.: `0.5f` = 50%).
 *   Já calculada por quem chama (ex.: um `ViewModel`) — o componente só formata e desenha.
 *   A cor da barra **não** é escolhida aqui: [HorizontalBarGraph] atribui automaticamente,
 *   por ordem de porcentagem (maior primeiro).
 */
data class BarGraphItem(
    val label: String,
    val percentage: Float,
)

/**
 * Gráfico de barras horizontais dentro de um card branco: uma linha por
 * [BarGraphItem], com o rótulo à esquerda, a barra preenchida proporcionalmente
 * a [BarGraphItem.percentage] e a porcentagem por extenso à direita.
 *
 * A cor de cada barra é automática (ver [rankedBarGraphColors]): ranking decrescente
 * de [BarGraphItem.percentage], ciclando `Azul -> Amarelo -> Verde -> Azul...` —
 * independe da ordem de [items], que só define a ordem de exibição das linhas.
 *
 * @param items categorias a exibir, na ordem em que devem aparecer.
 * @param modifier modificador externo opcional.
 * @param labelWeight fração da largura reservada para a coluna de rótulos (o
 *   restante vai para a barra + porcentagem). Ajuste se os rótulos forem
 *   tipicamente mais longos/curtos que o padrão.
 * @param barHeight espessura da barra.
 */
@Composable
fun HorizontalBarGraph(
    items: List<BarGraphItem>,
    modifier: Modifier = Modifier,
    labelWeight: Float = 0.32f,
    barHeight: Dp = 14.dp,
) {
    val styleByIndex = rankedBarGraphColors(items) { it.percentage }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.medium),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            items.forEachIndexed { index, item ->
                HorizontalBarGraphRow(
                    item = item,
                    style = styleByIndex.getValue(index),
                    labelWeight = labelWeight,
                    barHeight = barHeight,
                )
            }
        }
    }
}

@Composable
private fun HorizontalBarGraphRow(
    item: BarGraphItem,
    style: ZeraColorFamily,
    labelWeight: Float,
    barHeight: Dp,
) {
    val palette = style.palette()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        LabelText(
            text = item.label,
            modifier = Modifier.weight(labelWeight),
            color = palette.base,
            bold = true,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier.weight(1f - labelWeight),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(barHeight)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(percent = 50),
                    ),
            ) {
                // TODO: animar o preenchimento (ex.: animateFloatAsState) quando a barra entrar em tela.
                Box(
                    modifier = Modifier
                        .fillMaxWidth(item.percentage.coerceIn(0f, 1f))
                        .height(barHeight)
                        .background(
                            color = palette.base,
                            shape = RoundedCornerShape(percent = 50),
                        ),
                )
            }
            CaptionText(
                text = "${(item.percentage * 100).toInt()}%",
                modifier = Modifier.width(36.dp),
                color = palette.base,
            )
        }
    }
}

@Preview()
@Composable
private fun HorizontalBarGraphPreview() {
    ZeraTheme {
        HorizontalBarGraph(
            items = listOf(
                BarGraphItem(label = "Eletrônicos", percentage = 0.5f),
                BarGraphItem(label = "Plásticos", percentage = 0.42f),
                BarGraphItem(label = "Outros", percentage = 0.2f),
            ),
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
