package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.usecase.inventory.ListDisposalSchedules
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

data class ScheduledDisposalsState(
    val nextDisposalDate: String = "",
    val nextDisposalRecyclerName: String = "",
    val disposals: List<OptionItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val hasNextDisposal: Boolean
        get() = nextDisposalDate.isNotEmpty()
}

class ScheduledDisposalsViewModel : ZeraViewModel() {
    private val listDisposalSchedules = ListDisposalSchedules()
    private val _state = mutableStateOf(ScheduledDisposalsState())
    val state = _state
    private var scheduleIds: List<String> = emptyList()

    init {
        loadDisposals()
    }

    fun onScheduleNextClick() {
        ZeraNavigator.push(Route.Recycling, animation = ScreenAnimation.SlideHorizontal)
    }

    fun onDisposalClick(disposal: OptionItem) {
        val index = _state.value.disposals.indexOf(disposal)
        val id = scheduleIds.getOrNull(index) ?: return
        ZeraNavigator.push(
            Route.SchedulingDetails(scheduleId = id),
            animation = ScreenAnimation.SlideHorizontal,
        )
    }

    private fun loadDisposals() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val schedules = listDisposalSchedules.execute()
                scheduleIds = schedules.map { it.id }
                val next = schedules.firstOrNull()
                _state.value = _state.value.copy(
                    isLoading = false,
                    nextDisposalDate = next?.let { formatSchedule(it.scheduledAt) }.orEmpty(),
                    nextDisposalRecyclerName = next?.placeName.orEmpty(),
                    disposals = schedules.map { schedule ->
                        OptionItem(
                            name = schedule.placeName,
                            description = scheduleLine(schedule.scheduledAt, schedule.itemCount),
                        )
                    },
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    companion object {
        private val ptBr = Locale.forLanguageTag("pt-BR")

        internal fun formatSchedule(iso: String): String {
            val dateTime = LocalDateTime.parse(iso)
            val month = dateTime.month.getDisplayName(TextStyle.SHORT, ptBr)
                .replace(".", "")
                .take(3)
                .lowercase(ptBr)
            val hour = dateTime.hour.toString().padStart(2, '0')
            val minute = dateTime.minute.toString().padStart(2, '0')
            return "${dateTime.dayOfMonth} $month · $hour:$minute"
        }

        internal fun scheduleLine(iso: String, itemCount: Int): String {
            val hour = runCatching { LocalDateTime.parse(iso).hour }.getOrNull()
            val whenLabel = if (hour == null) iso else "Às ${hour}h"
            val items = if (itemCount == 1) "1 item" else "$itemCount itens"
            return "$whenLabel - $items"
        }
    }
}
