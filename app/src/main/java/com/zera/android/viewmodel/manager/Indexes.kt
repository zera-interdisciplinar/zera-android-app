package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.components.graphs.BarGraphItem
import com.zera.android.view.components.graphs.VerticalBarGraphItem
import com.zera.android.viewmodel.ZeraViewModel

data class IndexesState(
    val periodFilters: List<String> = listOf("Todos", "Este mês", "Categoria"),
    val selectedFilter: String = "Todos",
    val recyclingRateLabel: String = "",
    val recyclingRateDeltaLabel: String = "",
    val recyclingGoal: Float = 0f,
    val monthlyEvolution: List<VerticalBarGraphItem> = emptyList(),
    val residuesByCategory: List<BarGraphItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class IndexesViewModel : ZeraViewModel() {
    // TODO: substituir os dados de exemplo pela chamada ao back assim que o
    // endpoint de indicadores do gestor existir (ver loadIndexes)
    private val _state = mutableStateOf(
        IndexesState(
            recyclingRateLabel = "68%",
            recyclingRateDeltaLabel = "↑ 4,5% no período",
            recyclingGoal = 0.75f,
            monthlyEvolution = listOf(
                VerticalBarGraphItem(label = "Mai", value = 7f),
                VerticalBarGraphItem(label = "Jun", value = 10f),
                VerticalBarGraphItem(label = "Jul", value = 29f),
                VerticalBarGraphItem(label = "Ago", value = 12f),
            ),
            residuesByCategory = listOf(
                BarGraphItem(label = "Eletrônicos", percentage = 0.5f),
                BarGraphItem(label = "Plásticos", percentage = 0.42f),
                BarGraphItem(label = "Outros", percentage = 0.2f),
            ),
        )
    )
    val state = _state

    init {
        loadIndexes()
    }

    fun onFilterChange(filter: String) {
        _state.value = _state.value.copy(selectedFilter = filter)
        // TODO: recarregar os indicadores considerando o filtro selecionado
    }

    private fun loadIndexes() {
        // TODO: buscar os indicadores do gestor no back e atualizar o _state (isLoading / errorMessage inclusos)
    }
}
