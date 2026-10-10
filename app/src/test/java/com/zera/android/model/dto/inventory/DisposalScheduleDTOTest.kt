package com.zera.android.model.dto.inventory

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class DisposalScheduleDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun decodesScheduleDetailWithContact() {
        val detail = json.decodeFromString(
            DisposalScheduleDetailDTO.serializer(),
            """
            {
              "id": "sch-1",
              "scheduledAt": "2026-10-20T15:00:00",
              "placeId": "places/ChIJ",
              "placeName": "Ecoponto Central",
              "items": [{ "itemId": "item-1", "displayCode": "265964", "name": "Notebook" }],
              "contact": { "email": "a@b.com", "phone": null }
            }
            """.trimIndent(),
        )

        assertEquals("sch-1", detail.id)
        assertEquals("Notebook", detail.items.single().name)
        assertEquals("a@b.com", detail.contact?.email)
        assertEquals(null, detail.contact?.phone)
    }
}
