package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.usecase.inventory.GetIndicators
import com.zera.android.view.components.graphs.BarGraphItem
import com.zera.android.view.components.graphs.VerticalBarGraphItem
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs

data class IndexesState(
    val periodFilters: List<String> = listOf(FILTER_ALL, FILTER_THIS_MONTH, FILTER_CATEGORY),
    val selectedFilter: String = FILTER_ALL,
    val recyclingRateLabel: String = "",
    val recyclingRateDeltaLabel: String = "",
    val recyclingRateDeltaPositive: Boolean = true,
    val recyclingRateProgress: Float = 0f,
    val monthlyEvolution: List<VerticalBarGraphItem> = emptyList(),
    val residuesByCategory: List<BarGraphItem> = emptyList(),
    val errorMessage: String? = null,
) {
    companion object {
        const val FILTER_ALL = "Todos"
        const val FILTER_THIS_MONTH = "Este mês"
        const val FILTER_CATEGORY = "Categoria"
    }
}

class IndexesViewModel : ZeraViewModel() {
    private val getIndicators = GetIndicators()

    private val _state = mutableStateOf(IndexesState())
    val state = _state

    init {
        loadIndexes()
    }

    fun onFilterChange(filter: String) {
        _state.value = _state.value.copy(selectedFilter = filter)
        loadIndexes(filter)
    }

    private fun loadIndexes(filter: String = _state.value.selectedFilter) {
        _state.value = _state.value.copy(errorMessage = null)
        viewModelScope.launch {
            try {
                val (from, to) = periodFor(filter)
                val indicators = getIndicators.execute(from = from, to = to)
                _state.value = _state.value.copy(
                    recyclingRateLabel = formatPercent(indicators.recyclingRatePercent),
                    recyclingRateDeltaLabel = formatDeltaPoints(indicators.recyclingRateChangePoints).orEmpty(),
                    recyclingRateDeltaPositive = indicators.recyclingRateChangePoints?.let { it >= 0.0 } ?: true,
                    recyclingRateProgress = (indicators.recyclingRatePercent / 100.0).toFloat().coerceIn(0f, 1f),
                    monthlyEvolution = indicators.monthlyWeightKg.map { month ->
                        VerticalBarGraphItem(
                            label = monthLabel(month.month),
                            value = month.weightKg.toFloat(),
                        )
                    },
                    residuesByCategory = indicators.weightByMaterial.map { material ->
                        BarGraphItem(
                            label = materialLabel(material.material),
                            percentage = (material.percent / 100.0).toFloat().coerceIn(0f, 1f),
                        )
                    },
                    errorMessage = null,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.message)
            }
        }
    }

    companion object {
        private val ptBr = Locale.forLanguageTag("pt-BR")

        internal fun periodFor(
            filter: String,
            today: LocalDate = LocalDate.now(),
        ): Pair<String?, String?> = when (filter) {
            IndexesState.FILTER_THIS_MONTH ->
                today.withDayOfMonth(1).toString() to today.toString()
            else -> null to null
        }

        internal fun formatPercent(value: Double): String = "${formatDecimal(value)}%"

        internal fun formatDeltaPoints(value: Double?): String? {
            if (value == null) return null
            val arrow = if (value >= 0) "↑" else "↓"
            return "$arrow ${formatDecimal(abs(value))}% no período"
        }

        internal fun monthLabel(yearMonth: String): String {
            val monthNumber = yearMonth.split("-").getOrNull(1)?.toIntOrNull() ?: return yearMonth
            if (monthNumber !in 1..12) return yearMonth
            return Month.of(monthNumber)
                .getDisplayName(TextStyle.SHORT, ptBr)
                .replace(".", "")
                .replaceFirstChar { it.uppercase() }
                .take(3)
        }

        internal fun materialLabel(code: String): String = when (code) {
            "PLASTIC" -> "Plásticos"
            "METAL" -> "Metal"
            "GLASS" -> "Vidro"
            "PAPER" -> "Papel"
            "BATTERY" -> "Baterias"
            "CIRCUIT_BOARD" -> "Placas"
            "CABLE" -> "Cabos"
            "SCREEN" -> "Telas"
            "OTHER" -> "Outros"
            else -> code
        }

        private fun formatDecimal(value: Double): String {
            val format = NumberFormat.getNumberInstance(ptBr)
            format.minimumFractionDigits = 0
            format.maximumFractionDigits = 1
            return format.format(value)
        }
    }
}
