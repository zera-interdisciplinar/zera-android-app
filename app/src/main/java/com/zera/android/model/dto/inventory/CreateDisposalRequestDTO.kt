package com.zera.android.model.dto.inventory

import kotlinx.serialization.Serializable

@Serializable
data class CreateDisposalRequestDTO(
    val destination: String,
    val placeId: String? = null,
    val placeName: String? = null,
    val disposedAt: String? = null,
    val notes: String? = null,
    val itemIds: List<String>,
)
