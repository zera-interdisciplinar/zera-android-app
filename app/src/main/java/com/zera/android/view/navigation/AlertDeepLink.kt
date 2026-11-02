package com.zera.android.view.navigation

import android.content.Intent
import android.net.Uri
import com.zera.android.model.entity.notification.AlertKind
import com.zera.android.view.components.outros.ItemStatus

object PendingAlertDeepLink {
    const val ExtraAlertId = "alertId"
    const val ExtraKind = "kind"
    const val ExtraSeverity = "severity"
    const val ExtraDeepLink = "deepLink"

    @Volatile
    private var kind: String? = null

    @Volatile
    private var deepLink: String? = null

    fun stash(intent: Intent?) {
        val nextLink = intent?.getStringExtra(ExtraDeepLink)?.takeIf { it.isNotBlank() }
        if (nextLink != null) {
            deepLink = nextLink
        }
        val nextKind = intent?.getStringExtra(ExtraKind)?.takeIf { it.isNotBlank() } ?: return
        kind = nextKind
    }

    fun takeKind(): String? {
        val current = kind
        kind = null
        return current
    }

    fun takeDeepLink(): String? {
        val current = deepLink
        deepLink = null
        return current
    }
}

fun routeForItemDeepLink(link: String?): Route? {
    val uri = link?.let(Uri::parse) ?: return null
    if (uri.scheme != "zera" || uri.host != "item") {
        return null
    }
    val itemId = uri.getQueryParameter("id")?.takeIf { it.isNotBlank() } ?: return null
    return Route.ItemDetails(itemId)
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
    AlertKind.ITEM_PENDING_APPROVAL -> Route.Itens(status = ItemStatus.PendingApproval.label)
    else -> null
}

fun consumePendingAlertDeepLink() {
    val link = PendingAlertDeepLink.takeDeepLink()
    val kind = PendingAlertDeepLink.takeKind()
    val route = routeForItemDeepLink(link) ?: routeForAlertKind(kind) ?: return
    ZeraNavigator.push(route)
}
