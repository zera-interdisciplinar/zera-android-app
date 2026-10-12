package com.zera.android.model.dto.inventory

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class CreateItemRequestDTOTest {
    private val json = Json { encodeDefaults = false }

    @Test
    fun omitsNullBarcode() {
        val payload = json.encodeToString(
            CreateItemRequestDTO.serializer(),
            CreateItemRequestDTO(
                name = "Notebook",
                modelId = "model-1",
                serialNumber = "SN1",
                condition = "USED",
                hasDamages = false,
                damages = emptyList(),
                acquiredAt = "2026-07-01",
                manufacturingYear = 2022,
                usageIntensity = 1,
            ),
        )

        assertFalse(payload.contains("barcode"))
        assertEquals(
            """{"name":"Notebook","modelId":"model-1","serialNumber":"SN1","condition":"USED","hasDamages":false,"damages":[],"acquiredAt":"2026-07-01","manufacturingYear":2022,"usageIntensity":1}""",
            payload,
        )
    }

    @Test
    fun encodesBarcodeWhenPresent() {
        val payload = json.encodeToString(
            CreateItemRequestDTO.serializer(),
            CreateItemRequestDTO(name = "Notebook", modelId = "model-1", barcode = "789"),
        )

        assertEquals(
            """{"name":"Notebook","modelId":"model-1","barcode":"789"}""",
            payload,
        )
    }
}
