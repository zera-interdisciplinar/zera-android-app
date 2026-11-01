package com.zera.android.view.screens.main.helpCentral

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.scaffolds.EmployeeScaffold
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.MarkdownText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.main.helpCentral.HelpDocumentState
import com.zera.android.viewmodel.main.helpCentral.HelpDocumentViewModel

/**
 * Detalhe de um documento da Central de Ajuda: título e corpo em markdown.
 *
 * @param documentKey chave do documento no Scrapy (ex.: `category_vidro`).
 */
@Composable
fun HelpDocumentScreen(
    documentKey: String,
    viewModel: HelpDocumentViewModel = viewModel(),
) {
    val state by viewModel.state

    LaunchedEffect(documentKey) {
        viewModel.load(documentKey)
    }

    EmployeeScaffold(
        title = "Central de ajuda",
        currentRoute = Route.HelpDocument(documentKey),
        goBack = true,
        fabIcon = null,
    ) {
        if (state.notFound) {
            BodyText(text = "Documento não encontrado")
        } else {
            TitleText(text = state.title, bold = true)
            MarkdownText(text = state.body)
        }
    }
}

@Composable
@Preview(heightDp = 900)
fun HelpDocumentScreenPreview() {
    val previewViewModel = HelpDocumentViewModel().apply {
        state.value = HelpDocumentState(
            title = "Vidro",
            body = """
                ## Como descartar
                1. Lave o recipiente
                2. Embale em jornal ou papelão
                3. Escreva **vidro** na embalagem

                ### O que não pode
                - Espelhos
                - Lâmpadas
            """.trimIndent(),
        )
    }
    ZeraTheme {
        HelpDocumentScreen(documentKey = "category_vidro", viewModel = previewViewModel)
    }
}

/** A mesma tela quando a chave não está mais nas flags. */
@Composable
@Preview(heightDp = 900)
fun HelpDocumentScreenNotFoundPreview() {
    val previewViewModel = HelpDocumentViewModel().apply {
        state.value = HelpDocumentState(notFound = true)
    }
    ZeraTheme {
        HelpDocumentScreen(documentKey = "category_vidro", viewModel = previewViewModel)
    }
}
