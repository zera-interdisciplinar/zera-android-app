package com.zera.android.view.components.inputs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.overlays.PopupDialog
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme

/**
 * Popup para editar um valor textual com tipo de entrada e validação configuráveis.
 *
 * @param label rótulo exibido no campo de entrada.
 * @param value valor inicial do campo.
 * @param inputType tipo de teclado e transformação do campo.
 * @param onDismissRequest ação executada ao fechar o popup sem confirmar.
 * @param onConfirm ação executada com o valor validado.
 * @param validate retorna uma mensagem de erro quando o valor for inválido; `null` indica sucesso.
 */
@Composable
fun ZeraInputPopup(
    label: String,
    value: String,
    inputType: ZeraInputType = ZeraInputType.Text,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
    validate: (String) -> String? = { null },
) {
    var inputValue by remember(value) { mutableStateOf(value) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    PopupDialog(onDismissRequest = onDismissRequest) {
        Spacer(Modifier.height(Spacing.medium))
        ZeraTextInput(
            value = inputValue,
            onValueChange = {
                inputValue = it
                errorMessage = null
            },
            label = label,
            type = inputType,
            imeAction = ImeAction.Done,
            isError = errorMessage != null,
            errorMessage = errorMessage,
        )
        ZeraButton(
            text = "Confirmar",
            onClick = {
                errorMessage = validate(inputValue)
                if (errorMessage == null) {
                    onConfirm(inputValue)
                }
            },
            style = ZeraColorFamily.Green,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun ZeraInputPopupPreview() {
    ZeraTheme {
        ZeraInputPopup(
            label = "E-mail",
            value = "usuario@exemplo.com",
            inputType = ZeraInputType.Email,
            onDismissRequest = {},
            onConfirm = {},
        )
    }
}
