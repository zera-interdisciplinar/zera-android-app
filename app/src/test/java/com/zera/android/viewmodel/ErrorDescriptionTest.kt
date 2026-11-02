package com.zera.android.viewmodel

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException

class ErrorDescriptionTest {

    @Test
    fun connectionErrorHasNoPopup() {
        assertNull(errorDescription(SocketTimeoutException("timeout"), "Falhou."))
    }

    @Test
    fun otherErrorsUseTheGivenDescription() {
        val serverError = HttpException(Response.error<Unit>(500, "".toResponseBody()))

        assertEquals("Falhou.", errorDescription(serverError, "Falhou."))
    }

    @Test
    fun loadDescriptionNamesWhatFailed() {
        assertEquals(
            "Houve uma instabilidade e não conseguimos carregar as categorias.",
            loadErrorDescription("as categorias"),
        )
    }

    @Test
    fun appMessagesAreKeptButTechnicalOnesAreNot() {
        assertEquals("Entre de novo.", appMessageOrFallback(IllegalStateException("Entre de novo."), "Falhou."))
        assertEquals("Falhou.", appMessageOrFallback(IllegalStateException("  "), "Falhou."))
        assertEquals("Falhou.", appMessageOrFallback(RuntimeException("HTTP 500 Internal Server Error"), "Falhou."))
        assertNull(appMessageOrFallback(SocketTimeoutException("timeout"), "Falhou."))
    }
}
