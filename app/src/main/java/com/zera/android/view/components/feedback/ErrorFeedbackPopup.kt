package com.zera.android.view.components.feedback

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

/**
 * Popup de erro das telas (falha ao carregar ou enviar): [FeedbackPopup] com o título fixo
 * "Algo deu errado", ícone de exclamação vermelho e a [description] explicando o que falhou.
 *
 * A [description] é texto para o usuário: nunca código HTTP, nome de exceção ou `e.message` cru.
 * Falta de conexão não usa este popup, e sim a tela cheia de conexão.
 *
 * O chamador só compõe o popup com `if (...) { ... }` ou `?.let`, mesma convenção do [FeedbackPopup].
 *
 * @param description o que falhou, em linguagem simples (ex.: "Houve uma instabilidade e não
 *   conseguimos carregar os itens.").
 * @param onDismissRequest chamado ao tocar no X, fora do popup ou em voltar; deve limpar o erro.
 * @param modifier modificador externo opcional.
 */
@Composable
fun ErrorFeedbackPopup(
    description: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FeedbackPopup(
        modifier = modifier,
        title = "Algo deu errado",
        subtitle = description,
        onDismissRequest = onDismissRequest,
        icon = ZeraIcon.Exclamation,
        iconStyle = ZeraColorFamily.Red,
    )
}

@Preview
@Composable
private fun ErrorFeedbackPopupPreview() {
    ZeraTheme {
        ErrorFeedbackPopup(
            description = "Houve uma instabilidade e não conseguimos carregar os itens.",
            onDismissRequest = {},
        )
    }
}
