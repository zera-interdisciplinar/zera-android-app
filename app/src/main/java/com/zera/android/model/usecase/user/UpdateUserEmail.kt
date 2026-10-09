package com.zera.android.model.usecase.user

import com.zera.android.model.dto.user.UpdateEmailRequestDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AdmCoreClient
import retrofit2.HttpException

class UpdateUserEmail {
    suspend fun execute(email: String) {
        val userId = SqliteManager.getUserId()
            ?: error("Não há usuário logado")
        val response = AdmCoreClient.selfUserService.updateEmail(userId, UpdateEmailRequestDTO(email))
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }
}
