package com.zera.android.model.remote.service

import com.zera.android.model.dto.ai.ConversationMessageDTO
import com.zera.android.model.dto.ai.ConversationsPageDTO
import com.zera.android.model.dto.ai.ProcessMessageRequestDTO
import com.zera.android.model.dto.ai.ProcessMessageResponseDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatService {
    @POST("api/v1/multi-agent/process-message")
    suspend fun processMessage(
        @Body body: ProcessMessageRequestDTO,
    ): ProcessMessageResponseDTO

    @GET("api/v1/multi-agent/conversations")
    suspend fun listConversations(
        @Query("user_id") userId: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
    ): ConversationsPageDTO

    @GET("api/v1/multi-agent/conversations/{threadId}")
    suspend fun getConversation(
        @Path("threadId") threadId: String,
        @Query("user_id") userId: String,
    ): List<ConversationMessageDTO>
}
