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
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.layouts.AdaptiveRow
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/** Formulário "Editar categoria", pensado para o conteúdo de um `BottomSheet`. */
@Composable
fun EditCategoryForm(
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    nameError: String? = null,
    descriptionError: String? = null,
    isSubmitting: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        TitleText(text = "Editar categoria", bold = true)
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
            value = description,
            onValueChange = onDescriptionChange,
            label = "Descrição",
            imeAction = ImeAction.Done,
            enabled = !isSubmitting,
            isError = descriptionError != null,
            errorMessage = descriptionError,
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
private fun EditCategoryFormPreview() {
    ZeraTheme {
        EditCategoryForm(
            name = "Informática",
            onNameChange = {},
            description = "Notebooks e monitores",
            onDescriptionChange = {},
            onSave = {},
            onCancel = {},
        )
    }
}
