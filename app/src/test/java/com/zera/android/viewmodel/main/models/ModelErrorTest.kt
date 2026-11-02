package com.zera.android.viewmodel.main.models

import com.zera.android.model.dto.inventory.MaterialCatalogDTO
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

class ModelErrorTest {

    @Test
    fun fieldErrorsPointAtEachInvalidField() {
        val errors = ModelCreationViewModel.fieldErrors(
            name = " ",
            manufacturer = "",
            categoryId = null,
            materialCodes = listOf("PLASTIC", "BATTERY"),
            notes = "",
        )

        assertEquals("Informe o nome do modelo.", errors.name)
        assertEquals("Informe a marca.", errors.manufacturer)
        assertEquals("Selecione a categoria.", errors.category)
        assertNull(errors.material)
        assertNull(errors.notes)
        assertEquals("Informe o nome do modelo.", errors.first)
    }

    @Test
    fun validFormHasNoFieldErrors() {
        val errors = ModelCreationViewModel.fieldErrors("Notebook", "Dell", "c1", listOf("PLASTIC"), "ok")

        assertFalse(errors.hasAny)
    }

    @Test
    fun formWithoutMaterialsIsInvalid() {
        val errors = ModelCreationViewModel.fieldErrors("Notebook", "Dell", "c1", emptyList(), "ok")

        assertEquals("Selecione ao menos um material.", errors.material)
    }

    @Test
    fun materialCodesFollowCatalogOrderAndIgnoreUnknownNames() {
        val catalog = listOf(
            MaterialCatalogDTO(code = "PLASTIC", name = "Plástico"),
            MaterialCatalogDTO(code = "METAL", name = "Metal"),
            MaterialCatalogDTO(code = "BATTERY", name = "Bateria"),
        )

        assertEquals(
            listOf("PLASTIC", "BATTERY"),
            ModelCreationViewModel.materialCodes(listOf("Bateria", "Plástico", "Inexistente"), catalog),
        )
    }

    @Test
    fun materialsLabelJoinsTheSelectedNames() {
        assertEquals("Plástico, Bateria", ModelCreationState(materials = listOf("Plástico", "Bateria")).materialsLabel)
        assertEquals("", ModelCreationState().materialsLabel)
    }

    @Test
    fun tooLongNotesAreInvalid() {
        val errors = ModelCreationViewModel.fieldErrors("Notebook", "Dell", "c1", listOf("PLASTIC"), "a".repeat(501))

        assertTrue(errors.hasAny)
        assertEquals("A observação pode ter no máximo 500 caracteres.", errors.notes)
    }

    @Test
    fun serverErrorNeverShowsTheHttpCode() {
        val error = HttpException(Response.error<Unit>(500, "".toResponseBody()))

        assertEquals("Falhou.", userFacingError(error))
    }

    @Test
    fun connectionErrorHasNoPopup() {
        assertNull(userFacingError(UnknownHostException("inventory")))
    }

    private fun userFacingError(error: Exception): String? = ModelDetailsViewModel.userFacingError(
        error = error,
        conflictMessage = "Conflito.",
        validationMessage = "Inválido.",
        fallback = "Falhou.",
    )
}
