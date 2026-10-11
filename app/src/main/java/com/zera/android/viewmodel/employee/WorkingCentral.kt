package com.zera.android.viewmodel.employee

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.viewmodel.ZeraViewModel

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
 * Os dados ainda são fixos (mock) até o back expor as pendências e a manutenção do operário.
 */
class WorkingCentralViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(
        WorkingCentralState(
            maintenanceCount = 25,
            priorityNotifications = listOf(
                NotificationItem(
                    id = "incomplete-registration",
                    label = "Completar cadastro",
                    text = "Teclado mecânico · faltam danos",
                    style = ZeraColorFamily.Yellow,
                ),
                NotificationItem(
                    id = "rejected-by-manager",
                    label = "Recusado pelo gestor",
                    text = "Chip controlador",
                    style = ZeraColorFamily.Red,
                ),
                NotificationItem(
                    id = "send-to-maintenance",
                    label = "Enviar para manutenção",
                    text = "Mouse sem fio · aguardando destino",
                    style = ZeraColorFamily.Blue,
                ),
            ),
        )
    )
    val state = _state

    fun onNotificationClick(notification: NotificationItem) {
        // TODO: navegar para a tela de resolução da pendência
    }
}
