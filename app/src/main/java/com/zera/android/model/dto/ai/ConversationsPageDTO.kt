package com.zera.android.model.dto.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConversationsPageDTO(
    val items: List<ConversationSummaryDTO> = emptyList(),
    val page: Int = 1,
    @SerialName("page_size") val pageSize: Int = 20,
    val total: Int = 0,
)

@Serializable
data class ConversationSummaryDTO(
    @SerialName("thread_id") val threadId: String = "",
    val id: String = "",
    val preview: String = "",
    @SerialName("last_message_at") val lastMessageAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
) {
    fun resolvedThreadId(): String = threadId.ifBlank { id }
}
