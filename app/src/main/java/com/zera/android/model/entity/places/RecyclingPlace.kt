package com.zera.android.model.entity.places

data class RecyclingPlace(
    val placeId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Long,
)
