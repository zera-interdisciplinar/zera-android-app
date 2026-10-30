package com.zera.android.view.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.inputs.ZeraDropdownInput
import com.zera.android.view.components.inputs.ZeraRadioButton
import com.zera.android.view.components.inputs.ZeraSliderInput
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.layouts.AdaptiveRow
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Formulário "Editar o item", pensado para o conteúdo de um `BottomSheet`. Os campos são
 * os editáveis de `PATCH /api/v1/items/{id}` que a tela expõe; o tipo de dano só aparece
 * quando [hasDamage] está marcado.
 *
 * O estado é todo do chamador: [onHasDamageChange] recebe o valor *atual* do radio, como em
 * [ZeraRadioButton], e quem chama decide o novo valor.
 */
@Composable
fun EditItemForm(
    name: String,
    onNameChange: (String) -> Unit,
    condition: String,
    conditionOptions: List<String>,
    onConditionChange: (String) -> Unit,
    usageIntensity: Int,
    onUsageIntensityChange: (Int) -> Unit,
    hasDamage: Boolean,
    onHasDamageChange: (Boolean) -> Unit,
    damageType: String,
    damageOptions: List<String>,
    onDamageTypeChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    nameError: String? = null,
    damageTypeError: String? = null,
    notesError: String? = null,
    errorMessage: String? = null,
    isSubmitting: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        TitleText(text = "Editar o item", bold = true)
        ZeraTextInput(
            value = name,
            onValueChange = onNameChange,
            label = "Nome",
            imeAction = ImeAction.Next,
            enabled = !isSubmitting,
            isError = nameError != null,
            errorMessage = nameError,
        )
        ZeraDropdownInput(
            value = condition,
            values = conditionOptions,
            onValueChange = onConditionChange,
            label = "Condição",
            placeholder = "Novo, usado ou danificado",
            enabled = !isSubmitting,
        )
        ZeraSliderInput(
            value = usageIntensity,
            onValueChange = onUsageIntensityChange,
            label = "Intensidade de uso",
            enabled = !isSubmitting,
        )
        ZeraRadioButton(
            selected = hasDamage,
            onClick = { onHasDamageChange(hasDamage) },
            label = "Possui danos?",
        )
        if (hasDamage) {
            ZeraDropdownInput(
                value = damageType,
                values = damageOptions,
                onValueChange = onDamageTypeChange,
                label = "Tipo de Dano",
                placeholder = "Selecione o tipo de dano",
                enabled = !isSubmitting,
                isError = damageTypeError != null,
                errorMessage = damageTypeError,
            )
        }
        ZeraTextInput(
            value = notes,
            onValueChange = onNotesChange,
            label = "Observação",
            placeholder = "Detalhes opcionais",
            imeAction = ImeAction.Done,
            enabled = !isSubmitting,
            isError = notesError != null,
            errorMessage = notesError,
        )
        errorMessage?.let { CaptionText(text = it, color = MaterialTheme.colorScheme.error) }
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
private fun EditItemFormPreview() {
    ZeraTheme {
        EditItemForm(
            name = "Placa de vídeo",
            onNameChange = {},
            condition = "Semidanificado",
            conditionOptions = listOf("Novo", "Usado", "Semidanificado", "Danificado"),
            onConditionChange = {},
            usageIntensity = 4,
            onUsageIntensityChange = {},
            hasDamage = true,
            onHasDamageChange = {},
            damageType = "Tela quebrada",
            damageOptions = listOf("Tela quebrada", "Peça faltando", "Não liga", "Enferrujado", "Outro"),
            onDamageTypeChange = {},
            notes = "Pequeno arranhão na lateral.",
            onNotesChange = {},
            onSave = {},
            onCancel = {},
        )
    }
}
