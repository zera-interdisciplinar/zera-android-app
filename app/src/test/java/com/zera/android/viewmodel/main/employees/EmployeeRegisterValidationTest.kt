package com.zera.android.viewmodel.main.employees

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class EmployeeRegisterValidationTest {
    @Test
    fun nameIsRequired() {
        assertNotNull(EmployeeRegisterViewModel.nameValidationError("   "))
        assertNull(EmployeeRegisterViewModel.nameValidationError("Carol"))
    }

    @Test
    fun emailIsRequiredAndNeedsAt() {
        assertNotNull(EmployeeRegisterViewModel.emailValidationError(""))
        assertEquals("E-mail inválido.", EmployeeRegisterViewModel.emailValidationError("carol.zera.com"))
        assertNull(EmployeeRegisterViewModel.emailValidationError(" carol@zera.com "))
    }

    @Test
    fun createInviteErrorFallsBackToGenericMessage() {
        assertEquals(
            "Não foi possível criar o convite. Tente novamente.",
            EmployeeRegisterViewModel.createInviteError(IllegalStateException("")),
        )
    }
}
