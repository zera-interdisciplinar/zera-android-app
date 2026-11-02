package com.zera.android.viewmodel.main.categories

import com.zera.android.model.dto.inventory.CategoryResponseDTO
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

class CategoryDetailsMappingTest {
    private val category = CategoryResponseDTO(
        id = "c1",
        name = "Informática",
        description = "Notebooks e monitores",
        createdAt = "2026-10-12T09:05:00",
    )

    @Test
    fun mapsCategoryToState() {
        val state = CategoryDetailsViewModel.stateFrom(category, isEmployee = false)

        assertEquals("c1", state.categoryId)
        assertEquals("Informática", state.name)
        assertEquals("Notebooks e monitores", state.description)
        assertTrue(state.registeredAt.contains("2026"))
    }

    @Test
    fun missingDescriptionBecomesEmpty() {
        val state = CategoryDetailsViewModel.stateFrom(category.copy(description = null), isEmployee = false)

        assertEquals("", state.description)
    }

    @Test
    fun onlyManagerCanEdit() {
        assertTrue(CategoryDetailsViewModel.stateFrom(category, isEmployee = false).canEdit)
        assertFalse(CategoryDetailsViewModel.stateFrom(category, isEmployee = true).canEdit)
    }

    @Test
    fun editChangesIgnoreUnchangedFieldsAndSurroundingSpaces() {
        val state = CategoryDetailsViewModel.stateFrom(category, isEmployee = false)
        val draft = CategoryEditDraft(name = " Informática ", description = "Notebooks e monitores")

        assertFalse(CategoryDetailsViewModel.editChangesFrom(state, draft).any)
        assertEquals(
            CategoryEditChanges(name = true, description = false),
            CategoryDetailsViewModel.editChangesFrom(state, draft.copy(name = "TI")),
        )
    }

    @Test
    fun nameAndDescriptionAreRequired() {
        assertEquals("Informe o nome da categoria.", CategoryDetailsViewModel.nameValidationError(" "))
        assertNull(CategoryDetailsViewModel.nameValidationError("Informática"))
        assertEquals("Informe a descrição da categoria.", CategoryDetailsViewModel.descriptionValidationError(" "))
        assertNull(CategoryDetailsViewModel.descriptionValidationError("Notebooks"))
    }

    @Test
    fun writeErrorNeverShowsTheHttpCode() {
        val error = HttpException(Response.error<Unit>(500, "".toResponseBody()))

        assertEquals("Não foi possível atualizar a categoria.", CategoryDetailsViewModel.writeError(error))
    }

    @Test
    fun connectionErrorHasNoPopup() {
        assertNull(CategoryDetailsViewModel.writeError(UnknownHostException("inventory")))
    }
}
