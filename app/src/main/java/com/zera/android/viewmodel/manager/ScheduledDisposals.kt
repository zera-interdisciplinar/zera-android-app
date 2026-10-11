package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.DisposalRecordDTO
import com.zera.android.model.dto.inventory.PagedDisposalsDTO
import com.zera.android.model.usecase.inventory.ListDisposalSchedules
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class ScheduledDisposalsState(
    val disposals: List<OptionItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedDestinations: List<String> = emptyList(),
    val selectedPeriod: String? = null,
    val destinationOptions: List<String> = ScheduledDisposalsViewModel.DESTINATION_LABELS,
    val periodOptions: List<String> = ScheduledDisposalsViewModel.PERIOD_LABELS,
)

class ScheduledDisposalsViewModel : ZeraViewModel() {
    private val listDisposals = ListDisposalSchedules()
    private val _state = mutableStateOf(ScheduledDisposalsState())
    val state = _state
    private var visibleIds: List<String> = emptyList()
    private var loadedPage: Int = -1
    private var totalPages: Int = 0
    private var loadJob: Job? = null

    init {
        load(reset = true)
    }

    fun onDisposalClick(disposal: OptionItem) {
        val index = _state.value.disposals.indexOf(disposal)
        val id = visibleIds.getOrNull(index) ?: return
        ZeraNavigator.push(
            Route.SchedulingDetails(scheduleId = id),
            animation = ScreenAnimation.SlideHorizontal,
        )
    }

    fun onSearchQueryChange(query: String) {
        val previous = _state.value.searchQuery
        _state.value = _state.value.copy(searchQuery = query)
        if (query.isEmpty() && previous.isNotEmpty()) load(reset = true)
    }

    fun onSearch() {
        load(reset = true)
    }

    fun applyFilters(destinations: List<String>, period: String?) {
        _state.value = _state.value.copy(
            selectedDestinations = destinations,
            selectedPeriod = period,
        )
        load(reset = true)
    }

    fun onAppliedFilterChipsChange(filters: List<String>) {
        val period = filters.firstOrNull { it in PERIOD_LABELS }
        applyFilters(
            destinations = filters.filter { it in DESTINATION_API.keys },
            period = period,
        )
    }

    fun loadNextPage() {
        if (!canLoadMore(loadedPage, totalPages, _state.value.isLoading || _state.value.isLoadingMore)) return
        load(reset = false)
    }

    private fun load(reset: Boolean) {
        if (reset) {
            loadJob?.cancel()
            loadedPage = -1
            totalPages = 0
            records = emptyList()
            visibleIds = emptyList()
            _state.value = _state.value.copy(
                isLoading = true,
                isLoadingMore = false,
                errorMessage = null,
                disposals = emptyList(),
            )
        } else {
            _state.value = _state.value.copy(isLoadingMore = true, errorMessage = null)
        }
        val pageIndex = if (reset) 0 else loadedPage + 1
        val current = _state.value
        loadJob = viewModelScope.launch {
            try {
                val queries = queriesFor(
                    searchQuery = current.searchQuery,
                    destinations = current.selectedDestinations,
                    period = current.selectedPeriod,
                    today = LocalDate.now(),
                )
                val pages = queries.map { query ->
                    listDisposals.execute(
                        page = pageIndex,
                        size = PAGE_SIZE,
                        q = query.q,
                        destination = query.destination,
                        disposedFrom = query.disposedFrom,
                        disposedTo = query.disposedTo,
                    )
                }
                val page = mergePages(pages)
                loadedPage = page.page
                totalPages = page.totalPages
                val shown = if (reset) {
                    page.content
                } else {
                    records + appendRecords(visibleIds, page.content)
                }
                show(shown)
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

    private var records: List<DisposalRecordDTO> = emptyList()

    private fun show(items: List<DisposalRecordDTO>) {
        records = items
        visibleIds = items.map { it.id }
        _state.value = _state.value.copy(
            isLoading = false,
            isLoadingMore = false,
            errorMessage = null,
            disposals = items.map { disposal ->
                OptionItem(
                    name = disposal.placeName.orEmpty().ifBlank { "Descarte" },
                    description = disposalLine(disposal.disposedAt, disposal.itemCount()),
                )
            },
        )
    }

    companion object {
        internal const val PAGE_SIZE = 20
        internal const val LABEL_RECYCLING = "Reciclagem"
        internal const val LABEL_LANDFILL = "Aterro"
        internal const val LABEL_DONATION = "Doação"
        internal const val PERIOD_7 = "Últimos 7 dias"
        internal const val PERIOD_30 = "Últimos 30 dias"
        internal const val PERIOD_MONTH = "Este mês"

        internal val DESTINATION_LABELS = listOf(LABEL_RECYCLING, LABEL_LANDFILL, LABEL_DONATION)
        internal val PERIOD_LABELS = listOf(PERIOD_7, PERIOD_30, PERIOD_MONTH)

        private val DESTINATION_API = linkedMapOf(
            LABEL_RECYCLING to "RECYCLING",
            LABEL_LANDFILL to "LANDFILL",
            LABEL_DONATION to "DONATION",
        )
        private val ptBr = Locale.forLanguageTag("pt-BR")

        internal data class DisposalQuery(
            val q: String?,
            val destination: String?,
            val disposedFrom: String?,
            val disposedTo: String?,
        )

        /** `destination` é um valor só. Vários chips viram uma chamada por destino, na mesma página. */
        internal fun queriesFor(
            searchQuery: String,
            destinations: List<String>,
            period: String?,
            today: LocalDate,
        ): List<DisposalQuery> {
            val q = searchQuery.trim().takeIf { it.isNotEmpty() }
            val (from, to) = disposedRange(period, today)
            val values = destinations.mapNotNull(DESTINATION_API::get).ifEmpty { listOf(null) }
            return values.map { destination ->
                DisposalQuery(q = q, destination = destination, disposedFrom = from, disposedTo = to)
            }
        }

        internal fun disposedRange(period: String?, today: LocalDate): Pair<String?, String?> {
            val from = when (period) {
                PERIOD_7 -> today.minusDays(6)
                PERIOD_30 -> today.minusDays(29)
                PERIOD_MONTH -> today.withDayOfMonth(1)
                else -> null
            } ?: return null to null
            return from.toString() to today.toString()
        }

        internal fun canLoadMore(loadedPage: Int, totalPages: Int, isBusy: Boolean): Boolean {
            if (isBusy) return false
            return loadedPage + 1 < totalPages
        }

        internal fun mergePages(pages: List<PagedDisposalsDTO>): PagedDisposalsDTO {
            if (pages.isEmpty()) return PagedDisposalsDTO()
            val seen = mutableSetOf<String>()
            return PagedDisposalsDTO(
                content = pages.flatMap { it.content }.filter { seen.add(it.id) },
                page = pages.first().page,
                size = PAGE_SIZE,
                totalElements = pages.sumOf { it.totalElements },
                totalPages = pages.maxOf { it.totalPages },
            )
        }

        internal fun appendRecords(
            currentIds: List<String>,
            incoming: List<DisposalRecordDTO>,
        ): List<DisposalRecordDTO> {
            val seen = currentIds.toMutableSet()
            return incoming.filter { seen.add(it.id) }
        }

        internal fun formatDisposedAt(raw: String): String {
            val date = runCatching { LocalDate.parse(raw.take(10)) }.getOrNull() ?: return raw
            val month = date.month.getDisplayName(TextStyle.SHORT, ptBr)
                .replace(".", "")
                .take(3)
                .lowercase(ptBr)
            return "${date.dayOfMonth} $month"
        }

        internal fun disposalLine(raw: String, itemCount: Int): String {
            val whenLabel = formatDisposedAt(raw)
            val items = if (itemCount == 1) "1 item" else "$itemCount itens"
            return "$whenLabel - $items"
        }
    }
}
