package com.zera.android.viewmodel.main.models

import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.MaterialResponseDTO
import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.view.components.outros.ItemStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelDetailsMappingTest {
    @Test
    fun managerCanReviewPendingModel() {
        val state = ModelDetailsViewModel.stateFrom(pendingModel(), isEmployee = false)
        assertTrue(state.canReview)
        assertTrue(state.canEdit)
        assertEquals(ItemStatus.PendingApproval, state.status)
    }

    @Test
    fun employeeCannotEditNorReview() {
        val state = ModelDetailsViewModel.stateFrom(pendingModel(), isEmployee = true)
        assertFalse(state.canReview)
        assertFalse(state.canEdit)
    }

    @Test
    fun onlyManagerCanDelete() {
        assertTrue(ModelDetailsViewModel.stateFrom(pendingModel(), isEmployee = false).canDelete)
        assertFalse(ModelDetailsViewModel.stateFrom(pendingModel(), isEmployee = true).canDelete)
    }

    @Test
    fun editChangesIgnoreUnchangedFieldsAndSurroundingSpaces() {
        val state = ModelDetailsState(name = "Latitude", manufacturer = "Dell")
        val draft = ModelEditDraft(name = " Latitude ", manufacturer = "Dell")

        assertFalse(ModelDetailsViewModel.editChangesFrom(state, draft).any)
        assertEquals(
            ModelEditChanges(name = false, manufacturer = true),
            ModelDetailsViewModel.editChangesFrom(state, draft.copy(manufacturer = "Lenovo")),
        )
    }

    @Test
    fun weightIsFormattedAndParsedWithCommaOrDot() {
        assertEquals("2,5", ModelDetailsViewModel.formatWeightKg(2.5))
        assertEquals("3", ModelDetailsViewModel.formatWeightKg(3.0))
        assertEquals("", ModelDetailsViewModel.formatWeightKg(null))
        assertEquals(2.5, ModelDetailsViewModel.parseWeightKg("2,5"))
        assertEquals(2.5, ModelDetailsViewModel.parseWeightKg(" 2.5 "))
        assertEquals(null, ModelDetailsViewModel.parseWeightKg("abc"))
    }

    @Test
    fun weightMustBePositive() {
        assertEquals(null, ModelDetailsViewModel.weightValidationError("2,5"))
        assertTrue(ModelDetailsViewModel.weightValidationError("0") != null)
        assertTrue(ModelDetailsViewModel.weightValidationError("-1") != null)
        assertTrue(ModelDetailsViewModel.weightValidationError("") != null)
    }

    @Test
    fun editChangesDetectWeight() {
        val state = ModelDetailsState(name = "Latitude", manufacturer = "Dell", weight = "2,5")
        val draft = ModelEditDraft(name = "Latitude", manufacturer = "Dell", weight = "2,5")

        assertFalse(ModelDetailsViewModel.editChangesFrom(state, draft).any)
        assertTrue(ModelDetailsViewModel.editChangesFrom(state, draft.copy(weight = "3")).weight)
    }

    @Test
    fun approvedModelHasNoReview() {
        val state = ModelDetailsViewModel.stateFrom(
            pendingModel().copy(approvalStatus = "APPROVED"),
            isEmployee = false,
        )
        assertFalse(state.canReview)
        assertEquals(ItemStatus.InStock, state.status)
    }

    @Test
    fun mapsFieldsFromResponse() {
        val state = ModelDetailsViewModel.stateFrom(pendingModel(), isEmployee = true)
        assertEquals("m1", state.modelId)
        assertEquals("Latitude 5420", state.name)
        assertEquals("Dell", state.manufacturer)
        assertEquals("Notebook", state.category)
        assertEquals("Plástico", state.material)
        assertEquals("tela frágil", state.notes)
        assertEquals("foto ruim", state.rejectionReason)
    }

    @Test
    fun reviewableItemIdPicksPendingApproval() {
        val items = listOf(
            ItemResponseDTO(id = "draft", name = "A", status = "DRAFT"),
            ItemResponseDTO(id = "pending", name = "B", status = "PENDING_APPROVAL"),
        )
        assertEquals("pending", ModelDetailsViewModel.reviewableItemId(items))
        assertNull(ModelDetailsViewModel.reviewableItemId(listOf(items.first())))
    }
}

private fun pendingModel() = ModelResponseDTO(
    id = "m1",
    name = "Latitude 5420",
    manufacturer = "Dell",
    materials = listOf(MaterialResponseDTO(code = "PLASTIC", name = "Plástico")),
    category = CategoryResponseDTO(id = "c1", name = "Notebook"),
    notes = "tela frágil",
    approvalStatus = "PENDING",
    rejectionReason = "foto ruim",
)
