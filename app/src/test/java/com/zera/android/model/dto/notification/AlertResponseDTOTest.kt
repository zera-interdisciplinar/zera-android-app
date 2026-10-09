package com.zera.android.model.dto.notification

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertResponseDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesContractSample() {
        val payload = """
            [
              {
                "alertId": "11111111-1111-1111-1111-111111111111",
                "kind": "STOCK_QUANTITY_LIMIT",
                "severity": "HIGH",
                "status": "OPEN",
                "description": "Estoque da unidade acima de 90% da capacidade configurada.",
                "unitId": "aa11bb22-0000-0000-0000-000000000009",
                "ruleId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "eventId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                "occurredAt": "2026-09-20T03:15:00",
                "createdAt": "2026-09-20T03:15:01",
                "updatedAt": "2026-09-20T03:15:01"
              }
            ]
        """.trimIndent()

        val alerts = json.decodeFromString<List<AlertResponseDTO>>(payload)
        val alert = alerts.single()

        assertEquals("11111111-1111-1111-1111-111111111111", alert.alertId)
        assertEquals("STOCK_QUANTITY_LIMIT", alert.kind)
        assertEquals("HIGH", alert.severity)
        assertEquals("OPEN", alert.status)
        assertEquals("Estoque da unidade acima de 90% da capacidade configurada.", alert.description)
        assertEquals("aa11bb22-0000-0000-0000-000000000009", alert.unitId)
        assertEquals("3fa85f64-5717-4562-b3fc-2c963f66afa6", alert.ruleId)
        assertEquals("7c9e6679-7425-40de-944b-e07fc1f90ae7", alert.eventId)
        assertEquals("2026-09-20T03:15:00", alert.occurredAt)
        assertEquals("2026-09-20T03:15:01", alert.createdAt)
        assertEquals("2026-09-20T03:15:01", alert.updatedAt)
    }

    @Test
    fun parsesNullRuleAndEvent() {
        val payload = """
            {
              "alertId": "22222222-2222-2222-2222-222222222222",
              "kind": "ITEM_APPROVED",
              "severity": "LOW",
              "status": "OPEN",
              "description": "Item aprovado pelo gestor.",
              "unitId": "aa11bb22-0000-0000-0000-000000000009",
              "ruleId": null,
              "eventId": null,
              "occurredAt": "2026-09-20T03:15:00",
              "createdAt": "2026-09-20T03:15:01",
              "updatedAt": "2026-09-20T03:15:01"
            }
        """.trimIndent()

        val alert = json.decodeFromString<AlertResponseDTO>(payload)

        assertEquals("ITEM_APPROVED", alert.kind)
        assertNull(alert.ruleId)
        assertNull(alert.eventId)
    }

    @Test
    fun parsesEmptyList() {
        val alerts = json.decodeFromString<List<AlertResponseDTO>>("[]")
        assertTrue(alerts.isEmpty())
    }
}
