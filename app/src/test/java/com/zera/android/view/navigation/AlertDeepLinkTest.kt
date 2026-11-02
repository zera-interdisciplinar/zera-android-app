package com.zera.android.view.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Robolectric fornece o android.net.Uri usado por routeForItemDeepLink.
@RunWith(RobolectricTestRunner::class)
class AlertDeepLinkTest {
    @Test
    fun mapsKnownKindsToExistingScreens() {
        assertEquals(Route.Itens(), routeForAlertKind("STOCK_QUANTITY_LIMIT"))
        assertEquals(Route.Itens(), routeForAlertKind("WARRANTY_EXPIRATION"))
        assertEquals(Route.Recycling, routeForAlertKind("RECYCLABLE_TO_LANDFILL"))
        assertEquals(Route.Itens(status = "Pendente"), routeForAlertKind("ITEM_PENDING_APPROVAL"))
    }

    @Test
    fun unknownOrUnmappedKindsHaveNoRoute() {
        assertNull(routeForAlertKind("ITEM_APPROVED"))
        assertNull(routeForAlertKind("ITEM_REJECTED"))
        assertNull(routeForAlertKind("STORAGE"))
        assertNull(routeForAlertKind("TIME"))
        assertNull(routeForAlertKind("UNKNOWN"))
        assertNull(routeForAlertKind(null))
    }

    @Test
    fun itemDeepLinkOpensItemDetails() {
        val itemId = "7c9e6679-7425-40de-944b-e07fc1f90ae7"
        assertEquals(Route.ItemDetails(itemId), routeForItemDeepLink("zera://item?id=$itemId"))
        assertNull(routeForItemDeepLink("zera://reset-password"))
        assertNull(routeForItemDeepLink(null))
    }

    @Test
    fun itemAlertsOpenTheItemFromTheEventId() {
        val itemId = "7c9e6679-7425-40de-944b-e07fc1f90ae7"
        listOf(
            "WARRANTY_EXPIRATION",
            "LIFESPAN_EXPIRATION",
            "USAGE_INTENSITY_LIMIT",
            "TIME_IN_STOCK_LIMIT",
            "STALE_ITEM",
            "PREDICTED_FAILURE",
            "ITEM_PENDING_APPROVAL",
        ).forEach { kind ->
            assertEquals(Route.ItemDetails(itemId), routeForAlert(kind, itemId))
        }
    }

    @Test
    fun occupationAlertNeverTreatsTheUnitAsAnItem() {
        assertEquals(Route.Itens(), routeForAlert("STOCK_QUANTITY_LIMIT", "unit-id"))
    }

    @Test
    fun alertWithoutEventIdFallsBackToTheKindRoute() {
        assertEquals(Route.Itens(), routeForAlert("WARRANTY_EXPIRATION", null))
        assertEquals(Route.Itens(), routeForAlert("STALE_ITEM", " "))
        assertEquals(Route.Itens(status = "Pendente"), routeForAlert("ITEM_PENDING_APPROVAL", null))
    }

    @Test
    fun otherKindsKeepTheirRouteEvenWithEventId() {
        assertEquals(Route.Recycling, routeForAlert("RECYCLABLE_TO_LANDFILL", "event-id"))
        assertNull(routeForAlert("ITEM_APPROVED", "event-id"))
        assertNull(routeForAlert("UNKNOWN", "event-id"))
    }

    @Test
    fun dismissesThePendingAlertOnlyOnce() {
        val dismissed = mutableListOf<String>()
        PendingAlertDeepLink.rememberAlertId("alert-1")

        dismissPendingAlert(canDismiss = { true }, dismiss = { dismissed += it })
        dismissPendingAlert(canDismiss = { true }, dismiss = { dismissed += it })

        assertEquals(listOf("alert-1"), dismissed)
    }

    @Test
    fun keepsThePendingAlertUntilItCanBeDismissed() {
        val dismissed = mutableListOf<String>()
        PendingAlertDeepLink.rememberAlertId("alert-1")

        dismissPendingAlert(canDismiss = { false }, dismiss = { dismissed += it })
        assertEquals(emptyList<String>(), dismissed)

        dismissPendingAlert(canDismiss = { true }, dismiss = { dismissed += it })
        assertEquals(listOf("alert-1"), dismissed)
    }

    @Test
    fun missingOrBlankAlertIdDoesNotReplaceThePendingOne() {
        val dismissed = mutableListOf<String>()
        PendingAlertDeepLink.rememberAlertId("alert-1")
        PendingAlertDeepLink.rememberAlertId(null)
        PendingAlertDeepLink.rememberAlertId(" ")

        dismissPendingAlert(canDismiss = { true }, dismiss = { dismissed += it })

        assertEquals(listOf("alert-1"), dismissed)
    }

    @Test
    fun nothingPendingDismissesNothing() {
        PendingAlertDeepLink.takeAlertId()
        val dismissed = mutableListOf<String>()

        dismissPendingAlert(canDismiss = { true }, dismiss = { dismissed += it })

        assertEquals(emptyList<String>(), dismissed)
    }
}
