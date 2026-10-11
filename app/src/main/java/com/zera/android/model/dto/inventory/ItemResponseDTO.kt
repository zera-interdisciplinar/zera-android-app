package com.zera.android.model.dto.inventory

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames

@Serializable
data class ItemResponseDTO(
    val id: String,
    val name: String,
    val barcode: String? = null,
    val displayCode: String? = null,
    @OptIn(ExperimentalSerializationApi::class)
    @JsonNames("itemStatus", "approvalStatus")
    val status: String? = null,
    val condition: String? = null,
    val serialNumber: String? = null,
    val notes: String? = null,
    val photoUrl: String? = null,
    @OptIn(ExperimentalSerializationApi::class)
    @JsonNames("created_by_name", "creatorName", "authorName", "registeredBy")
    val createdByName: String? = null,
    @OptIn(ExperimentalSerializationApi::class)
    @JsonNames("created_by", "creator", "author")
    val createdBy: JsonElement? = null,
    val createdAt: String? = null,
    val model: ModelResponseDTO? = null,
    @OptIn(ExperimentalSerializationApi::class)
    @JsonNames("missing_fields", "incompleteFields", "incomplete_fields")
    val missingFields: List<String> = emptyList(),
)

@Serializable
data class ModelResponseDTO(
    val id: String? = null,
    val name: String? = null,
    val manufacturer: String? = null,
    val materials: List<MaterialResponseDTO> = emptyList(),
    val category: CategoryResponseDTO? = null,
    val notes: String? = null,
    val approvalStatus: String? = null,
)

@Serializable
data class MaterialResponseDTO(
    val id: String? = null,
    val code: String? = null,
    val name: String = "",
)
