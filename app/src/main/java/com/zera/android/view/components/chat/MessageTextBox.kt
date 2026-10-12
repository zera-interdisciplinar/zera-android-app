package com.zera.android.view.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

private val SendButtonSize = 48.dp
private const val MaxVisibleLines = 4

/**
 * Campo para escrever a mensagem do chat, com o botão de enviar ao lado.
 *
 * O campo cresce até [MaxVisibleLines] linhas. O botão de enviar fica desabilitado enquanto
 * [value] estiver em branco ou [enabled] for `false`; a ação "enviar" do teclado segue a
 * mesma regra.
 *
 * @param value texto atual do campo (estado controlado pelo chamador).
 * @param onValueChange chamado a cada alteração do texto digitado.
 * @param onSend chamado ao tocar no botão de enviar ou na ação "enviar" do teclado.
 * @param modifier modificador externo opcional.
 * @param placeholder texto de dica exibido quando [value] está vazio.
 * @param enabled quando `false`, bloqueia a digitação e o envio (ex.: enquanto o assistente responde).
 */
@Composable
fun MessageTextBox(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Mensagem...",
    enabled: Boolean = true,
) {
    val canSend = enabled && value.isNotBlank()
    val shape = RoundedCornerShape(Radius.xLarge)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        verticalAlignment = Alignment.Bottom,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = SendButtonSize)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = shape)
                .background(color = MaterialTheme.colorScheme.surface, shape = shape)
                .padding(horizontal = Spacing.medium, vertical = Spacing.small),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (value.isEmpty()) {
                BodyText(text = placeholder, alpha = 0.5f)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                maxLines = MaxVisibleLines,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send,
                ),
                keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        IconButton(
            icon = ZeraIcon.Send,
            onClick = onSend,
            contentDescription = "Enviar mensagem",
            style = ZeraColorFamily.Green,
            enabled = canSend,
            size = SendButtonSize,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MessageTextBoxPreview() {
    ZeraTheme {
        Row(modifier = Modifier.padding(Spacing.medium)) {
            MessageTextBox(value = "", onValueChange = {}, onSend = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MessageTextBoxFilledPreview() {
    ZeraTheme {
        Row(modifier = Modifier.padding(Spacing.medium)) {
            MessageTextBox(value = "Onde descarto uma bateria?", onValueChange = {}, onSend = {})
        }
    }
}
