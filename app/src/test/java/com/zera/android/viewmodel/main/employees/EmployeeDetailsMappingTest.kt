package com.zera.android.viewmodel.main.employees

import com.zera.android.model.dto.user.SelfUserResponseDTO
import com.zera.android.model.entity.user.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class EmployeeDetailsMappingTest {
    private val user = SelfUserResponseDTO(
        userId = "9c858901-8a57-4791-81fe-4c455b099bc9",
        name = "João Silva",
        email = "joao@empresa.com",
        role = UserRole.EMPLOYEE,
        status = "ACTIVE",
        unitId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        createdAt = "2026-07-01T09:00:00",
        updatedAt = "2026-08-01T10:00:00",
        imageUrl = "https://cdn.example.com/avatars/joao.png",
    )

    @Test
    fun stateFromMapsUserFieldsAndRoleLabel() {
        val state = EmployeeDetailsViewModel.stateFrom(user)
        assertEquals(user.userId, state.employeeId)
        assertEquals("João Silva", state.name)
        assertEquals("JS", state.initials)
        assertEquals("joao@empresa.com", state.email)
        assertEquals(user.imageUrl, state.photoUrl)
        assertEquals(EmployeesViewModel.ROLE_OPERATOR, state.position)
        assertEquals("", state.phone)
        assertFalse(state.isLoading)
    }

    @Test
    fun loadErrorFallsBackToGenericMessage() {
        assertEquals(
            "Não foi possível carregar o operário.",
            EmployeeDetailsViewModel.loadError(IllegalStateException("")),
        )
    }
}
