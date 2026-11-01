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
import com.zera.android.view.components.inputs.ZeraInputType
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.layouts.AdaptiveRow
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Formulário "Editar perfil", pensado para o conteúdo de um `BottomSheet`. Cada campo tem
 * seu próprio `*Enabled` porque nem todo perfil pode alterar todos eles (ex.: só o gestor
 * altera o nome); o campo bloqueado continua visível, só não aceita edição.
 */
@Composable
fun EditProfileForm(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    nameEnabled: Boolean = true,
    emailEnabled: Boolean = true,
    nameError: String? = null,
    emailError: String? = null,
    errorMessage: String? = null,
    isSubmitting: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        TitleText(text = "Editar perfil", bold = true)
        ZeraTextInput(
            value = name,
            onValueChange = onNameChange,
            label = "Nome",
            imeAction = ImeAction.Next,
            enabled = nameEnabled && !isSubmitting,
            isError = nameError != null,
            errorMessage = nameError,
        )
        ZeraTextInput(
            value = email,
            onValueChange = onEmailChange,
            label = "E-mail",
            type = ZeraInputType.Email,
            imeAction = ImeAction.Done,
            enabled = emailEnabled && !isSubmitting,
            isError = emailError != null,
            errorMessage = emailError,
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
private fun EditProfileFormPreview() {
    ZeraTheme {
        EditProfileForm(
            name = "Natália Flores",
            onNameChange = {},
            email = "nome.sobrenome@empresa.com",
            onEmailChange = {},
            onSave = {},
            onCancel = {},
        )
    }
}
