package com.zera.android.model.dto.inventory

import kotlinx.serialization.Serializable

@Serializable
data class ItemResponseDTO(
    val id: String,
    val name: String,
)
