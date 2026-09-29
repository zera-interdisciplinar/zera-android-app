package com.zera.android.view.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.containers.ZeraBoxType
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme

/**
 * Formulário de geração de convite: o único campo de UI do contrato é [inviteeName].
 * `managerId` sai da sessão do gestor logado.
 */
@Composable
fun CreateInviteForm(
    inviteeName: String,
    onInviteeNameChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false,
    errorMessage: String? = null,
) {
    ZeraBox(
        modifier = modifier.fillMaxWidth(),
        style = ZeraColorFamily.Yellow,
        type = ZeraBoxType.Secondary,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            TitleText(
                text = "Novo convite",
                bold = true,
                color = LocalContentColor.current,
            )
            ZeraTextInput(
                value = inviteeName,
                label = "Nome do convidado",
                placeholder = "Nome que aparece no convite",
                onValueChange = onInviteeNameChange,
                enabled = !isSubmitting,
                isError = !errorMessage.isNullOrBlank(),
                errorMessage = errorMessage,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                ZeraButton(
                    text = "Cancelar",
                    onClick = onCancel,
                    enabled = !isSubmitting,
                    style = ZeraColorFamily.Yellow,
                    type = ZeraButtonType.Tertiary,
                    modifier = Modifier.weight(1f),
                    fillMaxWidth = true,
                )
                ZeraButton(
                    text = if (isSubmitting) "Enviando..." else "Gerar código",
                    onClick = onSubmit,
                    enabled = !isSubmitting,
                    style = ZeraColorFamily.Yellow,
                    modifier = Modifier.weight(1f),
                    fillMaxWidth = true,
                )
            }
            CaptionText(
                text = "O código vale por 7 dias.",
                color = LocalContentColor.current,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateInviteFormPreview() {
    ZeraTheme {
        CreateInviteForm(
            inviteeName = "Carol",
            onInviteeNameChange = {},
            onSubmit = {},
            onCancel = {},
        )
    }
}
