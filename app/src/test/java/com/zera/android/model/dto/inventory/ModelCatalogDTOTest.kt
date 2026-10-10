package com.zera.android.model.dto.inventory

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ModelCatalogDTOTest {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = false }

    @Test
    fun parsesPagedModels() {
        val page = json.decodeFromString<PagedModelsDTO>(
            """
            {
              "content": [
                {
                  "id": "1b2c3d4e-0000-0000-0000-000000000001",
                  "name": "Latitude 5420",
                  "manufacturer": "Dell",
                  "materials": [{"code": "PLASTIC", "name": "Plástico"}],
                  "notes": null,
                  "category": {"id": "c1", "name": "Informática"},
                  "approvalStatus": "APPROVED"
                }
              ],
              "page": 0,
              "size": 20,
              "totalElements": 1,
              "totalPages": 1
            }
            """.trimIndent(),
        )

        assertEquals("Latitude 5420", page.content.single().name)
        assertEquals("APPROVED", page.content.single().approvalStatus)
        assertEquals("PLASTIC", page.content.single().materials.single().code)
        assertEquals(1L, page.totalElements)
    }

    @Test
    fun parsesMaterialCatalog() {
        val materials = json.decodeFromString<List<MaterialCatalogDTO>>(
            """[{"code":"PLASTIC","name":"Plástico","hazardous":false,"recyclable":true}]""",
        )

        assertEquals("PLASTIC", materials.single().code)
        assertEquals("Plástico", materials.single().name)
    }

    @Test
    fun encodesCreateModelWithoutEmptyNotes() {
        val payload = json.encodeToString(
            CreateModelRequestDTO.serializer(),
            CreateModelRequestDTO(
                name = "Latitude 5420",
                manufacturer = "Dell",
                categoryId = "c1",
                materials = listOf("PLASTIC"),
            ),
        )

        assertEquals(
            """{"name":"Latitude 5420","manufacturer":"Dell","categoryId":"c1","materials":["PLASTIC"]}""",
            payload,
        )
        assertFalse(payload.contains("notes"))
    }
}
