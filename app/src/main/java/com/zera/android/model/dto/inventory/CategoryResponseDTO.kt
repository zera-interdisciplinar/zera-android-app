package com.zera.android.model.dto.inventory

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponseDTO(
    val id: String,
    val name: String,
    val unitId: String? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class CreateCategoryRequestDTO(
    val name: String,
    val description: String? = null,
)
