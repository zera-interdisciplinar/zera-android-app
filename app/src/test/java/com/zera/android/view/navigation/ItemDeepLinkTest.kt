package com.zera.android.view.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ItemDeepLinkTest {

    @Test
    fun readsTheItemId() {
        assertEquals("abc-123", itemIdFromDeepLink("zera", "item", "abc-123"))
    }

    @Test
    fun trimsTheItemId() {
        assertEquals("abc-123", itemIdFromDeepLink("zera", "item", "  abc-123 "))
    }

    @Test
    fun ignoresLinksWithoutId() {
        assertNull(itemIdFromDeepLink("zera", "item", null))
        assertNull(itemIdFromDeepLink("zera", "item", "   "))
    }

    @Test
    fun ignoresOtherLinks() {
        assertNull(itemIdFromDeepLink("zera", "reset-password", "abc"))
        assertNull(itemIdFromDeepLink("https", "item", "abc"))
        assertNull(itemIdFromDeepLink(null, null, "abc"))
    }
}
