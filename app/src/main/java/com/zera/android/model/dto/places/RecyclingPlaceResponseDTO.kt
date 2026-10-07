package com.zera.android.model.dto.places

import kotlinx.serialization.Serializable

@Serializable
data class OpeningHourDTO(
    val days: String = "",
    val hours: String = "",
)

@Serializable
data class RecyclingPlaceResponseDTO(
    val placeId: String,
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val distanceMeters: Long,
    val isOpen: Boolean? = null,
    val description: String? = null,
    val openingHours: List<OpeningHourDTO> = emptyList(),
    val recyclingBusinessId: String? = null,
    val email: String? = null,
)
