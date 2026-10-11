package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.components.lists.ProductItem
import com.zera.android.viewmodel.ZeraViewModel

data class ModelItemsState(
    // TODO: preencher com o cargo do usuário (validação a ser aplicada por outra pessoa).
    val isEmployee: Boolean = false,
    val searchQuery: String = "",
    val selectedStatuses: List<String> = emptyList(),
    val totalItemsLabel: String = "",
    val items: List<ProductItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
)

// TODO: conectar com o back. Falta aceitar modelId em ItemsQuery/GetItems e no
//  InventoryService (verificar se a API suporta o filtro) e implementar o carregamento
//  paginado, reaproveitando as funções internal do companion do ItensViewModel
//  (productFrom, totalItemsLabel, canLoadMore, appendProducts, mergePages, queriesFor).
class ModelItemsViewModel(
    @Suppress("unused") private val modelId: String,
) : ZeraViewModel() {
    private val _state = mutableStateOf(ModelItemsState())
    val state = _state

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onSearch() {
        // TODO: recarregar a lista com o termo de busca.
    }

    fun applyFilters(statuses: List<String>) {
        _state.value = _state.value.copy(selectedStatuses = statuses)
        // TODO: recarregar a lista com os status selecionados.
    }

    fun onAppliedFilterChipsChange(filters: List<String>) {
        applyFilters(filters)
    }

    fun loadNextPage() {
        // TODO: carregar a próxima página.
    }
}
