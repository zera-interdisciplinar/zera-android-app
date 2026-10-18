package com.zera.android.viewmodel.main.alerts

import com.zera.android.model.dto.notification.AlertResponseDTO
import com.zera.android.model.entity.notification.AlertKind
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.navigation.routeForAlertKind
import com.zera.android.view.theme.ZeraColorFamily

/** Converte os alertas da API nos cards de notificação, para a home do gestor e a Central de Alertas. */
object AlertMapping {
    fun notificationsFrom(alerts: List<AlertResponseDTO>): List<NotificationItem> =
        alerts.map { alert ->
            NotificationItem(
                id = alert.alertId,
                label = alert.description,
                style = colorFamilyForAlert(alert.kind, alert.severity),
                kind = alert.kind,
                resolvable = routeForAlertKind(alert.kind) != null,
            )
        }

    internal fun colorFamilyForAlert(kind: String?, severity: String): ZeraColorFamily =
        when (AlertKind.from(kind.orEmpty())) {
            AlertKind.ITEM_APPROVED -> ZeraColorFamily.Green
            AlertKind.ITEM_PENDING_APPROVAL,
            AlertKind.WARRANTY_EXPIRATION,
            AlertKind.LIFESPAN_EXPIRATION,
            AlertKind.STORAGE,
            AlertKind.TIME,
            -> ZeraColorFamily.Yellow
            AlertKind.STOCK_QUANTITY_LIMIT,
            AlertKind.TIME_IN_STOCK_LIMIT,
            AlertKind.STALE_ITEM,
            -> ZeraColorFamily.Blue
            AlertKind.ITEM_REJECTED,
            AlertKind.RECYCLABLE_TO_LANDFILL,
            AlertKind.USAGE_INTENSITY_LIMIT,
            AlertKind.PREDICTED_FAILURE,
            -> ZeraColorFamily.Red
            null -> colorFamilyForSeverity(severity)
        }

    internal fun colorFamilyForSeverity(severity: String): ZeraColorFamily = when (severity) {
        "HIGH" -> ZeraColorFamily.Red
        "MEDIUM" -> ZeraColorFamily.Yellow
        "LOW" -> ZeraColorFamily.Green
        else -> ZeraColorFamily.Yellow
    }
}
