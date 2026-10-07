package com.zera.android.model.dto.inventory

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class CreateDisposalRequestDTOTest {
    private val json = Json { encodeDefaults = false }

    @Test
    fun encodesRecyclingWithoutDisposedAt() {
        val payload = json.encodeToString(
            CreateDisposalRequestDTO.serializer(),
            CreateDisposalRequestDTO(
                destination = "RECYCLING",
                placeId = "ChIJ123",
                placeName = "Ecoponto Central",
                itemIds = listOf("item-1", "item-2"),
            ),
        )

        assertEquals(
            """{"destination":"RECYCLING","placeId":"ChIJ123","placeName":"Ecoponto Central","itemIds":["item-1","item-2"]}""",
            payload,
        )
        assertFalse(payload.contains("disposedAt"))
        assertFalse(payload.contains("notes"))
    }
}

class DisposalResponseDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun readsIdAndIgnoresExtraFields() {
        val parsed = json.decodeFromString(
            DisposalResponseDTO.serializer(),
            """{"id":"disposal-42","destination":"RECYCLING","placeName":"Ecoponto Central"}""",
        )

        assertEquals("disposal-42", parsed.id)
    }
}
