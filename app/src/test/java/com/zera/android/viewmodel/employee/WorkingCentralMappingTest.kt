package com.zera.android.viewmodel.employee

import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.PendingItemResponseDTO
import com.zera.android.model.dto.inventory.WorkCenterResponseDTO
import com.zera.android.view.theme.ZeraColorFamily
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkingCentralMappingTest {
    @Test
    fun emptyWorkCenterProducesNoCards() {
        assertEquals(0, WorkingCentralViewModel.priorityFrom(WorkCenterResponseDTO()).size)
    }

    @Test
    fun mapsEachItemInBucketOrder() {
        val workCenter = WorkCenterResponseDTO(
            drafts = listOf(
                PendingItemResponseDTO(
                    id = "d1",
                    name = "Teclado mecânico",
                    missingFields = listOf("damages"),
                ),
            ),
            rejected = listOf(
                PendingItemResponseDTO(
                    id = "r1",
                    name = "Chip controlador",
                    rejectionReason = "Foto ilegível",
                ),
            ),
            damagedWithoutDestination = listOf(
                PendingItemResponseDTO(id = "x1", name = "Mouse sem fio"),
            ),
            inMaintenance = 25,
        )

        val cards = WorkingCentralViewModel.priorityFrom(workCenter)
        assertEquals(listOf("d1", "r1", "x1"), cards.map { it.id })
        assertEquals("Completar cadastro", cards[0].label)
        assertEquals("Teclado mecânico · faltam tipo de dano", cards[0].text)
        assertEquals(ZeraColorFamily.Yellow, cards[0].style)
        assertEquals("Recusado pelo gestor", cards[1].label)
        assertEquals("Chip controlador · Foto ilegível", cards[1].text)
        assertEquals(ZeraColorFamily.Red, cards[1].style)
        assertEquals("Enviar para manutenção", cards[2].label)
        assertEquals("Mouse sem fio · aguardando destino", cards[2].text)
        assertEquals(ZeraColorFamily.Blue, cards[2].style)
        assertTrue(cards[0].resolvable)
        assertTrue(cards[1].resolvable)
        assertFalse(cards[2].resolvable)
    }

    @Test
    fun draftWithoutMissingFieldsUsesNameOnly() {
        val workCenter = WorkCenterResponseDTO(
            drafts = listOf(PendingItemResponseDTO(id = "d1", name = "Monitor")),
        )
        val card = WorkingCentralViewModel.priorityFrom(workCenter).single()
        assertEquals("Monitor", card.text)
    }

    @Test
    fun draftMissingFieldsUsePortugueseLabels() {
        val workCenter = WorkCenterResponseDTO(
            drafts = listOf(
                PendingItemResponseDTO(
                    id = "d1",
                    name = "Monitor",
                    missingFields = listOf("photoUrl", "serialNumber"),
                ),
            ),
        )
        val card = WorkingCentralViewModel.priorityFrom(workCenter).single()
        assertEquals("Monitor · faltam foto, número de série", card.text)
    }

    @Test
    fun derivesMissingFieldsWhenItemPayloadOmitsTheList() {
        val item = ItemResponseDTO(
            id = "d1",
            name = "Monitor",
            serialNumber = "SN1",
            condition = "USED",
            photoUrl = null,
        )
        assertEquals(listOf("photoUrl"), WorkingCentralViewModel.missingFieldsFrom(item))
        assertEquals(
            "Monitor · faltam foto",
            WorkingCentralViewModel.draftText(
                PendingItemResponseDTO(id = "d1", name = "Monitor", missingFields = listOf("PHOTO_URL")),
            ),
        )
    }

    @Test
    fun shiftSummaryUsesPendingListAndMaintenanceCounter() {
        val state = WorkingCentralState(
            maintenanceCount = 2,
            priorityNotifications = WorkingCentralViewModel.priorityFrom(
                WorkCenterResponseDTO(
                    drafts = listOf(PendingItemResponseDTO(id = "d1", name = "A")),
                    rejected = listOf(PendingItemResponseDTO(id = "r1", name = "B")),
                ),
            ),
        )
        assertEquals(2, state.pendingCount)
        assertEquals("2 pendências · 2 em manutenção", state.shiftSummary)
    }
}
