package com.zera.android.viewmodel.main.employees

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

class EmployeeInviteTest {
    @Test
    fun inviteNameIsRequired() {
        assertEquals("Informe o nome do convite.", EmployeesViewModel.inviteNameValidationError("   "))
        assertNull(EmployeesViewModel.inviteNameValidationError("Carol"))
    }

    @Test
    fun createInviteErrorFallsBackToGenericMessage() {
        assertEquals(
            "Não foi possível criar o convite. Tente novamente.",
            EmployeesViewModel.createInviteError(IllegalStateException("")),
        )
    }

    @Test
    fun createInviteErrorExplainsMissingPermission() {
        val error = HttpException(Response.error<Unit>(403, "".toResponseBody()))

        assertEquals("Sem permissão para criar convite.", EmployeesViewModel.createInviteError(error))
    }

    @Test
    fun connectionErrorHasNoPopup() {
        assertNull(EmployeesViewModel.createInviteError(UnknownHostException("adm")))
    }
}
