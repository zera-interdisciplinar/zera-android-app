package com.zera.android.model.dto.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConversationMessageDTO(
    val role: String,
    val content: String = "",
    @SerialName("created_at") val createdAt: String? = null,
)
