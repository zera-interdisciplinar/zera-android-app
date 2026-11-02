package com.zera.android.viewmodel.main.items

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException

class ItemDetailsErrorTest {

    @Test
    fun conflictUsesTheGivenMessage() {
        assertEquals("Conflito.", userFacingError(httpError(409)))
    }

    @Test
    fun serverErrorNeverShowsTheHttpCode() {
        assertEquals("Falhou.", userFacingError(httpError(500)))
    }

    @Test
    fun unknownErrorUsesTheFallback() {
        assertEquals("Falhou.", userFacingError(IllegalStateException("detalhe técnico")))
    }

    @Test
    fun connectionErrorHasNoPopup() {
        assertNull(userFacingError(ConnectException("refused")))
    }

    private fun userFacingError(error: Exception): String? = ItemDetailsViewModel.userFacingError(
        error = error,
        conflictMessage = "Conflito.",
        validationMessage = "Inválido.",
        fallback = "Falhou.",
    )

    private fun httpError(code: Int) = HttpException(Response.error<Unit>(code, "".toResponseBody()))
}
