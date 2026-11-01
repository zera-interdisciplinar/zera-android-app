package com.zera.android.viewmodel.main.helpCentral

import androidx.compose.runtime.mutableStateOf
import com.zera.android.model.entity.helpCentral.HelpDocument
import com.zera.android.model.usecase.helpCentral.GetHelpDocuments
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel
import java.util.UUID

data class HelpCentralHubState(
    val searchQuery: String = "",
    val documents: List<OptionItem> = emptyList(),
)

/**
 * ViewModel da Central de Ajuda.
 *
 * Os documentos vêm das flags do Scrapy já carregadas ao entrar no app ([GetHelpDocuments]),
 * então a leitura é síncrona e não há estado de carregamento. A busca filtra localmente.
 */
class HelpCentralHubViewModel : ZeraViewModel() {
    private val getHelpDocuments = GetHelpDocuments()

    private val _state = mutableStateOf(HelpCentralHubState())
    val state = _state

    private val loadedDocuments: List<HelpDocument> = getHelpDocuments.execute()

    init {
        publishList()
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        publishList()
    }

    fun onDocumentClick(document: OptionItem) {
        // TODO: abrir o detalhe do documento (item 5 da spec)
    }

    fun onChatClick() {
        ZeraNavigator.push(Route.ChatBot(UUID.randomUUID().toString()), ScreenAnimation.SlideVertical)
    }

    private fun publishList() {
        val visible = visibleDocuments(loadedDocuments, _state.value.searchQuery)
        _state.value = _state.value.copy(documents = visible.map(::optionFrom))
    }

    companion object {
        internal fun optionFrom(document: HelpDocument): OptionItem {
            return OptionItem(
                id = document.key,
                name = document.title,
                description = document.subtitle,
                icon = ZeraIcon.InfoCircle,
            )
        }

        internal fun visibleDocuments(
            documents: List<HelpDocument>,
            query: String,
        ): List<HelpDocument> {
            val term = query.trim()
            if (term.isEmpty()) return documents
            return documents.filter { document ->
                document.title.contains(term, ignoreCase = true) ||
                    document.subtitle.contains(term, ignoreCase = true)
            }
        }
    }
}
