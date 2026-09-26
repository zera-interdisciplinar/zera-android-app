package com.zera.android.model.usecase.auth

import com.zera.android.model.entity.auth.SingInRequestDTO
import com.zera.android.model.entity.user.SelfUserResponseDTO
import com.zera.android.model.local.SharedPreferencesManager
import com.zera.android.model.remote.client.ApiClient
import com.zera.android.model.usecase.config.LoadFlags

class SingIn {
    private val getSelfUser = GetSelfUser()
    private val loadFlags = LoadFlags()

    suspend fun execute(email: String, password: String): SelfUserResponseDTO {
        val signInRequest = SingInRequestDTO(email, password)
        val response = ApiClient.authService.signIn(signInRequest)

        SharedPreferencesManager.saveSession(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            userId = response.userId,
            email = email,
            password = password,
        )

        val selfUser = getSelfUser.execute(response.userId)
        loadFlags.execute(selfUser)
        return selfUser
    }

    fun clearSession() {
        SharedPreferencesManager.clearSession()
        loadFlags.clear()
    }
}
