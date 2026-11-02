package com.zera.android.view.components.graphs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/** Ponto de partida da fatia, em graus: 12 horas no relógio. */
private const val PieStartAngle = -90f
private const val PieFullSweep = 360f

/**
 * Gráfico de pizza de um valor só: a fatia em [color] cobre a fração [progress] do círculo,
 * no sentido horário a partir do topo, sobre o fundo [trackColor]. A fatia se preenche ao
 * entrar na tela (ver [rememberGraphFill]).
 *
 * O tamanho vem do [modifier] (ex.: `Modifier.size(84.dp)`).
 *
 * @param progress fração preenchida, de `0f` a `1f`.
 * @param label texto centralizado sobre o gráfico (ex.: "60%").
 * @param color cor da fatia.
 * @param trackColor cor do restante do círculo.
 * @param labelColor cor de [label]; precisa ser legível sobre [color] e [trackColor].
 * @param modifier modificador externo opcional.
 */
@Composable
fun PieGraph(
    progress: Float,
    label: String,
    color: Color,
    trackColor: Color,
    labelColor: Color,
    modifier: Modifier = Modifier,
) {
    val fill = rememberGraphFill(progress.coerceIn(0f, 1f))

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = trackColor)
            drawArc(
                color = color,
                startAngle = PieStartAngle,
                sweepAngle = PieFullSweep * fill.value,
                useCenter = true,
            )
        }
        TitleText(text = label, bold = true, color = labelColor)
    }
}

@Preview(showBackground = true)
@Composable
private fun PieGraphPreview() {
    ZeraTheme {
        PieGraph(
            progress = 0.6f,
            label = "60%",
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
            labelColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .padding(Spacing.medium)
                .size(84.dp),
        )
    }
}
