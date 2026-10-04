package com.zera.android.model.dto.places

import kotlinx.serialization.Serializable

@Serializable
data class RecyclingPlaceResponseDTO(
    val placeId: String,
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val distanceMeters: Long,
)
