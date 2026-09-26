package com.zera.android.model.dto.inventory

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DashboardHomeResponseDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesContractSample() {
        val payload = """
            {
              "activeItems": 128,
              "activeItemsChangePercent": 4.35,
              "stockCapacity": 200,
              "occupancyPercent": 64.0,
              "pendingApproval": 3,
              "inMaintenance": 2,
              "awaitingEvaluation": 1,
              "disposalsInWindow": 5,
              "windowDays": 30,
              "recentItems": {
                "content": [
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
                      "unitId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                      "name": "Latitude 5420",
                      "manufacturer": "Dell",
                      "warrantyMonths": 12,
                      "expectedLifespanMonths": 48,
                      "materials": [
                        {"id": "m1", "name": "Plastic"}
                      ],
                      "hazardous": false,
                      "estimatedWeightKg": 1.8,
                      "notes": null,
                      "category": {"id": "c1", "name": "Informática"},
                      "approvalStatus": "APPROVED",
                      "rejectionReason": null,
                      "createdBy": "user-uuid",
                      "reviewedBy": "user-uuid",
                      "reviewedAt": "2026-08-01T10:00:00",
                      "createdAt": "2026-07-01T09:00:00",
                      "updatedAt": "2026-08-01T10:00:00"
                    },
                    "serialNumber": "SN123456",
                    "acquiredAt": "2026-07-01",
                    "manufacturingYear": 2024,
                    "usageIntensity": 3,
                    "predictedFailureDate": "2029-07-01",
                    "predictionUpdatedAt": "2026-08-01T10:00:00",
                    "missingFields": [],
                    "lastEventAt": "2026-09-20T15:00:00",
                    "createdBy": "user-uuid",
                    "createdByName": "João Silva",
                    "createdAt": "2026-07-01T09:00:00",
                    "updatedAt": "2026-09-20T15:00:00"
                  }
                ],
                "page": 0,
                "size": 5,
                "totalElements": 128,
                "totalPages": 26
              }
            }
        """.trimIndent()

        val home = json.decodeFromString<DashboardHomeResponseDTO>(payload)

        assertEquals(128L, home.activeItems)
        assertEquals(4.35, home.activeItemsChangePercent)
        assertEquals(64.0, home.occupancyPercent)
        assertEquals(3L, home.pendingApproval)
        assertEquals(1, home.recentItems.content.size)
        assertEquals("Notebook Dell Latitude", home.recentItems.content.first().name)
        assertEquals("9c858901-8a57-4791-81fe-4c455b099bc9", home.recentItems.content.first().id)
    }

    @Test
    fun parsesNullComparisonsAndCapacity() {
        val payload = """
            {
              "activeItems": 0,
              "activeItemsChangePercent": null,
              "stockCapacity": null,
              "occupancyPercent": null,
              "pendingApproval": 0,
              "inMaintenance": 0,
              "awaitingEvaluation": 0,
              "disposalsInWindow": 0,
              "windowDays": 30,
              "recentItems": {
                "content": [],
                "page": 0,
                "size": 5,
                "totalElements": 0,
                "totalPages": 0
              }
            }
        """.trimIndent()

        val home = json.decodeFromString<DashboardHomeResponseDTO>(payload)

        assertNull(home.activeItemsChangePercent)
        assertNull(home.occupancyPercent)
        assertEquals(0, home.recentItems.content.size)
    }
}
