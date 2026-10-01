package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.components.inputs.SelectOption
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel

data class ItensSelectionState(
    val searchQuery: String = "",
    val options: List<SelectOption> = emptyList(),
    val selectedValues: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
) {
    val canChooseDate: Boolean
        get() = selectedValues.isNotEmpty()
}

class ItensSelectionViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(ItensSelectionState())
    val state = _state

    init {
        loadOptions()
    }

    fun onSearchQueryChange(value: String) {
        _state.value = _state.value.copy(searchQuery = value)
    }

    fun onSearch() {
        // TODO: buscar itens cadastrados pelo texto de searchQuery (contrato ainda não definido)
    }

    fun onSelectedValuesChange(values: Set<String>) {
        _state.value = _state.value.copy(selectedValues = values)
    }

    /** Chamado pela tela quando a lista chega perto do fim; deve anexar a próxima página a `options`. */
    fun loadNextPage() {
        // TODO: paginar como ItensViewModel (isLoadingMore + anexar sem duplicar value)
    }

    fun onChooseDateClick() {
        // TODO: levar os itens selecionados (selectedValues) e a escolha de data para o resumo
        ZeraNavigator.push(Route.RecyclingResume, ScreenAnimation.SlideHorizontal)
    }

    private fun loadOptions() {
        // TODO: carregar os itens descartáveis e mapear para SelectOption
        //  (name = nome, description = "ID {id} · {categoria}", value = id) — contrato ainda não definido
    }
}
