package com.zera.android.model.dto.telephone

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TelephoneResponseDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesGetTelephoneUserPayload() {
        val payload = """
            {
              "telephoneId": "bb22cc33-0000-0000-0000-000000000001",
              "number": "11987654321",
              "userId": "9c858901-8a57-4791-81fe-4c455b099bc9",
              "organizationId": null,
              "unitId": null,
              "recyclingBusinessId": null,
              "createdAt": "2026-07-01T09:00:00",
              "updatedAt": "2026-07-01T09:00:00"
            }
        """.trimIndent()

        val telephone = json.decodeFromString<TelephoneResponseDTO>(payload)

        assertEquals("bb22cc33-0000-0000-0000-000000000001", telephone.telephoneId)
        assertEquals("11987654321", telephone.number)
        assertEquals("9c858901-8a57-4791-81fe-4c455b099bc9", telephone.userId)
        assertNull(telephone.organizationId)
        assertTrue(telephone.belongsToUser("9c858901-8a57-4791-81fe-4c455b099bc9"))
    }

    @Test
    fun belongsToUserRejectsOtherOwners() {
        val userPhone = TelephoneResponseDTO(
            telephoneId = "tel-1",
            number = "11987654321",
            userId = "user-1",
        )
        val orgPhone = userPhone.copy(organizationId = "org-1")
        val otherUser = userPhone.copy(userId = "user-2")

        assertTrue(userPhone.belongsToUser("user-1"))
        assertFalse(orgPhone.belongsToUser("user-1"))
        assertFalse(otherUser.belongsToUser("user-1"))
    }
}
