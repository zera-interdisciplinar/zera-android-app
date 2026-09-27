package com.zera.android.model.dto.inventory

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class UpdateItemRequestDTOTest {
    private val json = Json { encodeDefaults = false }

    @Test
    fun encodesNameAndCondition() {
        val payload = json.encodeToString(
            UpdateItemRequestDTO.serializer(),
            UpdateItemRequestDTO(name = "Notebook Dell Latitude", condition = "USED"),
        )

        assertEquals(
            """{"name":"Notebook Dell Latitude","condition":"USED"}""",
            payload,
        )
    }

    @Test
    fun omitsNullFields() {
        val payload = json.encodeToString(
            UpdateItemRequestDTO.serializer(),
            UpdateItemRequestDTO(name = "Notebook"),
        )

        assertEquals("""{"name":"Notebook"}""", payload)
        assertFalse(payload.contains("condition"))
        assertFalse(payload.contains("hasDamages"))
        assertFalse(payload.contains("notes"))
    }

    @Test
    fun encodesContractSampleFields() {
        val payload = json.encodeToString(
            UpdateItemRequestDTO.serializer(),
            UpdateItemRequestDTO(
                name = "Notebook Dell Latitude 5420",
                condition = "USED",
                hasDamages = false,
                notes = "",
                serialNumber = "SN123456",
                acquiredAt = "2026-07-01",
                manufacturingYear = 2024,
                usageIntensity = 3,
            ),
        )

        assertEquals(
            """{"name":"Notebook Dell Latitude 5420","condition":"USED","hasDamages":false,"notes":"","serialNumber":"SN123456","acquiredAt":"2026-07-01","manufacturingYear":2024,"usageIntensity":3}""",
            payload,
        )
    }
}

class RejectItemRequestDTOTest {
    private val json = Json { encodeDefaults = false }

    @Test
    fun encodesReason() {
        val payload = json.encodeToString(
            RejectItemRequestDTO.serializer(),
            RejectItemRequestDTO(reason = "Foto ilegível, reenviar"),
        )

        assertEquals("""{"reason":"Foto ilegível, reenviar"}""", payload)
    }
}
