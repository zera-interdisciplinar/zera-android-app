package com.zera.android.model.usecase.auth

import com.zera.android.model.entity.user.SelfUserResponseDTO
import com.zera.android.model.remote.client.ApiClient
import com.zera.android.model.usecase.config.LoadFlags
import kotlinx.serialization.json.JsonPrimitive

class GetSelfUser {
    private val loadFlags = LoadFlags()

    suspend fun execute(userId: String): SelfUserResponseDTO {
        val selfUser = ApiClient.selfUserService.getSelfUser(userId)
        loadFlags.execute(
            mapOf("user_id" to JsonPrimitive(selfUser.userId)),
        )
        return selfUser
    }
}
