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
    val extraStatuses: List<String> = emptyList(),
    val categoryId: String? = null,
    val q: String? = null,
)

data class ItensState(
    val searchQuery: String = "",
    val filterOptions: List<String> = listOf(
        FILTER_ALL,
        FILTER_PENDING,
        FILTER_CATEGORY,
    ),
    val selectedFilter: String = FILTER_ALL,
    val totalItemsLabel: String = "",
    val items: List<ProductItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
) {
    companion object {
        const val FILTER_ALL = "Todos"
        const val FILTER_PENDING = "Pendentes"
        const val FILTER_CATEGORY = "Categoria"
    }
}

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

    fun onFilterChange(filter: String) {
        _state.value = _state.value.copy(selectedFilter = filter)
        loadItems()
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
                val options = filterOptionsFor(categories)
                val selected = _state.value.selectedFilter
                _state.value = _state.value.copy(
                    filterOptions = options,
                    selectedFilter = if (selected in options) selected else ItensState.FILTER_ALL,
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
                val query = queryFor(
                    selectedFilter = _state.value.selectedFilter,
                    searchQuery = _state.value.searchQuery,
                    categories = categories,
                )
                val page = fetchPage(query, pageIndex)
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

    private suspend fun fetchPage(query: ItemsQuery, pageIndex: Int): PagedItemsDTO {
        return if (query.extraStatuses.isEmpty()) {
            getItems.execute(
                status = query.status,
                categoryId = query.categoryId,
                q = query.q,
                page = pageIndex,
                size = PAGE_SIZE,
            )
        } else {
            val first = getItems.execute(
                status = query.status,
                q = query.q,
                page = pageIndex,
                size = PAGE_SIZE,
            )
            val second = getItems.execute(
                status = query.extraStatuses.first(),
                q = query.q,
                page = pageIndex,
                size = PAGE_SIZE,
            )
            mergePages(first, second)
        }
    }

    companion object {
        internal const val PAGE_SIZE = 20
        private val ptBr = Locale.forLanguageTag("pt-BR")

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

        internal fun queryFor(
            selectedFilter: String,
            searchQuery: String,
            categories: List<CategoryResponseDTO>,
        ): ItemsQuery {
            val q = searchQuery.trim().takeIf { it.isNotEmpty() }
            return when (selectedFilter) {
                ItensState.FILTER_ALL -> ItemsQuery(q = q)
                ItensState.FILTER_PENDING -> ItemsQuery(
                    status = "PENDING_APPROVAL",
                    extraStatuses = listOf("AWAITING_EVALUATION"),
                    q = q,
                )
                else -> ItemsQuery(
                    categoryId = categories.find { it.name == selectedFilter }?.id,
                    q = q,
                )
            }
        }

        internal fun filterOptionsFor(categories: List<CategoryResponseDTO>): List<String> {
            if (categories.isEmpty()) {
                return listOf(
                    ItensState.FILTER_ALL,
                    ItensState.FILTER_PENDING,
                    ItensState.FILTER_CATEGORY,
                )
            }
            return listOf(ItensState.FILTER_ALL, ItensState.FILTER_PENDING) + categories.map { it.name }
        }

        internal fun mergePages(first: PagedItemsDTO, second: PagedItemsDTO): PagedItemsDTO {
            val seen = mutableSetOf<String>()
            val content = (first.content + second.content).filter { seen.add(it.id) }
            return PagedItemsDTO(
                content = content,
                page = first.page,
                size = PAGE_SIZE,
                totalElements = first.totalElements + second.totalElements,
                totalPages = maxOf(first.totalPages, second.totalPages),
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
