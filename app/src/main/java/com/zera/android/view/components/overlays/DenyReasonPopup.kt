package com.zera.android.view.components.overlays

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.layouts.AdaptiveRow
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme

/**
 * Popup para o gestor informar o motivo da reprovação de um item: título, campo de texto
 * e os botões "Cancelar" e de confirmação (ex.: "Reprovar"). Construído sobre o
 * [PopupDialog], sem o X de fechar — tocar fora, apertar voltar ou tocar em "Cancelar"
 * chamam [onDismissRequest].
 *
 * O chamador controla a visibilidade e só compõe o [DenyReasonPopup] com `if (...) { ... }`,
 * mesma convenção do [PopupDialog]. O texto digitado fica no próprio popup; ao confirmar,
 * ele passa por [validate] e, se válido, é entregue em [onConfirm]. O popup não se fecha
 * sozinho ao confirmar: feche-o em [onConfirm].
 *
 * @param onConfirm ação executada com o motivo validado.
 * @param onDismissRequest chamado ao tocar em "Cancelar", fora do popup ou em voltar.
 * @param title título em destaque acima do campo.
 * @param placeholder texto exibido no campo enquanto vazio.
 * @param confirmButtonText texto do botão da direita (o da esquerda é sempre "Cancelar").
 * @param validate retorna uma mensagem de erro quando o motivo for inválido; `null` indica sucesso.
 * @param modifier modificador externo opcional.
 */
@Composable
fun DenyReasonPopup(
    modifier: Modifier = Modifier,
    onConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit,
    title: String = "Explique o motivo da reprovação do item",
    placeholder: String = "Mensagem...",
    confirmButtonText: String = "Reprovar",
    validate: (String) -> String? = { null },
) {
    var reason by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    PopupDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        hasDismissButton = false,
    ) {
        TitleText(
            text = title,
            bold = true,
            textAlign = TextAlign.Center,
        )
        ZeraTextInput(
            value = reason,
            onValueChange = {
                reason = it
                errorMessage = null
            },
            placeholder = placeholder,
            imeAction = ImeAction.Done,
            isError = errorMessage != null,
            errorMessage = errorMessage,
        )
        AdaptiveRow {
            ZeraButton(
                text = "Cancelar",
                onClick = onDismissRequest,
                type = ZeraButtonType.Secondary,
            )
            ZeraButton(
                text = confirmButtonText,
                onClick = {
                    errorMessage = validate(reason)
                    if (errorMessage == null) {
                        onConfirm(reason)
                    }
                },
                style = ZeraColorFamily.Red,
            )
        }
    }
}

@Preview
@Composable
private fun DenyReasonPopupPreview() {
    ZeraTheme {
        DenyReasonPopup(
            onConfirm = {},
            onDismissRequest = {},
        )
    }
}
