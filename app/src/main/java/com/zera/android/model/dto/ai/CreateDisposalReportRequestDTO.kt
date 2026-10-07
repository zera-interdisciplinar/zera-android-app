package com.zera.android.model.dto.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateDisposalReportRequestDTO(
    @SerialName("user_id") val userId: String,
    @SerialName("disposal_id") val disposalId: String,
)
