package com.zera.android.viewmodel.manager

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
        assertFalse(state.canEdit)
        assertEquals(ItemStatus.PendingApproval, state.status)
    }

    @Test
    fun employeeCanEditAndCannotReview() {
        val state = ModelDetailsViewModel.stateFrom(pendingModel(), isEmployee = true)
        assertFalse(state.canReview)
        assertTrue(state.canEdit)
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
