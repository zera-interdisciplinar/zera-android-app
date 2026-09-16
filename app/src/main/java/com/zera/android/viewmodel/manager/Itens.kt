package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zera.android.view.components.lists.ProductItem
import com.zera.android.view.theme.ZeraColorFamily

data class ItensState(
    val searchQuery: String = "",
    val filterOptions: List<String> = listOf("Todos", "Pendentes", "Categoria"),
    val selectedFilter: String? = "Todos",
    val totalItemsLabel: String = "",
    val items: List<ProductItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class ItensViewModel : ViewModel() {
    // TODO: substituir os dados de exemplo pela chamada ao back (ver loadItems)
    private val _state = mutableStateOf(
        ItensState(
            totalItemsLabel = "1.230 Itens",
            items = listOf(
                ProductItem(
                    id = "265964",
                    name = "Placa de vídeo",
                    statusText = "Pendente",
                    statusStyle = ZeraColorFamily.Yellow,
                ),
                ProductItem(
                    id = "265965",
                    name = "Placa de vídeo",
                    statusText = "Reprovado",
                    statusStyle = ZeraColorFamily.Red,
                ),
                ProductItem(
                    id = "265966",
                    name = "Placa de vídeo",
                    statusText = "Aprovada",
                    statusStyle = ZeraColorFamily.Green,
                ),
                ProductItem(
                    id = "265967",
                    name = "Placa de vídeo",
                    statusText = "Aprovada",
                    statusStyle = ZeraColorFamily.Green,
                ),
            ),
        )
    )
    val state = _state

    init {
        loadItems()
    }

    private fun loadItems() {
        // TODO: buscar a lista de itens no back (com paginação) e atualizar o _state (isLoading / errorMessage inclusos)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onFilterChange(filter: String?) {
        _state.value = _state.value.copy(selectedFilter = filter)
    }
}
