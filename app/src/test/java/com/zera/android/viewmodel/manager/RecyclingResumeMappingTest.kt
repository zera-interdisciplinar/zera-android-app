package com.zera.android.viewmodel.manager

import com.zera.android.view.components.cards.OpeningHoursItem
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class RecyclingResumeMappingTest {
    @Test
    fun openingHoursPairsDaysWithLabels() {
        assertEquals(
            listOf(OpeningHoursItem(days = "segunda-feira", hours = "08:00 – 18:00")),
            RecyclingResumeViewModel.openingHours(listOf("segunda-feira"), listOf("08:00 – 18:00")),
        )
        assertEquals(emptyList<OpeningHoursItem>(), RecyclingResumeViewModel.openingHours(emptyList(), emptyList()))
    }

    @Test
    fun materialsSummaryJoinsUpToThreeNames() {
        assertEquals("Notebook, Mouse", RecyclingResumeViewModel.materialsSummary(listOf("Notebook", "Mouse")))
    }

    @Test
    fun materialsSummaryTruncatesLongLists() {
        assertEquals(
            "Notebook, Mouse e mais 2",
            RecyclingResumeViewModel.materialsSummary(listOf("Notebook", "Mouse", "Teclado", "Monitor")),
        )
    }

    @Test
    fun formatScheduledAtUsesPortugueseMonth() {
        val formatted = RecyclingResumeViewModel.formatScheduledAt(
            LocalDateTime.of(2026, 8, 21, 8, 0),
        )
        assertEquals("21 de agosto · 08:00", formatted)
    }

    @Test
    fun confirmErrorKeepsUseCaseMessage() {
        assertEquals(
            "Selecione ao menos um item para registrar o descarte.",
            RecyclingResumeViewModel.confirmError(
                IllegalArgumentException("Selecione ao menos um item para registrar o descarte."),
            ),
        )
    }

    @Test
    fun confirmErrorFallsBackWhenMessageIsBlank() {
        assertEquals(
            "Não foi possível registrar o descarte.",
            RecyclingResumeViewModel.confirmError(IllegalStateException("   ")),
        )
    }
}
