package com.zera.android.viewmodel.main.helpCentral

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel
import java.util.UUID

data class HelpCentralHubState(
    val searchQuery: String = "",
    val documents: List<OptionItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * ViewModel da Central de Ajuda.
 *
 * Ainda sem integração com o back: a busca só atualiza o texto digitado e a lista de
 * documentos nasce vazia.
 */
class HelpCentralHubViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(HelpCentralHubState())
    val state = _state

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onSearch() {
        // TODO: buscar documentos no back
    }

    fun onDocumentClick(document: OptionItem) {
        // TODO: abrir o documento
    }

    fun onChatClick() {
        ZeraNavigator.push(Route.ChatBot(UUID.randomUUID().toString()), ScreenAnimation.SlideVertical)
    }
}
