package com.zera.android.model.usecase.ai

import com.zera.android.model.dto.ai.ConversationMessageDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AiClient

class GetChatConversation(
    private val userId: () -> String? = { SqliteManager.getUserId() },
    private val fetch: suspend (String, String) -> List<ConversationMessageDTO> = { threadId, id ->
        AiClient.chatService.getConversation(threadId, id)
    },
) {
    suspend fun execute(threadId: String): List<ConversationMessageDTO> {
        val id = threadId.trim()
        if (id.isEmpty()) return emptyList()
        val user = userId()?.takeIf { it.isNotBlank() } ?: return emptyList()
        return fetch(id, user)
    }
}
