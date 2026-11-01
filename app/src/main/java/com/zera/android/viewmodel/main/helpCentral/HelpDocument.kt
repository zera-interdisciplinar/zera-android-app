package com.zera.android.viewmodel.main.helpCentral

import androidx.compose.runtime.mutableStateOf
import com.zera.android.model.usecase.helpCentral.GetHelpDocuments
import com.zera.android.viewmodel.ZeraViewModel

data class HelpDocumentState(
    val title: String = "",
    val body: String = "",
    val notFound: Boolean = false,
)

/**
 * ViewModel do detalhe de um documento da Central de Ajuda.
 *
 * Lê o documento das flags do Scrapy em memória ([GetHelpDocuments.find]); a chave vem da rota.
 */
class HelpDocumentViewModel : ZeraViewModel() {
    private val getHelpDocuments = GetHelpDocuments()

    private val _state = mutableStateOf(HelpDocumentState())
    val state = _state

    fun load(documentKey: String) {
        val document = getHelpDocuments.find(documentKey)
        _state.value = if (document == null) {
            HelpDocumentState(notFound = true)
        } else {
            HelpDocumentState(title = document.title, body = document.body)
        }
    }
}
