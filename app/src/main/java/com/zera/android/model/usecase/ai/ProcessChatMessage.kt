package com.zera.android.model.usecase.ai

import com.zera.android.model.dto.ai.ProcessMessageRequestDTO
import com.zera.android.model.dto.ai.ProcessMessageResponseDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AiClient
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.util.UUID

class ProcessChatMessage(
    private val send: suspend (ProcessMessageRequestDTO) -> ProcessMessageResponseDTO = {
        AiClient.chatService.processMessage(it)
    },
    private val userId: () -> String? = { SqliteManager.getUserId() },
    private val unitId: () -> String? = { SqliteManager.getUnitId() },
) {
    suspend fun execute(threadId: String, content: String): ProcessMessageResponseDTO {
        val user = userId()?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException(GENERIC_FAILURE)
        val unit = unitId()?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException(GENERIC_FAILURE)
        requireValidUuid(threadId)
        requireValidUuid(user)
        requireValidUuid(unit)
        return try {
            send(
                ProcessMessageRequestDTO(
                    userId = user,
                    threadId = threadId,
                    unitId = unit,
                    content = content,
                ),
            )
        } catch (error: CancellationException) {
            throw error
        } catch (error: HttpException) {
            throw IllegalStateException(GENERIC_FAILURE, error)
        } catch (error: Exception) {
            throw IllegalStateException(GENERIC_FAILURE, error)
        }
    }

    private fun requireValidUuid(value: String) {
        try {
            UUID.fromString(value.trim())
        } catch (_: IllegalArgumentException) {
            throw IllegalStateException(GENERIC_FAILURE)
        }
    }

    companion object {
        const val GENERIC_FAILURE = "Não foi possível obter a resposta. Tente de novo."
    }
}
