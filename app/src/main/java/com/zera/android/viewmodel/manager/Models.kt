package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.model.usecase.inventory.GetModels
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class ModelsState(
    val searchQuery: String = "",
    val totalModelsLabel: String = "",
    val models: List<OptionItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val isModelUsageSheetVisible: Boolean = false,
)

class ModelsViewModel : ZeraViewModel() {
    private val getModels = GetModels()

    private val _state = mutableStateOf(ModelsState())
    val state = _state

    private var loadJob: Job? = null
    private var loadedPage: Int = -1
    private var totalPages: Int = 0
    private var loadedModels: List<ModelResponseDTO> = emptyList()

    fun refresh() {
        loadModels()
    }

    fun onSearchQueryChange(query: String) {
        val previous = _state.value.searchQuery
        _state.value = _state.value.copy(searchQuery = query)
        if (query.isEmpty() && previous.isNotEmpty()) {
            loadModels()
        }
    }

    fun onSearch() {
        loadModels()
    }

    fun loadNextPage() {
        if (!canLoadMore(
                loadedPage = loadedPage,
                totalPages = totalPages,
                isBusy = _state.value.isLoading || _state.value.isLoadingMore,
            )
        ) {
            return
        }
        loadModels(reset = false)
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

    private fun loadModels(reset: Boolean = true) {
        if (reset) {
            loadJob?.cancel()
            loadedPage = -1
            totalPages = 0
            _state.value = _state.value.copy(isLoading = true, isLoadingMore = false, errorMessage = null)
        } else {
            _state.value = _state.value.copy(isLoadingMore = true, errorMessage = null)
        }
        val pageIndex = if (reset) 0 else loadedPage + 1
        val query = _state.value.searchQuery.trim().takeIf { it.isNotEmpty() }
        loadJob = viewModelScope.launch {
            try {
                val page = getModels.execute(q = query, page = pageIndex, size = PAGE_SIZE)
                loadedPage = page.page
                totalPages = page.totalPages
                loadedModels = if (reset) {
                    page.content
                } else {
                    appendModels(loadedModels, page.content)
                }
                _state.value = _state.value.copy(
                    totalModelsLabel = totalModelsLabel(page.totalElements),
                    models = loadedModels.map(::optionFrom),
                    isLoading = false,
                    isLoadingMore = false,
                    errorMessage = null,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    errorMessage = e.message,
                )
            }
        }
    }

    companion object {
        internal const val PAGE_SIZE = 20
        private val ptBr = Locale.forLanguageTag("pt-BR")

        internal fun totalModelsLabel(total: Long): String {
            val formatted = NumberFormat.getIntegerInstance(ptBr).format(total)
            return if (total == 1L) "$formatted Modelo" else "$formatted Modelos"
        }

        internal fun optionFrom(model: ModelResponseDTO): OptionItem {
            val materialLabel = model.materials
                .map { material -> material.name.ifBlank { material.code.orEmpty() } }
                .filter { it.isNotBlank() }
                .joinToString(", ")
            val description = listOfNotNull(
                model.manufacturer?.takeIf { it.isNotBlank() },
                materialLabel.takeIf { it.isNotBlank() },
            ).joinToString(" · ")
            val approval = approvalOf(model.approvalStatus)
            return OptionItem(
                name = model.name.orEmpty(),
                description = description,
                statusText = approval?.label,
                statusStyle = approval?.color ?: ZeraColorFamily.Yellow,
            )
        }

        internal fun canLoadMore(loadedPage: Int, totalPages: Int, isBusy: Boolean): Boolean {
            if (isBusy) return false
            return loadedPage + 1 < totalPages
        }

        internal fun appendModels(
            current: List<ModelResponseDTO>,
            incoming: List<ModelResponseDTO>,
        ): List<ModelResponseDTO> {
            val seen = current.mapNotNull { it.id }.toMutableSet()
            return current + incoming.filter { model -> model.id == null || seen.add(model.id) }
        }

        private fun approvalOf(status: String?): Approval? = when (status) {
            "PENDING" -> Approval("Pendente", ZeraColorFamily.Yellow)
            "APPROVED" -> Approval("Aprovado", ZeraColorFamily.Green)
            "REJECTED" -> Approval("Recusado", ZeraColorFamily.Red)
            else -> null
        }

        private data class Approval(val label: String, val color: ZeraColorFamily)
    }
}
