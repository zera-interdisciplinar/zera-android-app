package com.zera.android.view.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Formulário "Criar convite", pensado para o conteúdo de um `BottomSheet`. O nome serve só
 * para o gestor identificar o convite; quem resgata informa os próprios dados no cadastro.
 */
@Composable
fun CreateInviteForm(
    name: String,
    onNameChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    nameError: String? = null,
    isSubmitting: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        TitleText(text = "Criar convite", bold = true)
        ZeraTextInput(
            value = name,
            onValueChange = onNameChange,
            label = "Nome do convite",
            imeAction = ImeAction.Done,
            enabled = !isSubmitting,
            isError = nameError != null,
            errorMessage = nameError,
        )
        ZeraButton(
            text = if (isSubmitting) "Enviando..." else "Criar convite",
            onClick = onSubmit,
            fillMaxWidth = true,
            enabled = !isSubmitting,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateInviteFormPreview() {
    ZeraTheme {
        CreateInviteForm(
            name = "Operadora Carol",
            onNameChange = {},
            onSubmit = {},
        )
    }
}
