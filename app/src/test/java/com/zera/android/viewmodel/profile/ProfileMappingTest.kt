package com.zera.android.viewmodel.profile

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
    }

    @Test
    fun managerCanEditName() {
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
    }

    @Test
    fun roleCaptionOmitsCompanyWhenBlank() {
        assertEquals("Gestor", ProfileViewModel.roleCaption("Gestor", ""))
        assertEquals("Gestor · Empresa Zera", ProfileViewModel.roleCaption("Gestor", "Empresa Zera"))
    }

    @Test
    fun photoValidationRejectsWrongMimeAndEmptyBytes() {
        assertEquals(
            "Use uma foto JPEG, PNG ou WebP.",
            ProfileViewModel.photoValidationError(byteArrayOf(1), "application/pdf"),
        )
        assertEquals(
            "A foto deve ter no máximo 5 MB.",
            ProfileViewModel.photoValidationError(byteArrayOf(), "image/jpeg"),
        )
    }

    @Test
    fun photoWriteErrorHidesTechnicalStorageDetails() {
        assertEquals(
            "Este tipo de arquivo não é permitido. Use JPEG, PNG ou WebP.",
            ProfileViewModel.photoWriteError(Exception("Não foi possível enviar a foto (400): InvalidMimeType")),
        )
        assertEquals(
            "Sem permissão para enviar a foto. Confira a política do Storage.",
            ProfileViewModel.photoWriteError(
                Exception("Não foi possível enviar a foto (400): new row violates row-level security policy"),
            ),
        )
    }

    @Test
    fun emailWriteErrorFallsBackToGenericMessage() {
        assertEquals("falhou", ProfileViewModel.emailWriteError(Exception("falhou")))
    }

    @Test
    fun editChangesIgnoreUnchangedFieldsAndSurroundingSpaces() {
        val state = ProfileState(
            fullName = "Ana Lima",
            email = "ana@zera.com",
            canEditName = true,
        )
        val draft = ProfileEditDraft(fullName = " Ana Lima ", email = "ana@zera.com")

        assertFalse(ProfileViewModel.editChangesFrom(state, draft).any)
        assertEquals(
            ProfileEditChanges(name = false, email = true),
            ProfileViewModel.editChangesFrom(state, draft.copy(email = "ana.lima@zera.com")),
        )
    }

    @Test
    fun employeeEditChangesOnlyIncludeEmail() {
        val state = ProfileState(
            fullName = "Caio Reis",
            email = "caio@zera.com",
            canEditName = false,
        )
        val draft = ProfileEditDraft(fullName = "Outro Nome", email = "novo@zera.com")

        assertEquals(
            ProfileEditChanges(name = false, email = true),
            ProfileViewModel.editChangesFrom(state, draft),
        )
    }
}
