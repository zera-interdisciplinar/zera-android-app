package com.zera.android.view.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.OverlineText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette
import kotlin.math.roundToInt

/**
 * Slider numérico com paradas em cada inteiro de [valueRange].
 *
 * @param value valor atual (estado controlado pelo chamador).
 * @param onValueChange chamado com o novo inteiro ao soltar ou arrastar o slider.
 * @param modifier modificador externo opcional.
 * @param label rótulo exibido acima do slider. Omitido quando vazio.
 * @param valueRange faixa de inteiros permitida, inclusive nas pontas.
 * @param enabled habilita ou desabilita a interação.
 * @param style família de cor usada na trilha e na alça do slider. Ver [ZeraColorFamily].
 * @param width largura fixa. Quando `null`, o campo ocupa toda a largura disponível.
 */
@Composable
fun ZeraSliderInput(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    valueRange: IntRange = 1..10,
    enabled: Boolean = true,
    style: ZeraColorFamily = ZeraColorFamily.Blue,
    width: Dp? = null,
) {
    val palette = style.palette()
    val widthModifier =
        if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()
    val steps = (valueRange.last - valueRange.first - 1).coerceAtLeast(0)

    Column(modifier = modifier.then(widthModifier)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        ) {
            if (label.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    OverlineText(text = label, bold = true)
                    LabelText(text = "$value", bold = true)
                }
                Spacer(modifier = Modifier.height(Spacing.micro))
            }
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.roundToInt()) },
                valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
                steps = steps,
                enabled = enabled,
                colors = SliderDefaults.colors(
                    thumbColor = palette.base,
                    activeTrackColor = palette.base,
                    inactiveTrackColor = palette.onBase,
                    activeTickColor = palette.onBase,
                    inactiveTickColor = palette.base,
                ),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                CaptionText(text = "${valueRange.first}")
                CaptionText(text = "${valueRange.last}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ZeraSliderInputPreview() {
    ZeraTheme {
        var intensity by remember { mutableIntStateOf(5) }
        Column(modifier = Modifier.padding(Spacing.medium)) {
            ZeraSliderInput(
                label = "Intensidade de uso",
                value = intensity,
                onValueChange = { intensity = it },
            )
        }
    }
}
