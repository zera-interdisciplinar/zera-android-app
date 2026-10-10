package com.zera.android.view.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette

/**
 * Botão de seleção única: apenas o indicador circular, com uma label opcional acima.
 *
 * O estado é controlado pelo chamador. Quando selecionado, o indicador é
 * preenchido com a cor base de [style].
 *
 * @param selected se o botão está selecionado.
 * @param onClick chamado ao tocar no botão.
 * @param modifier modificador externo opcional.
 * @param label rótulo exibido acima do botão. Omitido quando vazio.
 * @param style família de cor usada para destacar o botão selecionado. Ver [ZeraColorFamily].
 */
@Composable
fun ZeraRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    style: ZeraColorFamily = ZeraColorFamily.Blue,
) {
    val palette = style.palette()
    val color = if (selected) palette.base else MaterialTheme.colorScheme.outline

    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            LabelText(
                text = label,
                modifier = Modifier.padding(bottom = Spacing.small),
                bold = true,
            )
        }
        Box(
            modifier = Modifier
                .size(24.dp)
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    role = Role.RadioButton,
                )
                .border(width = 2.dp, color = color, shape = CircleShape)
                .padding(5.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(palette.base, CircleShape),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ZeraRadioButtonPreview() {
    ZeraTheme {
        var selectedOption by remember { mutableStateOf("Azul") }
        Row(
            modifier = Modifier.padding(Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.large),
        ) {
            ZeraRadioButton(
                label = "Azul",
                selected = selectedOption == "Azul",
                onClick = { selectedOption = "Azul" },
            )
            ZeraRadioButton(
                label = "Branco",
                selected = selectedOption == "Branco",
                onClick = { selectedOption = "Branco" },
            )
        }
    }
}
