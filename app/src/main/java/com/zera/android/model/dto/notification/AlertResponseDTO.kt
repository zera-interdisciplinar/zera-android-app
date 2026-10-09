package com.zera.android.model.dto.notification

import kotlinx.serialization.Serializable

@Serializable
data class AlertResponseDTO(
    val alertId: String,
    val kind: String,
    val severity: String,
    val status: String,
    val description: String,
    val unitId: String,
    val ruleId: String? = null,
    val eventId: String? = null,
    val occurredAt: String,
    val createdAt: String,
    val updatedAt: String,
)
