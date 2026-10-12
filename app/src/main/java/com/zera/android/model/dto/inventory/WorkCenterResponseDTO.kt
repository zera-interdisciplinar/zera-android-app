package com.zera.android.model.dto.inventory

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class PendingItemResponseDTO(
    val id: String,
    val displayCode: String? = null,
    val name: String,
    val modelName: String? = null,
    @OptIn(ExperimentalSerializationApi::class)
    @JsonNames("missing_fields", "incompleteFields", "incomplete_fields")
    val missingFields: List<String> = emptyList(),
    val rejectionReason: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class WorkCenterResponseDTO(
    val drafts: List<PendingItemResponseDTO> = emptyList(),
    val rejected: List<PendingItemResponseDTO> = emptyList(),
    val damagedWithoutDestination: List<PendingItemResponseDTO> = emptyList(),
    val inMaintenance: Long = 0,
    val awaitingEvaluation: Long = 0,
)
