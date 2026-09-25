package com.zera.android.model.usecase.auth

import com.zera.android.model.remote.client.ApiClient
import com.zera.android.model.entity.auth.SingInRequestDTO
import com.zera.android.model.local.SharedPreferencesManager
import com.zera.android.model.entity.user.SelfUserResponseDTO
import com.zera.android.config.SetupApp
import kotlinx.serialization.json.JsonPrimitive

class SingIn {
    suspend fun execute(email: String, password: String): SelfUserResponseDTO {
        val signInRequest = SingInRequestDTO(email, password)

        val response = ApiClient.authService.signIn(signInRequest)

        SharedPreferencesManager.saveAccessToken(response.accessToken)
        SharedPreferencesManager.saveRefreshToken(response.refreshToken)

        val selfUser = ApiClient.selfUserService.getSelfUser(response.userId)

        SetupApp.loadFlags(
            mapOf("user_id" to JsonPrimitive(selfUser.userId)),
        )

        return selfUser
    }
}