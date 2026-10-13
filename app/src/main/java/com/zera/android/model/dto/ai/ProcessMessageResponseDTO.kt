package com.zera.android.model.dto.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProcessMessageResponseDTO(
    val content: String,
    val blocked: Boolean = false,
    @SerialName("blocked_reason") val blockedReason: String? = null,
    @SerialName("agent_trace") val agentTrace: List<String> = emptyList(),
    @SerialName("report_url") val reportUrl: String? = null,
    @SerialName("redirect_to") val redirectTo: RedirectToDTO? = null,
)

@Serializable
data class RedirectToDTO(
    val screen: String? = null,
    val message: String? = null,
)
