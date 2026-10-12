package com.zera.android.model.dto.inventory

import kotlinx.serialization.Serializable

@Serializable
data class PagedModelsDTO(
    val content: List<ModelResponseDTO> = emptyList(),
    val page: Int = 0,
    val size: Int = 20,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
)

@Serializable
data class UpdateModelNameRequestDTO(
    val name: String,
)

@Serializable
data class UpdateModelManufacturerRequestDTO(
    val manufacturer: String,
)

@Serializable
data class UpdateModelMaterialsRequestDTO(
    val materials: List<String>,
)

@Serializable
data class CreateModelRequestDTO(
    val name: String,
    val manufacturer: String,
    val categoryId: String,
    val materials: List<String>,
    val notes: String? = null,
)

@Serializable
data class MaterialCatalogDTO(
    val code: String,
    val name: String,
    val recyclable: Boolean? = null,
    val hazardous: Boolean? = null,
    val disposalGuide: String? = null,
)
