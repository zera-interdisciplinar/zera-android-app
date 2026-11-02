package com.zera.android.viewmodel.main.categories

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.usecase.inventory.GetCategories
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.errorDescription
import com.zera.android.viewmodel.loadErrorDescription
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class CategoriesState(
    val searchQuery: String = "",
    val totalCategoriesLabel: String = "",
    val categories: List<OptionItem> = emptyList(),
    val isLoading: Boolean = true,
    /** A última carga falhou: a lista vazia não mostra "Nenhuma categoria encontrada". */
    val loadFailed: Boolean = false,
    val errorMessage: String? = null,
)

class CategoriesViewModel : ZeraViewModel() {
    private val getCategories = GetCategories()

    private val _state = mutableStateOf(CategoriesState())
    val state = _state

    private var loadJob: Job? = null
    private var hasLoaded = false
    private var loadedCategories: List<CategoryResponseDTO> = emptyList()

    fun refresh() {
        loadCategories(silent = hasLoaded)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        publishList()
    }

    fun onAddCategoryClick() {
        ZeraNavigator.push(Route.CategoryCreation)
    }

    fun onErrorDismiss() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    /** [silent] é a recarga automática (retorno à tela): não reativa `isLoading`, que só sobe na primeira carga. */
    private fun loadCategories(silent: Boolean = false) {
        loadJob?.cancel()
        _state.value = _state.value.copy(isLoading = !silent, errorMessage = null)
        loadJob = viewModelScope.launch {
            try {
                loadedCategories = getCategories.execute()
                hasLoaded = true
                _state.value = _state.value.copy(isLoading = false, loadFailed = false, errorMessage = null)
                publishList()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    loadFailed = true,
                    errorMessage = errorDescription(e, loadErrorDescription("as categorias")),
                )
            }
        }
    }

    private fun publishList() {
        val visible = visibleCategories(loadedCategories, _state.value.searchQuery)
        _state.value = _state.value.copy(
            totalCategoriesLabel = totalCategoriesLabel(visible.size),
            categories = visible.map(::optionFrom),
        )
    }

    companion object {
        private val ptBr = Locale.forLanguageTag("pt-BR")

        internal fun totalCategoriesLabel(total: Int): String {
            val formatted = NumberFormat.getIntegerInstance(ptBr).format(total)
            return if (total == 1) "$formatted Categoria" else "$formatted Categorias"
        }

        internal fun optionFrom(category: CategoryResponseDTO): OptionItem {
            return OptionItem(
                name = category.name,
                description = category.description.orEmpty(),
            )
        }

        internal fun visibleCategories(
            categories: List<CategoryResponseDTO>,
            query: String,
        ): List<CategoryResponseDTO> {
            val term = query.trim()
            if (term.isEmpty()) return categories
            return categories.filter { category ->
                category.name.contains(term, ignoreCase = true) ||
                    category.description.orEmpty().contains(term, ignoreCase = true)
            }
        }
    }
}
