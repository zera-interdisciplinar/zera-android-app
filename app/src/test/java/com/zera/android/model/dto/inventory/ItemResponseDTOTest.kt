package com.zera.android.model.dto.inventory

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ItemResponseDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesContractItemSample() {
        val payload = """
            {
              "id": "9c858901-8a57-4791-81fe-4c455b099bc9",
              "barcode": "7891234567890",
              "displayCode": "ITM-0042",
              "name": "Notebook Dell Latitude",
              "status": "IN_STOCK",
              "condition": "USED",
              "hasDamages": false,
              "damages": [],
              "notes": null,
              "photoUrl": "https://storage.example.com/photos/item-9c85.jpg",
              "unitId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
              "model": {
                "id": "1b2c3d4e-0000-0000-0000-000000000001",
                "name": "Latitude 5420",
                "manufacturer": "Dell",
                "materials": [
                  {"id": "m1", "name": "Plastic"}
                ],
                "category": {"id": "c1", "name": "Informática", "description": null}
              },
              "serialNumber": "SN123456",
              "createdBy": "user-uuid",
              "createdByName": "João Silva",
              "createdAt": "2026-07-01T09:00:00",
              "updatedAt": "2026-09-20T15:00:00"
            }
        """.trimIndent()

        val item = json.decodeFromString<ItemResponseDTO>(payload)

        assertEquals("9c858901-8a57-4791-81fe-4c455b099bc9", item.id)
        assertEquals("Notebook Dell Latitude", item.name)
        assertEquals("ITM-0042", item.displayCode)
        assertEquals("IN_STOCK", item.status)
        assertEquals("USED", item.condition)
        assertEquals("SN123456", item.serialNumber)
        assertEquals("João Silva", item.createdByName)
        assertEquals("2026-07-01T09:00:00", item.createdAt)
        assertEquals("Latitude 5420", item.model?.name)
        assertEquals("Informática", item.model?.category?.name)
        assertEquals(listOf("Plastic"), item.model?.materials?.map { it.name })
    }

    @Test
    fun parsesPagedItemsEnvelope() {
        val payload = """
            {
              "content": [
                {
                  "id": "9c858901-8a57-4791-81fe-4c455b099bc9",
                  "name": "Notebook Dell Latitude",
                  "status": "IN_STOCK"
                }
              ],
              "page": 0,
              "size": 20,
              "totalElements": 1230,
              "totalPages": 62
            }
        """.trimIndent()

        val page = json.decodeFromString<PagedItemsDTO>(payload)

        assertEquals(1, page.content.size)
        assertEquals(0, page.page)
        assertEquals(20, page.size)
        assertEquals(1230L, page.totalElements)
        assertEquals(62, page.totalPages)
    }

    @Test
    fun parsesItemWithoutOptionalFields() {
        val payload = """
            {
              "id": "id-1",
              "name": "Item mínimo"
            }
        """.trimIndent()

        val item = json.decodeFromString<ItemResponseDTO>(payload)

        assertEquals("id-1", item.id)
        assertEquals("Item mínimo", item.name)
        assertNull(item.status)
        assertNull(item.model)
        assertTrue(item.model?.materials.orEmpty().isEmpty())
    }

    @Test
    fun parsesItemStatusAlias() {
        val payload = """
            {
              "id": "id-1",
              "name": "Notebook",
              "itemStatus": "IN_STOCK",
              "condition": "USED"
            }
        """.trimIndent()

        val item = json.decodeFromString<ItemResponseDTO>(payload)

        assertEquals("IN_STOCK", item.status)
        assertEquals("USED", item.condition)
    }

    @Test
    fun parsesApprovalStatusAlias() {
        val payload = """
            {
              "id": "id-1",
              "name": "Notebook",
              "approvalStatus": "APPROVED",
              "condition": "USED"
            }
        """.trimIndent()

        val item = json.decodeFromString<ItemResponseDTO>(payload)

        assertEquals("APPROVED", item.status)
        assertEquals("USED", item.condition)
    }
}
