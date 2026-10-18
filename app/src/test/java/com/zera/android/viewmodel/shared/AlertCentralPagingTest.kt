package com.zera.android.viewmodel.shared

import com.zera.android.view.components.lists.NotificationItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertCentralPagingTest {
    @Test
    fun fullPageMeansThereMayBeMore() {
        assertTrue(AlertCentralViewModel.hasMoreAfter(AlertCentralViewModel.PageSize))
    }

    @Test
    fun partialOrEmptyPageIsTheLast() {
        assertFalse(AlertCentralViewModel.hasMoreAfter(AlertCentralViewModel.PageSize - 1))
        assertFalse(AlertCentralViewModel.hasMoreAfter(0))
    }

    @Test
    fun appendPageKeepsOrderAndDropsRepeatedIds() {
        val merged = AlertCentralViewModel.appendPage(
            current = listOf(item("a"), item("b")),
            page = listOf(item("b"), item("c")),
        )
        assertEquals(listOf("a", "b", "c"), merged.map { it.id })
    }

    private fun item(id: String) = NotificationItem(id = id, label = id)
}
