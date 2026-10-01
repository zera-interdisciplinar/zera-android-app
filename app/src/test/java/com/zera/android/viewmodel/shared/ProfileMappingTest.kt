package com.zera.android.viewmodel.shared

import com.zera.android.model.entity.user.ProfileUser
import com.zera.android.model.entity.user.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileMappingTest {
    @Test
    fun stateFromMapsContractUserToProfileFields() {
        val profile = ProfileUser(
            userId = "user-1",
            name = "João Silva",
            email = "joao@empresa.com",
            role = UserRole.EMPLOYEE,
            imageUrl = "https://cdn.example.com/avatars/joao.png",
        )

        val state = ProfileViewModel.stateFrom(profile)

        assertEquals("João Silva", state.displayName)
        assertEquals("João Silva", state.fullName)
        assertEquals("Operador", state.role)
        assertEquals("JS", state.initials)
        assertEquals("https://cdn.example.com/avatars/joao.png", state.photoUrl)
        assertEquals("joao@empresa.com", state.email)
        assertEquals("", state.phone)
        assertEquals("", state.company)
        assertEquals("Operador", state.position)
    }

    @Test
    fun managerRoleBecomesGestor() {
        val profile = ProfileUser(
            userId = "user-2",
            name = "Natalia Flores",
            email = "natalia@zera.com.br",
            role = UserRole.MANAGER,
            imageUrl = null,
        )

        val state = ProfileViewModel.stateFrom(profile)

        assertEquals("Gestor", state.role)
        assertEquals("NF", state.initials)
        assertNull(state.photoUrl)
    }

    @Test
    fun roleCaptionOmitsCompanyWhenBlank() {
        assertEquals("Gestor", ProfileViewModel.roleCaption("Gestor", ""))
        assertEquals("Gestor · Empresa Zera", ProfileViewModel.roleCaption("Gestor", "Empresa Zera"))
    }
}
