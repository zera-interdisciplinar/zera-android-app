package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.DisposalScheduleDetailDTO
import com.zera.android.model.usecase.inventory.CancelDisposalSchedule
import com.zera.android.model.usecase.inventory.GetDisposalSchedule
import com.zera.android.model.usecase.inventory.UpdateDisposalSchedule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class SchedulingDetailsState(
    val recyclerName: String = "",
    val scheduledAt: String = "",
    val placeName: String = "",
    val itemsLabel: String = "",
    val options: List<OptionItem> = emptyList(),
    val contactEmail: String = "",
    val contactPhone: String = "",
    val contactButtonsEnabled: Boolean = false,
    val pendingUri: String? = null,
    val rawScheduledAt: String = "",
    val showDateSheet: Boolean = false,
    val isSaving: Boolean = false,
    val isCancelling: Boolean = false,
    val errorMessage: String? = null,
)

class SchedulingDetailsViewModel : ZeraViewModel() {
    private val getDisposalSchedule = GetDisposalSchedule()
    private val updateDisposalSchedule = UpdateDisposalSchedule()
    private val cancelDisposalSchedule = CancelDisposalSchedule()
    private val _state = mutableStateOf(SchedulingDetailsState())
    val state = _state
    private var scheduleId: String = ""

    fun load(scheduleId: String) {
        if (scheduleId.isBlank() || scheduleId == this.scheduleId) return
        this.scheduleId = scheduleId
        _state.value = _state.value.copy(errorMessage = null)
        viewModelScope.launch {
            try {
                apply(getDisposalSchedule.execute(scheduleId))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.message)
            }
        }
    }

    fun onOptionClick(option: OptionItem) {
        if (option.name == "Alterar data ou horário") {
            _state.value = _state.value.copy(showDateSheet = true, errorMessage = null)
            return
        }
        _state.value = _state.value.copy(errorMessage = "A edição de produtos ainda não tem tela.")
    }

    fun dismissDateSheet() {
        if (_state.value.isSaving) return
        _state.value = _state.value.copy(showDateSheet = false)
    }

    fun confirmSchedule(dateEpochDayMillis: Long, hour: Int, minute: Int) {
        val id = scheduleId
        if (id.isBlank() || _state.value.isSaving) return
        val scheduledAt = LocalDateTime.of(
            LocalDate.ofEpochDay(dateEpochDayMillis / MILLIS_PER_DAY),
            LocalTime.of(hour, minute),
        )
        _state.value = _state.value.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            try {
                apply(updateDisposalSchedule.execute(id, scheduledAt))
                _state.value = _state.value.copy(isSaving = false, showDateSheet = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSaving = false, errorMessage = e.message)
            }
        }
    }

    fun onGenerateReportClick() {
        _state.value = _state.value.copy(
            errorMessage = "O relatório só existe depois que o descarte for registrado.",
        )
    }

    fun onEmailClick() {
        val email = _state.value.contactEmail
        if (email.isBlank()) return
        _state.value = _state.value.copy(pendingUri = "mailto:$email")
    }

    fun onPhoneClick() {
        val phone = _state.value.contactPhone.filter { it.isDigit() || it == '+' }
        if (phone.isBlank()) return
        _state.value = _state.value.copy(pendingUri = "tel:$phone")
    }

    fun consumePendingUri() {
        _state.value = _state.value.copy(pendingUri = null)
    }

    fun onCancelSchedulingClick() {
        val id = scheduleId
        if (id.isBlank() || _state.value.isCancelling) return
        _state.value = _state.value.copy(isCancelling = true, errorMessage = null)
        viewModelScope.launch {
            try {
                cancelDisposalSchedule.execute(id)
                ZeraNavigator.goBack()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(isCancelling = false, errorMessage = e.message)
            }
        }
    }

    private fun apply(detail: DisposalScheduleDetailDTO) {
        val email = detail.contact?.email.orEmpty()
        val phone = detail.contact?.phone.orEmpty()
        val names = detail.items.map { it.name.ifBlank { it.displayCode } }.filter { it.isNotBlank() }
        _state.value = _state.value.copy(
            recyclerName = detail.placeName,
            scheduledAt = ScheduledDisposalsViewModel.formatSchedule(detail.scheduledAt),
            placeName = detail.placeName,
            itemsLabel = names.joinToString(", "),
            options = listOf(
                OptionItem(
                    name = "Alterar data ou horário",
                    description = ScheduledDisposalsViewModel.formatSchedule(detail.scheduledAt),
                ),
                OptionItem(
                    name = "Adicionar ou remover produtos",
                    description = names.joinToString(", ").ifBlank { "Nenhum produto" },
                ),
            ),
            contactEmail = email,
            contactPhone = phone,
            contactButtonsEnabled = email.isNotBlank() || phone.isNotBlank(),
            rawScheduledAt = detail.scheduledAt,
            errorMessage = null,
        )
    }

    companion object {
        private const val MILLIS_PER_DAY = 86_400_000L

        internal fun initialDateMillis(iso: String): Long? {
            val parsed = runCatching { LocalDateTime.parse(iso) }.getOrNull() ?: return null
            return parsed.toLocalDate().toEpochDay() * MILLIS_PER_DAY
        }

        internal fun initialTime(iso: String): Pair<Int, Int> {
            val parsed = runCatching { LocalDateTime.parse(iso) }.getOrNull()
            return (parsed?.hour ?: 8) to (parsed?.minute ?: 0)
        }
    }
}
