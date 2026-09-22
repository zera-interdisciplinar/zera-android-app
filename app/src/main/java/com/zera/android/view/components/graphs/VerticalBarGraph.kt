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
 * Uma coluna exibida em [VerticalBarGraph].
 *
 * @param label rótulo exibido abaixo da barra (ex.: "Mai").
 * @param value valor bruto da coluna (ex.: `7` kg), já calculado por quem chama
 *   (ex.: um `ViewModel`). Exibido acima da barra e usado para calcular a altura
 *   relativa entre as colunas — a de maior [value] preenche [VerticalBarGraph.maxBarHeight]
 *   por completo, as demais são proporcionais a ela.
 */
data class VerticalBarGraphItem(
    val label: String,
    val value: Float,
)

/**
 * Gráfico de colunas verticais dentro de um card branco: [title] opcional no topo e
 * uma coluna por [VerticalBarGraphItem] (valor acima, barra, rótulo abaixo).
 *
 * A cor de cada barra é automática (ver [rankedBarGraphColors]): ranking decrescente
 * de [VerticalBarGraphItem.value], ciclando `Azul -> Amarelo -> Verde -> Azul...` —
 * independe da ordem de [items], que só define a ordem de exibição das colunas.
 *
 * @param items colunas a exibir, na ordem em que devem aparecer.
 * @param modifier modificador externo opcional.
 * @param title título opcional exibido no topo do card (ex.: "Materiais descartados (kg)").
 * @param barWidth largura de cada barra.
 * @param maxBarHeight altura da barra do maior [VerticalBarGraphItem.value]; as demais
 *   são desenhadas proporcionalmente a ela.
 */
@Composable
fun VerticalBarGraph(
    items: List<VerticalBarGraphItem>,
    modifier: Modifier = Modifier,
    title: String? = null,
    barWidth: Dp = 40.dp,
    maxBarHeight: Dp = 140.dp,
) {
    val styleByIndex = rankedBarGraphColors(items) { it.value }
    val maxValue = items.maxOfOrNull { it.value } ?: 0f

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
            verticalArrangement = Arrangement.spacedBy(Spacing.large),
        ) {
            if (title != null) {
                LabelText(text = title, bold = true)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                items.forEachIndexed { index, item ->
                    VerticalBarGraphColumn(
                        item = item,
                        style = styleByIndex.getValue(index),
                        heightFraction = if (maxValue > 0f) item.value / maxValue else 0f,
                        barWidth = barWidth,
                        maxBarHeight = maxBarHeight,
                    )
                }
            }
        }
    }
}

@Composable
private fun VerticalBarGraphColumn(
    item: VerticalBarGraphItem,
    style: ZeraColorFamily,
    heightFraction: Float,
    barWidth: Dp,
    maxBarHeight: Dp,
) {
    val palette = style.palette()
    val displayValue = if (item.value == item.value.toInt().toFloat()) {
        item.value.toInt().toString()
    } else {
        item.value.toString()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        CaptionText(text = displayValue, color = palette.base, bold = true)
        // TODO: animar a altura (ex.: animateDpAsState) quando a barra entrar em tela.
        Box(
            modifier = Modifier
                .width(barWidth)
                .height(maxBarHeight * heightFraction.coerceIn(0f, 1f))
                .background(color = palette.base, shape = RoundedCornerShape(Radius.medium)),
        )
        CaptionText(text = item.label)
    }
}

@Preview()
@Composable
private fun VerticalBarGraphPreview() {
    ZeraTheme {
        VerticalBarGraph(
            title = "Materiais descartados (kg)",
            items = listOf(
                VerticalBarGraphItem(label = "Mai", value = 7f),
                VerticalBarGraphItem(label = "Jun", value = 10f),
                VerticalBarGraphItem(label = "Jul", value = 29f),
                VerticalBarGraphItem(label = "Ago", value = 12f),
            ),
        )
    }
}
