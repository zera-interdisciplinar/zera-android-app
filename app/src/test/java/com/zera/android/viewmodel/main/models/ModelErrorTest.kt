package com.zera.android.viewmodel.main.models

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
            materialCode = "PLASTIC",
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
        val errors = ModelCreationViewModel.fieldErrors("Notebook", "Dell", "c1", "PLASTIC", "ok")

        assertFalse(errors.hasAny)
    }

    @Test
    fun tooLongNotesAreInvalid() {
        val errors = ModelCreationViewModel.fieldErrors("Notebook", "Dell", "c1", "PLASTIC", "a".repeat(501))

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
