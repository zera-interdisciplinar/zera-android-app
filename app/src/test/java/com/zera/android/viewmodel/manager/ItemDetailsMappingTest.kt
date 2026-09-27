package com.zera.android.viewmodel.manager

import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.MaterialResponseDTO
import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.view.components.outros.ItemStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ItemDetailsMappingTest {
    @Test
    fun mapsBackendStatusToChip() {
        assertEquals(ItemStatus.PendingApproval, ItemDetailsViewModel.statusFrom("PENDING_APPROVAL"))
        assertEquals(ItemStatus.Rejected, ItemDetailsViewModel.statusFrom("REJECTED"))
        assertEquals(ItemStatus.InStock, ItemDetailsViewModel.statusFrom("IN_STOCK"))
        assertEquals(ItemStatus.InMaintenance, ItemDetailsViewModel.statusFrom("IN_MAINTENANCE"))
        assertEquals(ItemStatus.AwaitingEvaluation, ItemDetailsViewModel.statusFrom("AWAITING_EVALUATION"))
        assertEquals(ItemStatus.Disposed, ItemDetailsViewModel.statusFrom("DISPOSED"))
        assertEquals(ItemStatus.InStock, ItemDetailsViewModel.statusFrom("in_stock"))
        assertEquals(ItemStatus.InStock, ItemDetailsViewModel.statusFrom("APPROVED"))
        assertEquals(ItemStatus.PendingApproval, ItemDetailsViewModel.statusFrom(" pending_approval "))
        assertEquals(ItemStatus.PendingApproval, ItemDetailsViewModel.statusFrom("PENDING"))
        assertNull(ItemDetailsViewModel.statusFrom("DRAFT"))
        assertNull(ItemDetailsViewModel.statusFrom(null))
    }

    @Test
    fun usedConditionDoesNotMakeItemPending() {
        val state = ItemDetailsViewModel.stateFrom(
            ItemResponseDTO(
                id = "id-1",
                name = "Notebook",
                status = "APPROVED",
                condition = "USED",
            ),
        )

        assertEquals("Usado", state.condition)
        assertEquals("USED", state.conditionCode)
        assertEquals(ItemStatus.InStock, state.status)
    }

    @Test
    fun missingStatusDoesNotFallBackToPending() {
        val state = ItemDetailsViewModel.stateFrom(
            ItemResponseDTO(
                id = "id-1",
                name = "Notebook",
                condition = "USED",
            ),
        )

        assertEquals("Usado", state.condition)
        assertNull(state.status)
    }

    @Test
    fun mapsConditionToUiLabel() {
        assertEquals("Novo", ItemDetailsViewModel.conditionLabel("NEW"))
        assertEquals("Usado", ItemDetailsViewModel.conditionLabel("USED"))
        assertEquals("Semidanificado", ItemDetailsViewModel.conditionLabel("SEMI_DAMAGED"))
        assertEquals("Danificado", ItemDetailsViewModel.conditionLabel("DAMAGED"))
        assertEquals("", ItemDetailsViewModel.conditionLabel(null))
    }

    @Test
    fun joinsMaterialNames() {
        assertEquals("", ItemDetailsViewModel.materialsLabel(emptyList()))
        assertEquals("Plastic", ItemDetailsViewModel.materialsLabel(listOf("Plastic")))
        assertEquals("Metal e Plástico", ItemDetailsViewModel.materialsLabel(listOf("Metal", "Plástico")))
        assertEquals(
            "Metal, Plástico e Vidro",
            ItemDetailsViewModel.materialsLabel(listOf("Metal", "Plástico", "Vidro")),
        )
    }

    @Test
    fun formatsRegistrationDateLikeTheScreen() {
        assertEquals(
            "1 de Jul 2026 - 9h00",
            ItemDetailsViewModel.formatRegisteredAt("2026-07-01T09:00:00"),
        )
        assertEquals("", ItemDetailsViewModel.formatRegisteredAt(null))
    }

    @Test
    fun mapsItemResponseToDetailsState() {
        val item = ItemResponseDTO(
            id = "9c858901-8a57-4791-81fe-4c455b099bc9",
            name = "Notebook Dell Latitude",
            status = "IN_STOCK",
            condition = "USED",
            serialNumber = "SN123456",
            notes = "Foto ok",
            createdByName = "João Silva",
            createdAt = "2026-07-01T09:00:00",
            model = ModelResponseDTO(
                name = "Latitude 5420",
                materials = listOf(MaterialResponseDTO(id = "m1", name = "Plastic")),
                category = CategoryResponseDTO(id = "c1", name = "Informática"),
            ),
        )

        val state = ItemDetailsViewModel.stateFrom(item)

        assertEquals("9c858901-8a57-4791-81fe-4c455b099bc9", state.itemId)
        assertEquals("Notebook Dell Latitude", state.itemName)
        assertEquals("Latitude 5420", state.itemSubtitle)
        assertEquals(ItemStatus.InStock, state.status)
        assertEquals("Informática", state.category)
        assertEquals("Plastic", state.material)
        assertEquals("Usado", state.condition)
        assertEquals("USED", state.conditionCode)
        assertEquals("SN123456", state.serialNumber)
        assertEquals("Foto ok", state.notes)
        assertEquals(false, state.canReview)
        assertEquals("João Silva", state.registeredBy)
        assertEquals("1 de Jul 2026 - 9h00", state.registeredAt)
    }

    @Test
    fun mapsConditionLabelBackToBackendCode() {
        assertEquals("NEW", ItemDetailsViewModel.conditionCodeFrom("Novo"))
        assertEquals("USED", ItemDetailsViewModel.conditionCodeFrom("usado"))
        assertEquals("SEMI_DAMAGED", ItemDetailsViewModel.conditionCodeFrom("Semidanificado"))
        assertEquals("DAMAGED", ItemDetailsViewModel.conditionCodeFrom("DAMAGED"))
        assertEquals(null, ItemDetailsViewModel.conditionCodeFrom("quebrado"))
    }

    @Test
    fun validatesNameLengthFromContract() {
        assertEquals("Informe o nome do item.", ItemDetailsViewModel.nameValidationError("  "))
        assertEquals(null, ItemDetailsViewModel.nameValidationError("Notebook"))
        assertEquals(
            "O nome deve ter no máximo 120 caracteres.",
            ItemDetailsViewModel.nameValidationError("a".repeat(121)),
        )
    }

    @Test
    fun validatesRejectReasonFromContract() {
        assertEquals("Informe o motivo da recusa.", ItemDetailsViewModel.reasonValidationError(""))
        assertEquals(null, ItemDetailsViewModel.reasonValidationError("Foto ilegível, reenviar"))
        assertEquals(
            "O motivo deve ter no máximo 500 caracteres.",
            ItemDetailsViewModel.reasonValidationError("a".repeat(501)),
        )
    }

    @Test
    fun reviewActionsOnlyForPendingStatuses() {
        assertEquals(true, ItemDetailsViewModel.canReview(ItemStatus.PendingApproval))
        assertEquals(true, ItemDetailsViewModel.canReview(ItemStatus.AwaitingEvaluation))
        assertEquals(false, ItemDetailsViewModel.canReview(ItemStatus.InStock))
        assertEquals(false, ItemDetailsViewModel.canReview(ItemStatus.Rejected))
        assertEquals(false, ItemDetailsViewModel.canReview(null))
    }

    @Test
    fun usedConditionDoesNotEnableReview() {
        val state = ItemDetailsViewModel.stateFrom(
            ItemResponseDTO(
                id = "id-1",
                name = "Notebook",
                status = "IN_STOCK",
                condition = "USED",
            ),
        )

        assertEquals("Usado", state.condition)
        assertEquals(false, state.canReview)
    }
}
