package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.PagedItemsDTO
import com.zera.android.model.usecase.inventory.GetCategories
import com.zera.android.model.usecase.inventory.GetItems
import com.zera.android.view.components.lists.ProductItem
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class ItemsQuery(
    val status: String? = null,
    val categoryId: String? = null,
    val q: String? = null,
)

data class ItensState(
    val searchQuery: String = "",
    val categoryOptions: List<String> = emptyList(),
    val selectedStatuses: List<String> = emptyList(),
    val selectedCategories: List<String> = emptyList(),
    val totalItemsLabel: String = "",
    val items: List<ProductItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
)

class ItensViewModel : ZeraViewModel() {
    private val getItems = GetItems()
    private val getCategories = GetCategories()

    private val _state = mutableStateOf(ItensState())
    val state = _state

    private var categories: List<CategoryResponseDTO> = emptyList()
    private var loadJob: Job? = null
    private var loadedPage: Int = -1
    private var totalPages: Int = 0

    init {
        loadCategories()
        loadItems()
    }

    fun onSearchQueryChange(query: String) {
        val previous = _state.value.searchQuery
        _state.value = _state.value.copy(searchQuery = query)
        if (query.isEmpty() && previous.isNotEmpty()) {
            loadItems()
        }
    }

    fun onSearch() {
        loadItems()
    }

    fun applyFilters(statuses: List<String>, categories: List<String>) {
        _state.value = _state.value.copy(
            selectedStatuses = statuses,
            selectedCategories = categories,
        )
        loadItems()
    }

    fun onAppliedFilterChipsChange(filters: List<String>) {
        val statusLabels = STATUS_API_VALUES.keys
        applyFilters(
            statuses = filters.filter { it in statusLabels },
            categories = filters.filter { it in _state.value.categoryOptions },
        )
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
        loadItems(reset = false)
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                categories = getCategories.execute()
                _state.value = _state.value.copy(
                    categoryOptions = categories.map { it.name },
                )
            } catch (_: Exception) {
                // A listagem ainda funciona sem o chip de categoria populado.
            }
        }
    }

    private fun loadItems(reset: Boolean = true) {
        if (reset) {
            loadJob?.cancel()
            loadedPage = -1
            totalPages = 0
            _state.value = _state.value.copy(isLoading = true, isLoadingMore = false, errorMessage = null)
        } else {
            _state.value = _state.value.copy(isLoadingMore = true, errorMessage = null)
        }
        val pageIndex = if (reset) 0 else loadedPage + 1
        loadJob = viewModelScope.launch {
            try {
                val queries = queriesFor(
                    selectedStatuses = _state.value.selectedStatuses,
                    selectedCategories = _state.value.selectedCategories,
                    searchQuery = _state.value.searchQuery,
                    categories = categories,
                )
                if (queries.isEmpty()) {
                    loadedPage = -1
                    totalPages = 0
                    _state.value = _state.value.copy(
                        totalItemsLabel = totalItemsLabel(0),
                        items = emptyList(),
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = null,
                    )
                    return@launch
                }
                val pages = queries.map { query ->
                    getItems.execute(
                        status = query.status,
                        categoryId = query.categoryId,
                        q = query.q,
                        page = pageIndex,
                        size = PAGE_SIZE,
                    )
                }
                val page = mergePages(pages)
                loadedPage = page.page
                totalPages = page.totalPages
                val items = if (reset) {
                    page.content.map(::productFrom)
                } else {
                    appendProducts(_state.value.items, page.content)
                }
                _state.value = _state.value.copy(
                    totalItemsLabel = totalItemsLabel(page.totalElements),
                    items = items,
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
        private val STATUS_API_VALUES = linkedMapOf(
            "Pendente" to "PENDING_APPROVAL",
            "Recusado" to "REJECTED",
            "Aprovado" to "IN_STOCK",
            "Em manutenção" to "IN_MAINTENANCE",
            "Em aprovação" to "AWAITING_EVALUATION",
            "Descartado" to "DISPOSED",
        )

        internal fun productFrom(item: ItemResponseDTO): ProductItem {
            val status = ItemStatus.fromBackend(item.status)
            return ProductItem(
                id = item.id,
                name = item.name,
                statusText = status?.label,
                statusStyle = status?.colorFamily ?: ZeraColorFamily.Yellow,
            )
        }

        internal fun totalItemsLabel(total: Long): String {
            val formatted = NumberFormat.getIntegerInstance(ptBr).format(total)
            return if (total == 1L) "$formatted Item" else "$formatted Itens"
        }

        internal fun queriesFor(
            selectedStatuses: List<String>,
            selectedCategories: List<String>,
            searchQuery: String,
            categories: List<CategoryResponseDTO>,
        ): List<ItemsQuery> {
            val q = searchQuery.trim().takeIf { it.isNotEmpty() }
            val statusValues = if (selectedStatuses.isEmpty()) {
                listOf(null)
            } else {
                selectedStatuses.mapNotNull(STATUS_API_VALUES::get)
            }
            val categoryIds = if (selectedCategories.isEmpty()) {
                listOf(null)
            } else {
                selectedCategories.mapNotNull { selected ->
                    categories.find { it.name == selected }?.id
                }
            }
            if (statusValues.isEmpty() || categoryIds.isEmpty()) return emptyList()

            return statusValues.flatMap { status ->
                categoryIds.map { categoryId ->
                    ItemsQuery(status = status, categoryId = categoryId, q = q)
                }
            }
        }

        internal fun mergePages(pages: List<PagedItemsDTO>): PagedItemsDTO {
            val seen = mutableSetOf<String>()
            val content = pages.flatMap { it.content }.filter { seen.add(it.id) }
            return PagedItemsDTO(
                content = content,
                page = pages.first().page,
                size = PAGE_SIZE,
                totalElements = pages.sumOf { it.totalElements },
                totalPages = pages.maxOf { it.totalPages },
            )
        }

        internal fun canLoadMore(loadedPage: Int, totalPages: Int, isBusy: Boolean): Boolean {
            if (isBusy) return false
            return loadedPage + 1 < totalPages
        }

        internal fun appendProducts(
            current: List<ProductItem>,
            incoming: List<ItemResponseDTO>,
        ): List<ProductItem> {
            val seen = current.map { it.id }.toMutableSet()
            return current + incoming.map(::productFrom).filter { seen.add(it.id) }
        }
    }
}
