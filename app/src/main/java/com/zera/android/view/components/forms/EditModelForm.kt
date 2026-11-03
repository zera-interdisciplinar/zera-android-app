package com.zera.android.view.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.inputs.ZeraInputType
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.layouts.AdaptiveRow
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/** Formulário "Editar modelo", pensado para o conteúdo de um `BottomSheet`. */
@Composable
fun EditModelForm(
    name: String,
    onNameChange: (String) -> Unit,
    manufacturer: String,
    onManufacturerChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    nameError: String? = null,
    manufacturerError: String? = null,
    weightError: String? = null,
    isSubmitting: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        TitleText(text = "Editar modelo", bold = true)
        ZeraTextInput(
            value = name,
            onValueChange = onNameChange,
            label = "Nome",
            imeAction = ImeAction.Next,
            enabled = !isSubmitting,
            isError = nameError != null,
            errorMessage = nameError,
        )
        ZeraTextInput(
            value = manufacturer,
            onValueChange = onManufacturerChange,
            label = "Marca",
            imeAction = ImeAction.Next,
            enabled = !isSubmitting,
            isError = manufacturerError != null,
            errorMessage = manufacturerError,
        )
        ZeraTextInput(
            value = weight,
            onValueChange = onWeightChange,
            label = "Peso estimado (kg)",
            placeholder = "Ex: 2,5",
            type = ZeraInputType.Decimal,
            imeAction = ImeAction.Done,
            enabled = !isSubmitting,
            isError = weightError != null,
            errorMessage = weightError,
        )
        AdaptiveRow(modifier = Modifier.fillMaxWidth()) {
            ZeraButton(
                text = "Cancelar",
                onClick = onCancel,
                type = ZeraButtonType.Secondary,
                enabled = !isSubmitting,
            )
            ZeraButton(
                text = if (isSubmitting) "Salvando..." else "Salvar",
                onClick = onSave,
                enabled = !isSubmitting,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditModelFormPreview() {
    ZeraTheme {
        EditModelForm(
            name = "Latitude 5420",
            onNameChange = {},
            manufacturer = "Dell",
            onManufacturerChange = {},
            weight = "2,5",
            onWeightChange = {},
            onSave = {},
            onCancel = {},
        )
    }
}
