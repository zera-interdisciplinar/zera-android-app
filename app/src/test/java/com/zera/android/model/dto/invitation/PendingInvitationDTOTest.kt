package com.zera.android.model.dto.invitation

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class PendingInvitationDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesPendingInviteCardPayload() {
        val payload = """
            {
              "code": "120443",
              "inviteeName": "Operadora Carol the Best",
              "expiresAt": "2026-10-03T09:00:00"
            }
        """.trimIndent()

        val invite = json.decodeFromString<PendingInvitationDTO>(payload)

        assertEquals("120443", invite.code)
        assertEquals("Operadora Carol the Best", invite.inviteeName)
        assertEquals("2026-10-03T09:00:00", invite.expiresAt)
    }
}
