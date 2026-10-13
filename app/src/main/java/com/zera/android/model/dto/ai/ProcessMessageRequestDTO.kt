package com.zera.android.model.dto.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProcessMessageRequestDTO(
    @SerialName("user_id") val userId: String,
    @SerialName("thread_id") val threadId: String,
    @SerialName("unit_id") val unitId: String,
    val content: String,
)
