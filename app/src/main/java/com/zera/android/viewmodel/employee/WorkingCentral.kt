package com.zera.android.viewmodel.employee

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.PendingItemResponseDTO
import com.zera.android.model.dto.inventory.WorkCenterResponseDTO
import com.zera.android.model.usecase.inventory.GetEmployeePendencies
import com.zera.android.model.usecase.inventory.GetItemDetails
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class WorkingCentralState(
    val maintenanceCount: Int = 0,
    val priorityNotifications: List<NotificationItem> = emptyList(),
) {
    val pendingCount: Int get() = priorityNotifications.size

    val shiftSummary: String
        get() = "$pendingCount pendências · $maintenanceCount em manutenção"
}

/**
 * ViewModel da Central de Trabalho do operário.
 *
 * Lista item a item o mesmo `GET /dashboard/work-center` da home; a home só mostra o resumo.
 * Rascunho sem `missingFields` consulta `GET /items/{id}` para montar o texto do que falta.
 */
class WorkingCentralViewModel : ZeraViewModel() {
    private val getEmployeePendencies = GetEmployeePendencies()
    private val getItemDetails = GetItemDetails()
    private val _state = mutableStateOf(WorkingCentralState())
    val state = _state

    init {
        viewModelScope.launch { loadWorkCenter() }
    }

    private suspend fun loadWorkCenter() {
        try {
            val workCenter = getEmployeePendencies.execute()
            val drafts = workCenter.drafts.map { draft -> enrichDraft(draft) }
            _state.value = WorkingCentralState(
                maintenanceCount = workCenter.inMaintenance.toInt(),
                priorityNotifications = priorityFrom(workCenter.copy(drafts = drafts)),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            _state.value = WorkingCentralState()
        }
    }

    private suspend fun enrichDraft(draft: PendingItemResponseDTO): PendingItemResponseDTO {
        if (draft.missingFields.isNotEmpty()) return draft
        return try {
            val item = getItemDetails.execute(draft.id)
            draft.copy(missingFields = missingFieldsFrom(item))
        } catch (_: Exception) {
            draft
        }
    }

    fun onNotificationClick(notification: NotificationItem) {
        if (!notification.resolvable) return
        ZeraNavigator.push(Route.ItemDetails(itemId = notification.id))
    }

    companion object {
        internal fun priorityFrom(workCenter: WorkCenterResponseDTO): List<NotificationItem> {
            val drafts = workCenter.drafts.map { item ->
                NotificationItem(
                    id = item.id,
                    label = "Completar cadastro",
                    text = draftText(item),
                    style = ZeraColorFamily.Yellow,
                    resolvable = true,
                )
            }
            val rejected = workCenter.rejected.map { item ->
                NotificationItem(
                    id = item.id,
                    label = "Recusado pelo gestor",
                    text = rejectedText(item),
                    style = ZeraColorFamily.Red,
                    resolvable = true,
                )
            }
            val damaged = workCenter.damagedWithoutDestination.map { item ->
                NotificationItem(
                    id = item.id,
                    label = "Enviar para manutenção",
                    text = "${item.name} · aguardando destino",
                    style = ZeraColorFamily.Blue,
                )
            }
            return drafts + rejected + damaged
        }

        internal fun missingFieldsFrom(item: ItemResponseDTO): List<String> {
            if (item.missingFields.isNotEmpty()) return item.missingFields
            return buildList {
                if (item.photoUrl.isNullOrBlank()) add("photoUrl")
                if (item.serialNumber.isNullOrBlank()) add("serialNumber")
                if (item.condition.isNullOrBlank()) add("condition")
            }
        }

        internal fun draftText(item: PendingItemResponseDTO): String {
            if (item.missingFields.isEmpty()) return item.name
            val labels = item.missingFields.map(::missingFieldLabel).distinct()
            return "${item.name} · faltam ${labels.joinToString(", ")}"
        }

        internal fun missingFieldLabel(field: String): String {
            val key = field.trim().replace("_", "").replace("-", "").lowercase()
            return when (key) {
                "photourl", "photo" -> "foto"
                "serialnumber", "serial" -> "número de série"
                "barcode" -> "código de barras"
                "damages", "damagetype" -> "tipo de dano"
                "condition" -> "condição"
                "acquiredat" -> "data de aquisição"
                "manufacturingyear" -> "ano de fabricação"
                "usageintensity" -> "intensidade de uso"
                "name" -> "nome"
                "modelid", "model" -> "modelo"
                else -> field
            }
        }

        private fun rejectedText(item: PendingItemResponseDTO): String {
            val reason = item.rejectionReason
            if (reason.isNullOrBlank()) return item.name
            return "${item.name} · $reason"
        }
    }
}
