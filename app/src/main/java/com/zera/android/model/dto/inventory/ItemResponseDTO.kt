package com.zera.android.model.dto.inventory

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class ItemResponseDTO(
    val id: String,
    val name: String,
    val displayCode: String? = null,
    @OptIn(ExperimentalSerializationApi::class)
    @JsonNames("itemStatus", "approvalStatus")
    val status: String? = null,
    val condition: String? = null,
    val serialNumber: String? = null,
    val notes: String? = null,
    val createdByName: String? = null,
    val createdAt: String? = null,
    val model: ModelResponseDTO? = null,
)

@Serializable
data class ModelResponseDTO(
    val id: String? = null,
    val name: String? = null,
    val manufacturer: String? = null,
    val materials: List<MaterialResponseDTO> = emptyList(),
    val category: CategoryResponseDTO? = null,
)

@Serializable
data class MaterialResponseDTO(
    val id: String? = null,
    val name: String,
)
