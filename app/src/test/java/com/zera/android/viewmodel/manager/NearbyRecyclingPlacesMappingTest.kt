package com.zera.android.viewmodel.manager

import com.zera.android.model.entity.places.RecyclingPlace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NearbyRecyclingPlacesMappingTest {
    private val place = RecyclingPlace(
        placeId = "places/ChIJxxxxxxxx",
        name = "Cooperativa Recicla SP",
        address = "Rua X, 123",
        latitude = -23.551,
        longitude = -46.634,
        distanceMeters = 1240,
    )

    @Test
    fun emptyListIsEmptyStateNotAnError() {
        val state = applyPlaces(NearbyRecyclingPlacesState(), emptyList())
        assertTrue(state.places.isEmpty())
        assertEquals("Nenhuma recicladora encontrada por aqui.", state.statusMessage)
        assertFalse(state.canRetry)
    }

    @Test
    fun keepsServerOrderAndSelection() {
        val current = NearbyRecyclingPlacesState(selectedPlaceId = place.placeId)
        val state = applyPlaces(current, listOf(place))
        assertEquals(place.placeId, state.places.first().placeId)
        assertEquals(place, state.selectedPlace)
        assertNull(state.statusMessage)
    }

    @Test
    fun serviceUnavailableIsRetryableAndClearsPlaces() {
        val state = applyFailure(
            NearbyRecyclingPlacesState(places = listOf(place), selectedPlaceId = place.placeId),
            httpCode = 503,
            detail = "Busca de recicladoras proximas esta desativada nesta instancia",
        )
        assertTrue(state.places.isEmpty())
        assertNull(state.selectedPlaceId)
        assertTrue(state.canRetry)
        assertEquals(
            "Busca de recicladoras proximas esta desativada nesta instancia",
            state.statusMessage,
        )
    }

    @Test
    fun formatsDistanceLikeTheContractExample() {
        assertEquals("1,2 km", formatDistanceMeters(1240))
        assertEquals("800 m", formatDistanceMeters(800))
    }
}
