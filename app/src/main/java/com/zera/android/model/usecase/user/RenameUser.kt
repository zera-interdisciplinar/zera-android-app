package com.zera.android.model.usecase.user

import com.zera.android.model.dto.user.RenameUserRequestDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AdmCoreClient
import retrofit2.HttpException

class RenameUser {
    suspend fun execute(name: String) {
        val userId = SqliteManager.getUserId()
            ?: error("Não há usuário logado")
        val response = AdmCoreClient.selfUserService.rename(userId, RenameUserRequestDTO(name))
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }
}
