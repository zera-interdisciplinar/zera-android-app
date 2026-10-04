package com.zera.android.model.dto.places

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class RecyclingPlaceResponseDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesContractSample() {
        val payload = """
            [
              {
                "placeId": "places/ChIJxxxxxxxx",
                "name": "Cooperativa Recicla SP",
                "address": "Rua X, 123 - Centro, São Paulo - SP",
                "lat": -23.551,
                "lng": -46.634,
                "distanceMeters": 1240
              }
            ]
        """.trimIndent()

        val places = json.decodeFromString<List<RecyclingPlaceResponseDTO>>(payload)

        assertEquals(1, places.size)
        assertEquals("places/ChIJxxxxxxxx", places.first().placeId)
        assertEquals("Cooperativa Recicla SP", places.first().name)
        assertEquals("Rua X, 123 - Centro, São Paulo - SP", places.first().address)
        assertEquals(-23.551, places.first().lat, 0.0)
        assertEquals(-46.634, places.first().lng, 0.0)
        assertEquals(1240L, places.first().distanceMeters)
    }
}
