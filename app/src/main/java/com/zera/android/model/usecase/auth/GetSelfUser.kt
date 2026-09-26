package com.zera.android.model.usecase.auth

import com.zera.android.model.entity.user.SelfUserResponseDTO
import com.zera.android.model.remote.client.ApiClient

class GetSelfUser {
    suspend fun execute(userId: String): SelfUserResponseDTO {
        return ApiClient.selfUserService.getSelfUser(userId)
    }
}
