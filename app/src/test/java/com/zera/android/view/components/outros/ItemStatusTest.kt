package com.zera.android.view.components.outros

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.zera.android.view.components.setZeraContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ItemStatusTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun tagShowsTheLabelOfEveryStatus() {
        composeRule.setZeraContent {
            Column {
                ItemStatus.entries.forEach { status -> ItemStatusTag(status = status) }
            }
        }

        ItemStatus.entries.forEach { status ->
            composeRule.onNodeWithText(status.label).assertIsDisplayed()
        }
    }

    @Test
    fun mapsEveryBackendStatus() {
        assertEquals(ItemStatus.Draft, ItemStatus.fromBackend("DRAFT"))
        assertEquals(ItemStatus.PendingApproval, ItemStatus.fromBackend("PENDING_APPROVAL"))
        assertEquals(ItemStatus.Rejected, ItemStatus.fromBackend("REJECTED"))
        assertEquals(ItemStatus.InStock, ItemStatus.fromBackend("IN_STOCK"))
        assertEquals(ItemStatus.InMaintenance, ItemStatus.fromBackend("IN_MAINTENANCE"))
        assertEquals(ItemStatus.AwaitingEvaluation, ItemStatus.fromBackend("AWAITING_EVALUATION"))
        assertEquals(ItemStatus.Disposed, ItemStatus.fromBackend("DISPOSED"))
    }

    @Test
    fun acceptsTheBackendAliases() {
        assertEquals(ItemStatus.PendingApproval, ItemStatus.fromBackend("PENDING"))
        assertEquals(ItemStatus.InStock, ItemStatus.fromBackend("APPROVED"))
    }

    @Test
    fun normalizesCaseAndSpaces() {
        assertEquals(ItemStatus.InStock, ItemStatus.fromBackend("in_stock"))
        assertEquals(ItemStatus.InStock, ItemStatus.fromBackend("in stock"))
        assertEquals(ItemStatus.PendingApproval, ItemStatus.fromBackend(" pending_approval "))
    }

    @Test
    fun unknownStatusHasNoChip() {
        assertNull(ItemStatus.fromBackend("REMOVED"))
        assertNull(ItemStatus.fromBackend("UNKNOWN"))
        assertNull(ItemStatus.fromBackend(""))
        assertNull(ItemStatus.fromBackend(null))
    }
}
