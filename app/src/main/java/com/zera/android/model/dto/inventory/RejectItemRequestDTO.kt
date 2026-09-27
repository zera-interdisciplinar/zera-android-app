package com.zera.android.model.dto.inventory

import kotlinx.serialization.Serializable

@Serializable
data class RejectItemRequestDTO(
    val reason: String,
)
