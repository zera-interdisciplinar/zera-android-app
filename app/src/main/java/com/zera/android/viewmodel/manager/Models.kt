package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class ModelsState(
    val searchQuery: String = "",
    val totalModelsLabel: String = "",
    val models: List<OptionItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isModelUsageSheetVisible: Boolean = false,
)

class ModelsViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(ModelsState())
    val state = _state

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onSearch() {
        // TODO: buscar modelos pelo termo em state.searchQuery.
    }

    fun onAddModelClick() {
        ZeraNavigator.push(Route.ModelCreation)
    }

    fun onModelClick(model: OptionItem) {
        // TODO: navegar para os detalhes do modelo.
    }

    fun setModelUsageSheetVisible(isVisible: Boolean) {
        _state.value = _state.value.copy(isModelUsageSheetVisible = isVisible)
    }
}
