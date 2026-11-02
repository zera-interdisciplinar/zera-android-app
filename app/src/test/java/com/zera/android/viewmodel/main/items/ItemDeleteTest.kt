package com.zera.android.viewmodel.main.items

import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.view.components.outros.ItemStatus
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ItemDeleteTest {
    @Test
    fun managerCanDeleteAnyActiveItem() {
        ItemStatus.entries.filter { it != ItemStatus.Disposed }.forEach { status ->
            assertTrue(ItemDetailsViewModel.canDelete(status, isEmployee = false))
        }
    }

    @Test
    fun employeeNeverSeesTheDeleteButton() {
        ItemStatus.entries.forEach { status ->
            assertFalse(ItemDetailsViewModel.canDelete(status, isEmployee = true))
        }
    }

    @Test
    fun disposedOrUnknownStatusCannotBeDeleted() {
        assertFalse(ItemDetailsViewModel.canDelete(ItemStatus.Disposed))
        assertFalse(ItemDetailsViewModel.canDelete(null))
    }

    @Test
    fun stateFromAppliesTheRule() {
        val item = ItemResponseDTO(id = "item-1", name = "Notebook", status = "IN_STOCK")

        assertTrue(ItemDetailsViewModel.stateFrom(item, isEmployee = false).canDelete)
        assertFalse(ItemDetailsViewModel.stateFrom(item, isEmployee = true).canDelete)
    }

    @Test
    fun conflictExplainsThatTheStatusBlocksTheDeletion() {
        val error = HttpException(Response.error<Unit>(409, "".toResponseBody()))

        assertEquals(
            "Não pode.",
            ItemDetailsViewModel.userFacingError(
                error = error,
                conflictMessage = "Não pode.",
                validationMessage = "Inválido.",
                fallback = "Falhou.",
            ),
        )
    }
}
