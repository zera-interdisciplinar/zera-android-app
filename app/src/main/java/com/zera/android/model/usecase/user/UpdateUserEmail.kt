package com.zera.android.model.usecase.user

import com.zera.android.model.dto.user.UpdateEmailRequestDTO
import com.zera.android.model.local.SharedPreferencesManager
import com.zera.android.model.remote.client.ApiClient
import retrofit2.HttpException

class UpdateUserEmail {
    suspend fun execute(email: String) {
        val userId = SharedPreferencesManager.getUserId()
            ?: error("Não há usuário logado")
        val response = ApiClient.selfUserService.updateEmail(userId, UpdateEmailRequestDTO(email))
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }
}
