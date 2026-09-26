package com.zera.android.viewmodel.manager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class IndexesMappingTest {
    @Test
    fun thisMonthUsesFirstDayAndToday() {
        val today = LocalDate.of(2026, 9, 26)
        val (from, to) = IndexesViewModel.periodFor(IndexesState.FILTER_THIS_MONTH, today)
        assertEquals("2026-09-01", from)
        assertEquals("2026-09-26", to)
    }

    @Test
    fun allAndCategoryOmitPeriod() {
        val today = LocalDate.of(2026, 9, 26)
        assertEquals(null to null, IndexesViewModel.periodFor(IndexesState.FILTER_ALL, today))
        assertEquals(null to null, IndexesViewModel.periodFor(IndexesState.FILTER_CATEGORY, today))
    }

    @Test
    fun monthLabelUsesShortPortuguese() {
        assertEquals("Jan", IndexesViewModel.monthLabel("2026-01"))
        assertEquals("Set", IndexesViewModel.monthLabel("2026-09"))
    }

    @Test
    fun materialLabelMapsKnownCodes() {
        assertEquals("Plásticos", IndexesViewModel.materialLabel("PLASTIC"))
        assertEquals("Placas", IndexesViewModel.materialLabel("CIRCUIT_BOARD"))
        assertEquals("CUSTOM", IndexesViewModel.materialLabel("CUSTOM"))
    }

    @Test
    fun deltaPointsStayNullWhenMissing() {
        assertNull(IndexesViewModel.formatDeltaPoints(null))
        assertTrue(IndexesViewModel.formatDeltaPoints(5.2)!!.startsWith("↑"))
        assertTrue(IndexesViewModel.formatDeltaPoints(-1.0)!!.startsWith("↓"))
    }
}
