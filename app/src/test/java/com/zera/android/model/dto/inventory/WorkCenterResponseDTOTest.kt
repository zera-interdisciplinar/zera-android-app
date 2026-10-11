package com.zera.android.model.dto.inventory

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WorkCenterResponseDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesContractSample() {
        val payload = """
            {
              "drafts": [{
                "id": "aa11bb22-0000-0000-0000-000000000001",
                "displayCode": "ITM-0099",
                "name": "Monitor LG 24pol",
                "modelName": "24ML600",
                "missingFields": ["serialNumber", "photoUrl"],
                "rejectionReason": null,
                "updatedAt": "2026-09-22T11:30:00"
              }],
              "rejected": [{
                "id": "aa11bb22-0000-0000-0000-000000000002",
                "displayCode": "ITM-0088",
                "name": "Teclado Logitech",
                "modelName": "K120",
                "missingFields": [],
                "rejectionReason": "Foto ilegível, reenviar",
                "updatedAt": "2026-09-21T09:00:00"
              }],
              "damagedWithoutDestination": [{
                "id": "aa11bb22-0000-0000-0000-000000000003",
                "displayCode": "ITM-0077",
                "name": "Mouse Dell",
                "modelName": "MS116",
                "missingFields": [],
                "rejectionReason": null,
                "updatedAt": "2026-09-19T14:00:00"
              }],
              "inMaintenance": 2,
              "awaitingEvaluation": 1
            }
        """.trimIndent()

        val workCenter = json.decodeFromString<WorkCenterResponseDTO>(payload)

        assertEquals(1, workCenter.drafts.size)
        assertEquals("aa11bb22-0000-0000-0000-000000000001", workCenter.drafts.first().id)
        assertEquals(listOf("serialNumber", "photoUrl"), workCenter.drafts.first().missingFields)
        assertEquals("Foto ilegível, reenviar", workCenter.rejected.first().rejectionReason)
        assertEquals("Mouse Dell", workCenter.damagedWithoutDestination.first().name)
        assertEquals(2L, workCenter.inMaintenance)
        assertEquals(1L, workCenter.awaitingEvaluation)
    }

    @Test
    fun parsesEmptyBuckets() {
        val payload = """
            {
              "drafts": [],
              "rejected": [],
              "damagedWithoutDestination": [],
              "inMaintenance": 0,
              "awaitingEvaluation": 0
            }
        """.trimIndent()

        val workCenter = json.decodeFromString<WorkCenterResponseDTO>(payload)

        assertEquals(0, workCenter.drafts.size)
        assertEquals(0, workCenter.rejected.size)
        assertEquals(0, workCenter.damagedWithoutDestination.size)
        assertNull(workCenter.drafts.firstOrNull()?.rejectionReason)
    }
}
