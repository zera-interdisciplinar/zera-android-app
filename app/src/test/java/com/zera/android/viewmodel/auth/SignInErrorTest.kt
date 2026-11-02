package com.zera.android.viewmodel.auth

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

class SignInErrorTest {

    @Test
    fun wrongCredentialsAreExplained() {
        val error = HttpException(Response.error<Unit>(401, "".toResponseBody()))

        assertEquals("E-mail ou senha incorretos.", SingInViewModel.signInError(error))
    }

    @Test
    fun serverErrorNeverShowsTheHttpCode() {
        val error = HttpException(Response.error<Unit>(500, "".toResponseBody()))

        assertEquals("Não foi possível entrar. Tente novamente.", SingInViewModel.signInError(error))
    }

    @Test
    fun unknownErrorHidesTheRawMessage() {
        assertEquals(
            "Não foi possível entrar. Tente novamente.",
            SingInViewModel.signInError(IllegalStateException("detalhe técnico")),
        )
    }

    @Test
    fun connectionErrorHasNoInlineMessage() {
        assertNull(SingInViewModel.signInError(UnknownHostException("adm")))
    }
}
