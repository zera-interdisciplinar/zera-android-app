package com.zera.android.model.usecase.auth

import com.zera.android.model.dto.user.SelfUserResponseDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AdmCoreClient

class GetSelfUser {
    suspend fun execute(userId: String): SelfUserResponseDTO {
        return AdmCoreClient.selfUserService.getSelfUser(userId)
    }

    suspend fun execute(): SelfUserResponseDTO {
        val userId = SqliteManager.getUserId()
            ?: error("Não há usuário logado")
        return execute(userId)
    }
}
