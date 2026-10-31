package com.zera.android.viewmodel.main.items

import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.MaterialResponseDTO
import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.model.dto.inventory.UpdateItemRequestDTO
import com.zera.android.view.components.navigation.EmployeeSideBarItems
import com.zera.android.view.components.navigation.ManagerSideBarItems
import com.zera.android.view.components.outros.ItemStatus
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ItemDetailsMappingTest {
    @Test
    fun mapsBackendStatusToChip() {
        assertEquals(ItemStatus.Draft, ItemDetailsViewModel.statusFrom("DRAFT"))
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
    fun formatsAcquisitionDateWithoutTime() {
        assertEquals("1 de Jul 2026", ItemDetailsViewModel.formatAcquiredAt("2026-07-01"))
        assertEquals("", ItemDetailsViewModel.formatAcquiredAt(null))
    }

    @Test
    fun mapsItemResponseToDetailsState() {
        val item = ItemResponseDTO(
            id = "9c858901-8a57-4791-81fe-4c455b099bc9",
            name = "Notebook Dell Latitude",
            status = "IN_STOCK",
            condition = "USED",
            serialNumber = "SN123456",
            acquiredAt = "2026-07-01",
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
        assertEquals("1 de Jul 2026", state.acquiredAt)
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
        assertEquals(false, ItemDetailsViewModel.canReview(ItemStatus.Draft))
        assertEquals(false, ItemDetailsViewModel.canReview(null))
        assertEquals(false, ItemDetailsViewModel.canReview(ItemStatus.PendingApproval, isEmployee = true))
        assertEquals(false, ItemDetailsViewModel.canReview(ItemStatus.AwaitingEvaluation, isEmployee = true))
    }

    @Test
    fun maintenanceActionOnlyForEmployeeOnInStockItem() {
        assertEquals(true, ItemDetailsViewModel.canSendToMaintenance(ItemStatus.InStock, isEmployee = true))
        ItemStatus.entries.filter { it != ItemStatus.InStock }.forEach { status ->
            assertEquals(false, ItemDetailsViewModel.canSendToMaintenance(status, isEmployee = true))
        }
        assertEquals(false, ItemDetailsViewModel.canSendToMaintenance(null, isEmployee = true))
        assertEquals(false, ItemDetailsViewModel.canSendToMaintenance(ItemStatus.InStock, isEmployee = false))
    }

    @Test
    fun sideBarFollowsUserRole() {
        val item = ItemResponseDTO(id = "id-1", name = "Notebook", status = "IN_STOCK")
        assertEquals(EmployeeSideBarItems, ItemDetailsViewModel.stateFrom(item, isEmployee = true).sideBarItems)
        assertEquals(ManagerSideBarItems, ItemDetailsViewModel.stateFrom(item, isEmployee = false).sideBarItems)
    }

    @Test
    fun inStockItemEnablesMaintenanceOnlyForEmployee() {
        val item = ItemResponseDTO(id = "id-1", name = "Notebook", status = "IN_STOCK")
        assertEquals(true, ItemDetailsViewModel.stateFrom(item, isEmployee = true).canSendToMaintenance)
        assertEquals(false, ItemDetailsViewModel.stateFrom(item, isEmployee = false).canSendToMaintenance)
    }

    @Test
    fun maintenanceStatusDisablesMaintenanceAction() {
        val state = ItemDetailsViewModel.stateFrom(
            ItemResponseDTO(id = "id-1", name = "Notebook", status = "IN_MAINTENANCE"),
        )
        assertEquals(false, state.canSendToMaintenance)
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

    @Test
    fun employeeDoesNotGetReviewOnPendingItem() {
        val state = ItemDetailsViewModel.stateFrom(
            ItemResponseDTO(id = "id-1", name = "Notebook", status = "PENDING_APPROVAL"),
            isEmployee = true,
        )
        assertEquals(false, state.canReview)
    }

    @Test
    fun readsRegisteredByFromNestedCreatedBy() {
        val json = Json { ignoreUnknownKeys = true }
        val item = json.decodeFromString<ItemResponseDTO>(
            """{"id":"id-1","name":"Notebook","createdBy":{"id":"u1","name":"Ana Lima"}}""",
        )
        assertEquals("Ana Lima", ItemDetailsViewModel.registeredByFrom(item))
        assertEquals("Ana Lima", ItemDetailsViewModel.stateFrom(item).registeredBy)
    }

    @Test
    fun ignoresCreatedByUuidWithoutName() {
        val json = Json { ignoreUnknownKeys = true }
        val item = json.decodeFromString<ItemResponseDTO>(
            """{"id":"id-1","name":"Notebook","createdBy":"user-uuid"}""",
        )
        assertEquals("", ItemDetailsViewModel.registeredByFrom(item))
    }

    @Test
    fun editDraftStartsFromLoadedItem() {
        val item = ItemResponseDTO(
            id = "id-1",
            name = "Placa de vídeo",
            condition = "SEMI_DAMAGED",
            hasDamages = true,
            damages = listOf("OXIDATION", "MISSING_PART"),
            usageIntensity = 4,
            notes = "Arranhão",
        )
        val draft = ItemDetailsViewModel.draftFrom(ItemDetailsViewModel.stateFrom(item))
        assertEquals(
            ItemEditDraft(
                name = "Placa de vídeo",
                condition = "Semidanificado",
                usageIntensity = 4,
                hasDamage = true,
                damageType = "Enferrujado",
                notes = "Arranhão",
            ),
            draft,
        )
    }

    @Test
    fun editRequestIsNullWhenNothingChanged() {
        val initial = ItemEditDraft(name = "Notebook", condition = "Usado", usageIntensity = 3)
        assertNull(ItemDetailsViewModel.editRequestFrom(initial, initial.copy(name = " Notebook ")))
    }

    @Test
    fun editRequestSendsOnlyChangedFields() {
        val initial = ItemEditDraft(name = "Notebook", condition = "Usado", usageIntensity = 3, notes = "Velho")
        val draft = initial.copy(condition = "Danificado", usageIntensity = 7, notes = "")
        assertEquals(
            UpdateItemRequestDTO(condition = "DAMAGED", usageIntensity = 7, notes = ""),
            ItemDetailsViewModel.editRequestFrom(initial, draft),
        )
    }

    @Test
    fun editRequestSendsDamagesOnlyWhenTouched() {
        val initial = ItemEditDraft(name = "Notebook", hasDamage = true, damageType = "Não liga")
        assertEquals(
            UpdateItemRequestDTO(hasDamages = true, damages = listOf("BROKEN_SCREEN")),
            ItemDetailsViewModel.editRequestFrom(initial, initial.copy(damageType = "Tela quebrada")),
        )
        assertEquals(
            UpdateItemRequestDTO(hasDamages = false, damages = emptyList()),
            ItemDetailsViewModel.editRequestFrom(initial, initial.copy(hasDamage = false, damageType = "")),
        )
    }
}
