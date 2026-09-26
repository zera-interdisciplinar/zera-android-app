package com.zera.android.model.usecase.auth

import com.zera.android.model.dto.user.SelfUserResponseDTO
import com.zera.android.model.local.SharedPreferencesManager
import com.zera.android.model.remote.client.ApiClient

class GetSelfUser {
    suspend fun execute(userId: String): SelfUserResponseDTO {
        return ApiClient.selfUserService.getSelfUser(userId)
    }

    suspend fun execute(): SelfUserResponseDTO {
        val userId = SharedPreferencesManager.getUserId()
            ?: error("Não há usuário logado")
        return execute(userId)
    }
}
