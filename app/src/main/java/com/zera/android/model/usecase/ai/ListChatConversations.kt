package com.zera.android.model.usecase.ai

import com.zera.android.model.dto.ai.ConversationsPageDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AiClient

class ListChatConversations(
    private val userId: () -> String? = { SqliteManager.getUserId() },
    private val fetch: suspend (String, Int, Int) -> ConversationsPageDTO = { id, page, pageSize ->
        AiClient.chatService.listConversations(userId = id, page = page, pageSize = pageSize)
    },
) {
    suspend fun execute(page: Int = 1, pageSize: Int = 20): ConversationsPageDTO {
        val id = userId()?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("Usuário não autenticado.")
        return fetch(id, page, pageSize)
    }
}
