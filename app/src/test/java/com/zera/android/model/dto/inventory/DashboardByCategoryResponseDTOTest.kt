package com.zera.android.model.dto.inventory

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DashboardByCategoryResponseDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesContractSample() {
        val payload = """
            {
              "categories": [
                { "categoryId": "uuid", "name": "Notebooks", "itemCount": 3, "percent": 75.0 }
              ],
              "totalItems": 4
            }
        """.trimIndent()

        val body = json.decodeFromString<DashboardByCategoryResponseDTO>(payload)

        assertEquals(4, body.totalItems)
        assertEquals(1, body.categories.size)
        assertEquals("uuid", body.categories.first().categoryId)
        assertEquals("Notebooks", body.categories.first().name)
        assertEquals(3, body.categories.first().itemCount)
        assertEquals(75.0, body.categories.first().percent, 0.0)
    }

    @Test
    fun parsesNullCategoryForItemsWithoutModel() {
        val payload = """
            {
              "categories": [
                { "categoryId": null, "name": null, "itemCount": 1, "percent": 25.0 }
              ],
              "totalItems": 4
            }
        """.trimIndent()

        val row = json.decodeFromString<DashboardByCategoryResponseDTO>(payload).categories.first()

        assertNull(row.categoryId)
        assertNull(row.name)
        assertEquals(1, row.itemCount)
    }
}
