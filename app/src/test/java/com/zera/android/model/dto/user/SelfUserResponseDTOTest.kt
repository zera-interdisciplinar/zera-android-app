package com.zera.android.model.dto.user

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SelfUserResponseDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesWhenManagerIdIsNull() {
        val payload = """
            {
              "userId": "user-1",
              "name": "Ada",
              "email": "ada@zera.app",
              "role": "MANAGER",
              "status": "ACTIVE",
              "unitId": "unit-1",
              "createdAt": "2026-09-26T19:26:09",
              "updatedAt": "2026-09-26T19:26:09",
              "managerId": null
            }
        """.trimIndent()

        val user = json.decodeFromString<SelfUserResponseDTO>(payload)

        assertEquals("MANAGER", user.role)
        assertNull(user.managerId)
        assertNull(user.imageUrl)
    }

    @Test
    fun parsesImageUrlWhenPresent() {
        val payload = """
            {
              "userId": "9c858901-8a57-4791-81fe-4c455b099bc9",
              "name": "João Silva",
              "email": "joao@empresa.com",
              "role": "EMPLOYEE",
              "status": "ACTIVE",
              "unitId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
              "createdAt": "2026-07-01T09:00:00",
              "updatedAt": "2026-08-01T10:00:00",
              "managerId": "aa11bb22-0000-0000-0000-000000000009",
              "imageUrl": "https://cdn.example.com/avatars/joao.png"
            }
        """.trimIndent()

        val user = json.decodeFromString<SelfUserResponseDTO>(payload)

        assertEquals("https://cdn.example.com/avatars/joao.png", user.imageUrl)
        assertEquals("aa11bb22-0000-0000-0000-000000000009", user.managerId)
    }
}
