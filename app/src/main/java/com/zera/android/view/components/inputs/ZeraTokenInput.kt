package com.zera.android.view.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.OverlineText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Campo de token de confirmação (OTP): mostra [size] células, uma por dígito,
 * mas por baixo é um único [BasicTextField] que guarda o código inteiro.
 *
 * Esse desenho (um campo de texto invisível + `decorationBox` desenhando as
 * células) faz o avanço de foco, o backspace, o colar e o preenchimento
 * automático funcionarem de graça — sem orquestrar N campos e N `FocusRequester`.
 *
 * Assim como [ZeraTextInput], é um componente controlado: não guarda o valor
 * internamente, apenas reporta as alterações em [onValueChange]. Cabe ao
 * chamador manter [value] e repassá-lo de volta.
 *
 * @param value código digitado até agora (só dígitos, no máximo [size]).
 * @param onValueChange chamado a cada alteração, já filtrado para conter apenas
 *   dígitos e no máximo [size] caracteres.
 * @param modifier modificador externo opcional.
 * @param size quantidade de dígitos do token.
 * @param label rótulo exibido acima do campo. Omitido quando vazio.
 * @param enabled habilita ou desabilita a interação.
 * @param isError quando `true`, destaca todas as células com a cor de erro.
 * @param errorMessage mensagem exibida abaixo do campo quando [isError] for `true`.
 * @param onFilled chamado quando o código atinge [size] dígitos (útil para
 *   disparar a verificação automaticamente).
 */
@Composable
fun ZeraTokenInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 6,
    label: String = "",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    onFilled: (String) -> Unit = {},
) {
    val code = value.filter(Char::isDigit).take(size)
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            OverlineText(text = label, bold = true)
            Spacer(modifier = Modifier.height(Spacing.small))
        }

        BasicTextField(
            value = code,
            onValueChange = { raw ->
                val digits = raw.filter(Char::isDigit).take(size)
                if (digits != code) {
                    onValueChange(digits)
                    if (digits.length == size) onFilled(digits)
                }
            },
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (code.length == size) {
                        onFilled(code)
                        keyboardController?.hide()
                    }
                },
            ),
            decorationBox = {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    repeat(size) { index ->
                        ZeraTokenCell(
                            digit = code.getOrNull(index)?.toString().orEmpty(),
                            isFocused = enabled && index == code.length,
                            isEnabled = enabled,
                            isError = isError,
                        )
                    }
                }
            },
        )

        if (isError && !errorMessage.isNullOrEmpty()) {
            CaptionText(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = Spacing.small, top = Spacing.small),
            )
        }
    }
}

/**
 * Célula visual de um dígito. Não é um campo — só desenha a caixa e o caractere
 * recebido de [ZeraTokenInput].
 *
 * @param digit dígito a exibir, ou vazio quando a célula ainda não foi preenchida.
 * @param isFocused `true` na próxima célula a ser preenchida (posição do cursor).
 * @param isEnabled repassa o estado de habilitação para ajustar a opacidade.
 * @param isError quando `true`, usa a cor de erro na borda.
 */
@Composable
private fun ZeraTokenCell(
    digit: String,
    isFocused: Boolean,
    isEnabled: Boolean,
    isError: Boolean,
) {
    val scheme = MaterialTheme.colorScheme
    val borderColor = when {
        isError -> scheme.error
        isFocused -> scheme.primary
        else -> scheme.outline
    }

    Box(
        modifier = Modifier
            .width(44.dp)
            .height(56.dp)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(Radius.medium),
            )
            .background(
                color = scheme.surfaceContainerLowest,
                shape = RoundedCornerShape(Radius.medium),
            ),
        contentAlignment = Alignment.Center,
    ) {
        TitleText(
            text = digit,
            color = scheme.primary,
            alpha = if (isEnabled) 1f else 0.5f,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview()
@Composable
private fun ZeraTokenInputPreview() {
    ZeraTheme {
        var vazio by remember { mutableStateOf("") }
        var parcial by remember { mutableStateOf("123") }
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.large),
        ) {
            ZeraTokenInput(
                label = "Código de confirmação",
                value = vazio,
                onValueChange = { vazio = it },
            )
            ZeraTokenInput(
                value = parcial,
                onValueChange = { parcial = it },
            )
            ZeraTokenInput(
                value = "1234",
                onValueChange = {},
                isError = true,
                errorMessage = "Código inválido",
            )
        }
    }
}
