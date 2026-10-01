package com.zera.android.model.usecase.user

import com.zera.android.model.dto.user.SelfUserResponseDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetProfileTest {
    @Test
    fun fromDtoKeepsIdentityAndAvatar() {
        val dto = SelfUserResponseDTO(
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

        val profile = GetProfile.fromDto(dto)

        assertEquals("user-1", profile.userId)
        assertEquals("João Silva", profile.name)
        assertEquals("joao@empresa.com", profile.email)
        assertEquals("EMPLOYEE", profile.role)
        assertEquals("https://cdn.example.com/avatars/joao.png", profile.imageUrl)
    }

    @Test
    fun fromDtoAllowsNullImageUrl() {
        val dto = SelfUserResponseDTO(
            userId = "user-1",
            name = "Ada",
            email = "ada@zera.app",
            role = "MANAGER",
            status = "ACTIVE",
            unitId = "unit-1",
            createdAt = "2026-09-26T19:26:09",
            updatedAt = "2026-09-26T19:26:09",
        )

        assertNull(GetProfile.fromDto(dto).imageUrl)
    }
}
