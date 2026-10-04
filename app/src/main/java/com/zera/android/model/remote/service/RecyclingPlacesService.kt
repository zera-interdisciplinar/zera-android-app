package com.zera.android.model.remote.service

import com.zera.android.model.dto.places.RecyclingPlaceResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface RecyclingPlacesService {
    @GET("recycling-places")
    suspend fun getNearby(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radiusMeters") radiusMeters: Int? = null,
    ): List<RecyclingPlaceResponseDTO>
}
