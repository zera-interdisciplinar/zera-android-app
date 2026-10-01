package com.zera.android.model.usecase.user

import com.zera.android.model.dto.telephone.TelephoneResponseDTO
import com.zera.android.model.dto.user.SelfUserResponseDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetProfileTest {
    private val user = SelfUserResponseDTO(
        userId = "user-1",
        name = "João Silva",
        email = "joao@empresa.com",
        role = "EMPLOYEE",
        status = "ACTIVE",
        unitId = "unit-1",
        createdAt = "2026-07-01T09:00:00",
        updatedAt = "2026-08-01T10:00:00",
        managerId = "manager-1",
        imageUrl = "https://cdn.example.com/avatars/joao.png",
    )

    @Test
    fun fromDtoKeepsIdentityAndAvatar() {
        val profile = GetProfile.fromDto(user)

        assertEquals("user-1", profile.userId)
        assertEquals("João Silva", profile.name)
        assertEquals("joao@empresa.com", profile.email)
        assertEquals("EMPLOYEE", profile.role)
        assertEquals("https://cdn.example.com/avatars/joao.png", profile.imageUrl)
        assertNull(profile.telephoneId)
        assertEquals("", profile.phone)
    }

    @Test
    fun fromDtoMergesTelephoneWhenPresent() {
        val telephone = TelephoneResponseDTO(
            telephoneId = "tel-1",
            number = "11987654321",
            userId = "user-1",
        )

        val profile = GetProfile.fromDto(user, telephone)

        assertEquals("tel-1", profile.telephoneId)
        assertEquals("11987654321", profile.phone)
    }
}
