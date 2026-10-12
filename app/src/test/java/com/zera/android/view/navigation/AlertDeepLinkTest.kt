package com.zera.android.view.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlertDeepLinkTest {
    @Test
    fun mapsKnownKindsToExistingScreens() {
        assertEquals(Route.Itens(), routeForAlertKind("STOCK_QUANTITY_LIMIT"))
        assertEquals(Route.Recycling, routeForAlertKind("RECYCLABLE_TO_LANDFILL"))
    }

    @Test
    fun unknownOrUnmappedKindsHaveNoRoute() {
        assertNull(routeForAlertKind("ITEM_APPROVED"))
        assertNull(routeForAlertKind("STORAGE"))
        assertNull(routeForAlertKind(null))
    }
}
