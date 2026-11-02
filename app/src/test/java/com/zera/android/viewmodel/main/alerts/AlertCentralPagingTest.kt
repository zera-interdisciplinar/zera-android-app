package com.zera.android.viewmodel.main.alerts

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

    @Test
    fun pageFollowsTheVisibleCount() {
        val pageSize = AlertCentralViewModel.PageSize
        assertEquals(0, AlertCentralViewModel.pageFor(0))
        assertEquals(1, AlertCentralViewModel.pageFor(pageSize))
        assertEquals(2, AlertCentralViewModel.pageFor(pageSize * 2))
    }

    @Test
    fun dismissingGoesBackAPageSoNoAlertIsSkipped() {
        val pageSize = AlertCentralViewModel.PageSize
        assertEquals(0, AlertCentralViewModel.pageFor(pageSize - 5))
        assertEquals(1, AlertCentralViewModel.pageFor(pageSize * 2 - 1))
    }

    private fun item(id: String) = NotificationItem(id = id, label = id)
}
