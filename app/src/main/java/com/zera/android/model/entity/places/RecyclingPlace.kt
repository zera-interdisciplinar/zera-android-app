package com.zera.android.model.entity.places

data class OpeningHour(
    val days: String,
    val hours: String,
)

data class RecyclingPlace(
    val placeId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Long,
    val isOpen: Boolean? = null,
    val description: String = "",
    val openingHours: List<OpeningHour> = emptyList(),
    val recyclingBusinessId: String = "",
    val email: String = "",
)
