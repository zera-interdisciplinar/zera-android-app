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
                "distanceMeters": 1240,
                "isOpen": true,
                "description": "Cooperativa de reciclagem de materiais.",
                "openingHours": [
                  { "days": "segunda-feira", "hours": "08:00 – 18:00" }
                ],
                "recyclingBusinessId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "email": "contato@reciclasp.com"
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
        assertEquals(true, places.first().isOpen)
        assertEquals("Cooperativa de reciclagem de materiais.", places.first().description)
        assertEquals("segunda-feira", places.first().openingHours.single().days)
        assertEquals("08:00 – 18:00", places.first().openingHours.single().hours)
        assertEquals("3fa85f64-5717-4562-b3fc-2c963f66afa6", places.first().recyclingBusinessId)
        assertEquals("contato@reciclasp.com", places.first().email)
    }

    @Test
    fun missingOptionalPinFieldsStayEmpty() {
        val payload = """
            [{
              "placeId": "places/x",
              "name": "Sem ficha",
              "address": "Rua Y",
              "lat": 0,
              "lng": 0,
              "distanceMeters": 10
            }]
        """.trimIndent()

        val place = json.decodeFromString<List<RecyclingPlaceResponseDTO>>(payload).single()

        assertEquals(null, place.isOpen)
        assertEquals(null, place.recyclingBusinessId)
        assertEquals(null, place.email)
        assertEquals(emptyList<OpeningHourDTO>(), place.openingHours)
    }
}
