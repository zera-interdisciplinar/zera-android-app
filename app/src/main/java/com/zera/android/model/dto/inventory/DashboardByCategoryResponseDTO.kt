package com.zera.android.model.dto.inventory

import kotlinx.serialization.Serializable

@Serializable
data class DashboardByCategoryResponseDTO(
    val categories: List<CategoryInventoryDTO> = emptyList(),
    val totalItems: Long = 0,
)

@Serializable
data class CategoryInventoryDTO(
    val categoryId: String? = null,
    val name: String? = null,
    val itemCount: Long,
    val percent: Double,
)
