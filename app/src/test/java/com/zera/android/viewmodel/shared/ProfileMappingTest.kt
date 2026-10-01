package com.zera.android.viewmodel.shared

import com.zera.android.model.entity.user.ProfileUser
import com.zera.android.model.entity.user.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
            telephoneId = "tel-1",
            phone = "11987654321",
        )

        val state = ProfileViewModel.stateFrom(profile)

        assertEquals("João Silva", state.displayName)
        assertEquals("João Silva", state.fullName)
        assertEquals("Operador", state.role)
        assertEquals("JS", state.initials)
        assertEquals("https://cdn.example.com/avatars/joao.png", state.photoUrl)
        assertEquals("joao@empresa.com", state.email)
        assertEquals("11987654321", state.phone)
        assertEquals("", state.company)
        assertEquals("Operador", state.position)
        assertFalse(state.canEditName)
        assertTrue(state.canEditEmail)
        assertFalse(state.canEditPhone)
    }

    @Test
    fun managerCanEditNameAndPhone() {
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
        assertTrue(state.canEditName)
        assertTrue(state.canEditEmail)
        assertTrue(state.canEditPhone)
    }

    @Test
    fun roleCaptionOmitsCompanyWhenBlank() {
        assertEquals("Gestor", ProfileViewModel.roleCaption("Gestor", ""))
        assertEquals("Gestor · Empresa Zera", ProfileViewModel.roleCaption("Gestor", "Empresa Zera"))
    }

    @Test
    fun phoneValidationAcceptsTenOrElevenDigits() {
        assertNull(ProfileViewModel.phoneValidationError("(11) 98765-4321"))
        assertNull(ProfileViewModel.phoneValidationError("1132654321"))
        assertEquals("Telefone inválido", ProfileViewModel.phoneValidationError("123"))
    }

    @Test
    fun saveTelephoneUsesCreateWhenThereIsNoId() {
        assertTrue(ProfileViewModel.saveTelephoneUsesCreate(null))
        assertTrue(ProfileViewModel.saveTelephoneUsesCreate(""))
        assertFalse(ProfileViewModel.saveTelephoneUsesCreate("tel-1"))
    }

    @Test
    fun emailWriteErrorFallsBackToGenericMessage() {
        assertEquals("falhou", ProfileViewModel.emailWriteError(Exception("falhou")))
    }
}
