package com.zera.android.view.components.layouts

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

/**
 * Linha que vira coluna quando os filhos não cabem lado a lado.
 *
 * Em linha, a largura é dividida em partes iguais entre os filhos. Se a largura natural
 * (em uma única linha de texto) de algum filho passar da sua parte, todos são empilhados
 * em coluna, cada um ocupando a largura toda. Assim o texto não quebra de linha e a altura
 * dos filhos não cresce.
 *
 * Os filhos não precisam de `weight` nem de `fillMaxWidth`: a largura de cada um é definida
 * aqui. A largura do container precisa ser limitada (não use dentro de um scroll horizontal).
 *
 * @param modifier modificador externo opcional.
 * @param spacing espaço horizontal entre os filhos quando estão em linha.
 * @param verticalSpacing espaço vertical entre os filhos quando estão empilhados.
 * @param content filhos, na ordem em que aparecem (da esquerda para a direita, ou de cima para baixo).
 */
@Composable
fun AdaptiveRow(
    modifier: Modifier = Modifier,
    spacing: Dp = Spacing.medium,
    verticalSpacing: Dp = spacing,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val count = measurables.size
        if (count == 0) return@Layout layout(constraints.minWidth, constraints.minHeight) {}

        val maxWidth = constraints.maxWidth
        val spacingPx = spacing.roundToPx()
        val verticalSpacingPx = verticalSpacing.roundToPx()
        val itemWidth = (maxWidth - spacingPx * (count - 1)) / count
        val fitsInRow = measurables.all { it.maxIntrinsicWidth(Constraints.Infinity) <= itemWidth }

        if (fitsInRow) {
            val placeables = measurables.map { it.measure(Constraints.fixedWidth(itemWidth)) }
            val height = constraints.constrainHeight(placeables.maxOf { it.height })
            layout(maxWidth, height) {
                placeables.forEachIndexed { index, placeable ->
                    placeable.placeRelative(x = index * (itemWidth + spacingPx), y = 0)
                }
            }
        } else {
            val placeables = measurables.map { it.measure(Constraints.fixedWidth(maxWidth)) }
            val height = constraints.constrainHeight(
                placeables.sumOf { it.height } + verticalSpacingPx * (count - 1)
            )
            layout(maxWidth, height) {
                var y = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(x = 0, y = y)
                    y += placeable.height + verticalSpacingPx
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun AdaptiveRowInRowPreview() {
    ZeraTheme {
        AdaptiveRow(modifier = Modifier.padding(Spacing.medium)) {
            ZeraButton(
                text = "Escanear",
                onClick = {},
                style = ZeraColorFamily.Yellow,
                icon = ZeraIcon.QrCode,
            )
            ZeraButton(
                text = "Adicionar Modelo",
                onClick = {},
                style = ZeraColorFamily.Green,
                icon = ZeraIcon.Plus,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 260)
@Composable
private fun AdaptiveRowStackedPreview() {
    ZeraTheme {
        AdaptiveRow(modifier = Modifier.padding(Spacing.medium)) {
            ZeraButton(
                text = "Escanear",
                onClick = {},
                style = ZeraColorFamily.Yellow,
                icon = ZeraIcon.QrCode,
            )
            ZeraButton(
                text = "Adicionar Modelo",
                onClick = {},
                style = ZeraColorFamily.Green,
                icon = ZeraIcon.Plus,
            )
        }
    }
}
