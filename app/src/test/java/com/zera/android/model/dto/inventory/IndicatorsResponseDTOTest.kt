package com.zera.android.model.dto.inventory

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IndicatorsResponseDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesContractSample() {
        val payload = """
            {
              "from": "2025-09-26",
              "to": "2026-09-26",
              "totalWeightKg": 342.75,
              "recyclingRatePercent": 78.4,
              "recyclingRateChangePoints": 5.2,
              "totalWeightChangePercent": -12.3,
              "monthlyWeightKg": [
                {"month": "2026-01", "weightKg": 25.5},
                {"month": "2026-02", "weightKg": 40.0}
              ],
              "weightByMaterial": [
                {"material": "PLASTIC", "weightKg": 120.5, "percent": 35.16},
                {"material": "METAL", "weightKg": 80.0, "percent": 23.34}
              ]
            }
        """.trimIndent()

        val indicators = json.decodeFromString<IndicatorsResponseDTO>(payload)

        assertEquals(78.4, indicators.recyclingRatePercent, 0.0)
        assertEquals(5.2, indicators.recyclingRateChangePoints)
        assertEquals(2, indicators.monthlyWeightKg.size)
        assertEquals("2026-01", indicators.monthlyWeightKg.first().month)
        assertEquals("PLASTIC", indicators.weightByMaterial.first().material)
        assertEquals(35.16, indicators.weightByMaterial.first().percent, 0.0)
    }

    @Test
    fun parsesNullChangeWhenPreviousPeriodIsEmpty() {
        val payload = """
            {
              "from": "2026-01-01",
              "to": "2026-01-31",
              "totalWeightKg": 10.0,
              "recyclingRatePercent": 50.0,
              "recyclingRateChangePoints": null,
              "totalWeightChangePercent": null,
              "monthlyWeightKg": [],
              "weightByMaterial": []
            }
        """.trimIndent()

        val indicators = json.decodeFromString<IndicatorsResponseDTO>(payload)

        assertNull(indicators.recyclingRateChangePoints)
        assertEquals(0, indicators.monthlyWeightKg.size)
    }
}
