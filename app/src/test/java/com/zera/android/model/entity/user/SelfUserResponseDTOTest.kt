package com.zera.android.model.entity.user

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
    }
}
