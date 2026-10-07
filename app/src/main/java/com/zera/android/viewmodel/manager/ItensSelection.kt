package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.usecase.inventory.GetItems
import com.zera.android.view.components.inputs.SelectOption
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
    private val getItems = GetItems()

    private val _state = mutableStateOf(ItensSelectionState())
    val state = _state

    private var placeId: String = ""
    private var placeName: String = ""
    private var placeAddress: String = ""
    private var distanceMeters: Long = 0
    private var isOpen: Boolean? = null
    private var description: String = ""
    private var openingDays: List<String> = emptyList()
    private var openingHourLabels: List<String> = emptyList()
    private var recyclingBusinessId: String = ""
    private var contactEmail: String = ""
    private var loadJob: Job? = null
    private var loadedPage: Int = -1
    private var totalPages: Int = 0

    init {
        loadOptions()
    }

    fun bindPlace(
        placeId: String,
        placeName: String,
        placeAddress: String,
        distanceMeters: Long,
        isOpen: Boolean? = null,
        description: String = "",
        openingDays: List<String> = emptyList(),
        openingHourLabels: List<String> = emptyList(),
        recyclingBusinessId: String = "",
        contactEmail: String = "",
    ) {
        this.placeId = placeId
        this.placeName = placeName
        this.placeAddress = placeAddress
        this.distanceMeters = distanceMeters
        this.isOpen = isOpen
        this.description = description
        this.openingDays = openingDays
        this.openingHourLabels = openingHourLabels
        this.recyclingBusinessId = recyclingBusinessId
        this.contactEmail = contactEmail
    }

    fun onSearchQueryChange(value: String) {
        val previous = _state.value.searchQuery
        _state.value = _state.value.copy(searchQuery = value)
        if (value.isEmpty() && previous.isNotEmpty()) {
            loadOptions()
        }
    }

    fun onSearch() {
        loadOptions()
    }

    fun onSelectedValuesChange(values: Set<String>) {
        _state.value = _state.value.copy(selectedValues = values)
    }

    /** Chamado pela tela quando a lista chega perto do fim; anexa a próxima página a `options`. */
    fun loadNextPage() {
        if (!ItensViewModel.canLoadMore(
                loadedPage = loadedPage,
                totalPages = totalPages,
                isBusy = _state.value.isLoading || _state.value.isLoadingMore,
            )
        ) {
            return
        }
        loadOptions(reset = false)
    }

    fun onChooseDateClick() {
        val selected = selectedItems(_state.value.options, _state.value.selectedValues)
        if (selected.isEmpty()) return
        ZeraNavigator.push(
            Route.RecyclingResume(
                placeId = placeId,
                placeName = placeName,
                placeAddress = placeAddress,
                distanceMeters = distanceMeters,
                itemIds = selected.map { it.value },
                itemNames = selected.map { it.name },
                isOpen = isOpen,
                description = description,
                openingDays = openingDays,
                openingHourLabels = openingHourLabels,
                recyclingBusinessId = recyclingBusinessId,
                contactEmail = contactEmail,
            ),
            ScreenAnimation.SlideHorizontal,
        )
    }

    private fun loadOptions(reset: Boolean = true) {
        if (reset) {
            loadJob?.cancel()
            loadedPage = -1
            totalPages = 0
            _state.value = _state.value.copy(isLoading = true, isLoadingMore = false, errorMessage = null)
        } else {
            _state.value = _state.value.copy(isLoadingMore = true, errorMessage = null)
        }
        val pageIndex = if (reset) 0 else loadedPage + 1
        val q = _state.value.searchQuery.trim().takeIf { it.isNotEmpty() }
        loadJob = viewModelScope.launch {
            try {
                val page = getItems.execute(
                    status = AVAILABLE_STATUS,
                    q = q,
                    page = pageIndex,
                    size = PAGE_SIZE,
                )
                loadedPage = page.page
                totalPages = page.totalPages
                val options = if (reset) {
                    page.content.map(::optionFrom)
                } else {
                    appendOptions(_state.value.options, page.content)
                }
                _state.value = _state.value.copy(
                    options = options,
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
        /** Em estoque e ainda não descartado. A API filtra um status por request. */
        internal const val AVAILABLE_STATUS = "IN_STOCK"
        internal const val PAGE_SIZE = 20

        internal fun optionFrom(item: ItemResponseDTO): SelectOption {
            val category = item.model?.category?.name?.takeIf { it.isNotBlank() }
            val code = item.displayCode?.takeIf { it.isNotBlank() } ?: item.id
            val description = if (category == null) "ID $code" else "ID $code · $category"
            return SelectOption(
                name = item.name,
                value = item.id,
                description = description,
            )
        }

        internal fun appendOptions(
            current: List<SelectOption>,
            incoming: List<ItemResponseDTO>,
        ): List<SelectOption> {
            val seen = current.map { it.value }.toMutableSet()
            return current + incoming.map(::optionFrom).filter { seen.add(it.value) }
        }

        internal fun selectedItems(
            options: List<SelectOption>,
            selectedValues: Set<String>,
        ): List<SelectOption> = options.filter { it.value in selectedValues }
    }
}
