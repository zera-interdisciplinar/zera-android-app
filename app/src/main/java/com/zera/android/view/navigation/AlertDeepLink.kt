package com.zera.android.view.navigation

import android.content.Intent
import com.zera.android.model.entity.notification.AlertKind

object PendingAlertDeepLink {
    const val ExtraAlertId = "alertId"
    const val ExtraKind = "kind"
    const val ExtraSeverity = "severity"

    @Volatile
    private var kind: String? = null

    fun stash(intent: Intent?) {
        val nextKind = intent?.getStringExtra(ExtraKind)?.takeIf { it.isNotBlank() } ?: return
        kind = nextKind
    }

    fun takeKind(): String? {
        val current = kind
        kind = null
        return current
    }
}

fun routeForAlertKind(kind: String?): Route? = when (AlertKind.from(kind.orEmpty())) {
    AlertKind.STOCK_QUANTITY_LIMIT,
    AlertKind.TIME_IN_STOCK_LIMIT,
    AlertKind.STALE_ITEM,
    AlertKind.WARRANTY_EXPIRATION,
    AlertKind.LIFESPAN_EXPIRATION,
    AlertKind.USAGE_INTENSITY_LIMIT,
    AlertKind.PREDICTED_FAILURE,
    -> Route.Itens()
    AlertKind.RECYCLABLE_TO_LANDFILL -> Route.Recycling
    else -> null
}

fun consumePendingAlertDeepLink() {
    val route = routeForAlertKind(PendingAlertDeepLink.takeKind()) ?: return
    ZeraNavigator.push(route)
}
