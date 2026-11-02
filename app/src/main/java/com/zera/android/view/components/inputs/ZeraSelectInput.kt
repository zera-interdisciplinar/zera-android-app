package com.zera.android.view.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.OverlineText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

private val SelectInputBorderWidth = 1.dp

/**
 * Campo com a aparência de [ZeraDropdownInput] que, em vez de abrir um menu, só avisa o toque em
 * [onClick]. Serve para escolhas que não cabem em um menu, como seleção múltipla em um `BottomSheet`:
 * quem chama abre a seleção e devolve o resumo em [value].
 *
 * @param onClick chamado ao tocar no campo.
 * @param modifier modificador externo opcional.
 * @param value resumo do que está selecionado. Vazio mostra [placeholder].
 * @param label rótulo exibido no topo do campo. Omitido quando vazio.
 * @param placeholder texto exibido enquanto não há seleção.
 * @param enabled quando `false`, o campo não responde ao toque.
 * @param isError destaca a borda com a cor de erro.
 * @param errorMessage texto exibido abaixo do campo quando [isError] é `true`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeraSelectInput(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    value: String = "",
    label: String = "",
    placeholder: String = "",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    val shape = RoundedCornerShape(Radius.large)
    val borderColor =
        if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline

    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .border(width = SelectInputBorderWidth, color = borderColor, shape = shape)
                .background(color = MaterialTheme.colorScheme.surface, shape = shape)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        ) {
            if (label.isNotEmpty()) {
                OverlineText(text = label, bold = true)
                Spacer(modifier = Modifier.height(Spacing.micro))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                BodyText(
                    text = value.ifEmpty { placeholder },
                    modifier = Modifier.weight(1f),
                    alpha = if (value.isEmpty()) 0.5f else 1f,
                )
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
            }
        }

        if (isError && !errorMessage.isNullOrEmpty()) {
            CaptionText(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = Spacing.small, top = Spacing.small),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ZeraSelectInputPreview() {
    ZeraTheme {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            ZeraSelectInput(
                onClick = {},
                value = "Plástico, Bateria",
                label = "Materiais",
                placeholder = "Selecione os materiais",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ZeraSelectInputErrorPreview() {
    ZeraTheme {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            ZeraSelectInput(
                onClick = {},
                label = "Materiais",
                placeholder = "Selecione os materiais",
                isError = true,
                errorMessage = "Selecione ao menos um material.",
            )
        }
    }
}
