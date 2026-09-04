package com.zera.android.view.components.inputs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Campo de texto padrão do app.
 *
 * @param onValueChange chamado a cada alteração do texto digitado.
 * @param modifier modificador externo opcional.
 * @param value texto atual do campo (estado controlado pelo chamador).
 * @param label rótulo exibido acima do campo. Omitido quando vazio.
 * @param placeholder texto de dica exibido quando [value] está vazio.
 * @param type tipo de entrada: define teclado, capitalização e mascaramento. Ver [ZeraInputType].
 * @param imeAction ação exibida no botão do teclado (Concluído, Próximo, ...).
 * @param enabled habilita ou desabilita a interação.
 * @param isError quando `true`, destaca o campo com a cor de erro.
 * @param errorMessage mensagem exibida abaixo do campo quando [isError] for `true`.
 * @param width largura fixa. Quando `null`, o campo ocupa toda a largura disponível.
 */
@Composable
fun ZeraTextInput(
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    value: String = "",
    label: String = "",
    placeholder: String = "",
    type: ZeraInputType = ZeraInputType.Text,
    imeAction: ImeAction = ImeAction.Default,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    width: Dp? = null,
) {
    var secretVisible by remember { mutableStateOf(false) }
    val showToggle = type.isSecret

    val visualTransformation: VisualTransformation =
        if (type.isSecret && !secretVisible) PasswordVisualTransformation()
        else VisualTransformation.None

    val widthModifier =
        if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()

    Column(modifier = modifier.then(widthModifier)) {
        if (label.isNotEmpty()) {
            LabelText(
                text = label,
                modifier = Modifier.padding(Spacing.small),
                bold = true,
            )
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            isError = isError,
            placeholder = { BodyText(placeholder, alpha = 0.5f) },
            singleLine = true,
            visualTransformation = visualTransformation,
            keyboardOptions = type.keyboardOptions(imeAction),
            trailingIcon = if (showToggle) {
                {
                    LabelText(
                        text = if (secretVisible) "Ocultar" else "Mostrar",
                        modifier = Modifier.padding(end = Spacing.medium),
                        onClick = { secretVisible = !secretVisible },
                    )
                }
            } else null,
            shape = RoundedCornerShape(Radius.large),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                errorContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        if (isError && !errorMessage.isNullOrEmpty()) {
            CaptionText(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(
                    start = Spacing.small,
                    top = Spacing.small,
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ZeraTextInputPreview() {
    ZeraTheme {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            ZeraTextInput(
                label = "Nome",
                placeholder = "Seu nome",
                value = "Maria",
                onValueChange = {},
            )
            ZeraTextInput(
                label = "Email",
                placeholder = "voce@exemplo.com",
                type = ZeraInputType.Email,
                value = "invalido",
                isError = true,
                errorMessage = "E-mail inválido",
                onValueChange = {},
            )
            ZeraTextInput(
                label = "Senha",
                placeholder = "Sua senha",
                type = ZeraInputType.Password,
                value = "segredo123",
                onValueChange = {},
            )
        }
    }
}
