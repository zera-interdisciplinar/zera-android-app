package com.zera.android.model.usecase.user

import com.zera.android.model.dto.user.RenameUserRequestDTO
import com.zera.android.model.local.SharedPreferencesManager
import com.zera.android.model.remote.client.ApiClient
import retrofit2.HttpException

class RenameUser {
    suspend fun execute(name: String) {
        val userId = SharedPreferencesManager.getUserId()
            ?: error("Não há usuário logado")
        val response = ApiClient.selfUserService.rename(userId, RenameUserRequestDTO(name))
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }
}
