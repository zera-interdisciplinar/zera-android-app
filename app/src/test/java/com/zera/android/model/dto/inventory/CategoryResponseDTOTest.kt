package com.zera.android.model.dto.inventory

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CategoryResponseDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesContractCategoriesSample() {
        val payload = """
            [
              {
                "id": "c1",
                "unitId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "name": "Informática",
                "description": null,
                "createdAt": "2026-07-01T09:00:00",
                "updatedAt": "2026-07-01T09:00:00"
              }
            ]
        """.trimIndent()

        val categories = json.decodeFromString<List<CategoryResponseDTO>>(payload)

        assertEquals(1, categories.size)
        assertEquals("c1", categories.first().id)
        assertEquals("Informática", categories.first().name)
        assertNull(categories.first().description)
    }
}
