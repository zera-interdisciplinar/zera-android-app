package com.zera.android.model.usecase.user

import com.zera.android.model.dto.user.UpdateImageRequestDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AdmCoreClient
import retrofit2.HttpException

class UpdateUserImage {
    suspend fun execute(imageUrl: String) {
        val userId = SqliteManager.getUserId()
            ?: error("Não há usuário logado")
        val response = AdmCoreClient.selfUserService.updateImage(
            userId,
            UpdateImageRequestDTO(imageUrl),
        )
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }
}
