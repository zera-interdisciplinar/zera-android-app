package com.zera.android.viewmodel.main.recycling

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.DisposalRecordDTO
import com.zera.android.model.usecase.ai.GenerateDisposalReport
import com.zera.android.model.usecase.ai.GetDisposalReport
import com.zera.android.model.usecase.auth.GetSelfUser
import com.zera.android.model.usecase.inventory.CancelDisposalSchedule
import com.zera.android.model.usecase.inventory.GetDisposalSchedule
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.view.components.navigation.SideBarItem
import com.zera.android.viewmodel.canCancelDisposal
import com.zera.android.viewmodel.isEmployeeRole
import com.zera.android.viewmodel.sideBarItemsFor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class SchedulingDetailsState(
    val scheduledAt: String = "",
    val placeName: String = "",
    val options: List<OptionItem> = emptyList(),
    val contactEmail: String = "",
    val contactPhone: String = "",
    val contactButtonsEnabled: Boolean = false,
    val pendingUri: String? = null,
    val reportUrl: String? = null,
    val isLoading: Boolean = true,
    val isGeneratingReport: Boolean = false,
    val isCancelling: Boolean = false,
    val canCancel: Boolean = false,
    val sideBarItems: List<SideBarItem> = emptyList(),
    val errorMessage: String? = null,
)

class SchedulingDetailsViewModel : ZeraViewModel() {
    private val getDisposal = GetDisposalSchedule()
    private val cancelDisposal = CancelDisposalSchedule()
    private val generateReport = GenerateDisposalReport()
    private val getReport = GetDisposalReport()
    private val getSelfUser = GetSelfUser()
    private val _state = mutableStateOf(SchedulingDetailsState())
    val state = _state
    private var disposalId: String = ""
    private var itemIds: List<String> = emptyList()
    private var itemNames: List<String> = emptyList()

    fun load(scheduleId: String) {
        if (scheduleId.isBlank()) {
            // Sem id não há o que carregar; evita deixar o isLoading inicial preso em true.
            _state.value = _state.value.copy(isLoading = false)
            return
        }
        if (scheduleId == this.disposalId) return
        this.disposalId = scheduleId
        _state.value = _state.value.copy(isLoading = true, errorMessage = null, reportUrl = null)
        viewModelScope.launch {
            try {
                val user = getSelfUser.execute()
                _state.value = _state.value.copy(
                    canCancel = canCancelDisposal(user.role),
                    sideBarItems = sideBarItemsFor(isEmployeeRole(user.role)),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _state.value = _state.value.copy(canCancel = false, sideBarItems = sideBarItemsFor(isEmployee = false))
            }
        }
        viewModelScope.launch {
            try {
                apply(getDisposal.execute(scheduleId))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
        viewModelScope.launch {
            lookupReport(scheduleId)
        }
    }

    fun onItemsClick() {
        ZeraNavigator.push(
            Route.ItensResume(
                itemIds = itemIds,
                itemNames = itemNames,
                title = "Itens descartados",
            ),
            animation = ScreenAnimation.SlideHorizontal,
        )
    }

    fun onGenerateReportClick() {
        val existing = _state.value.reportUrl
        if (!existing.isNullOrBlank()) {
            _state.value = _state.value.copy(pendingUri = existing, errorMessage = null)
            return
        }
        val id = disposalId
        if (id.isBlank() || _state.value.isGeneratingReport) return
        _state.value = _state.value.copy(isGeneratingReport = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val reportUrl = generateReport.execute(id)
                _state.value = _state.value.copy(
                    isGeneratingReport = false,
                    reportUrl = reportUrl,
                    pendingUri = reportUrl,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isGeneratingReport = false,
                    errorMessage = e.message ?: "Não foi possível gerar o relatório.",
                )
            }
        }
    }

    private suspend fun lookupReport(id: String) {
        try {
            val url = getReport.execute(id) ?: return
            if (disposalId != id) return
            _state.value = _state.value.copy(reportUrl = url)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // Sem PDF ainda: o botão continua em "Gerar relatório".
        }
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
        val id = disposalId
        if (!_state.value.canCancel || id.isBlank() || _state.value.isCancelling) return
        _state.value = _state.value.copy(isCancelling = true, errorMessage = null)
        viewModelScope.launch {
            try {
                cancelDisposal.execute(id)
                ZeraNavigator.goBack()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(isCancelling = false, errorMessage = cancelError(e))
            }
        }
    }

    private fun apply(detail: DisposalRecordDTO) {
        itemIds = detail.items.map { item ->
            item.itemId.ifBlank { item.id }.ifBlank { item.displayCode }.ifBlank { "item" }
        }
        itemNames = detail.items.map { item ->
            item.name.ifBlank { item.displayCode }.ifBlank { "Item" }
        }
        val names = itemNames
        _state.value = _state.value.copy(
            isLoading = false,
            scheduledAt = ScheduledDisposalsViewModel.formatDisposedAt(detail.disposedAt),
            placeName = detail.placeName.orEmpty(),
            options = listOf(
                OptionItem(
                    name = "Itens descartados",
                    description = names.joinToString(", ").ifBlank { "Nenhum item" },
                ),
            ),
            errorMessage = null,
        )
    }

    private fun cancelError(error: Exception): String {
        if (error is HttpException && error.code() == 403) {
            return "Só o gestor pode cancelar um descarte."
        }
        if (error is HttpException && error.code() == 409) {
            return "Esse descarte já foi cancelado."
        }
        return error.message ?: "Não foi possível cancelar o descarte."
    }
}
