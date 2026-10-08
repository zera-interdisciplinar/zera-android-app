package com.zera.android.model.usecase.auth

import com.zera.android.model.dto.auth.SingInRequestDTO
import com.zera.android.model.dto.user.SelfUserResponseDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.ApiClient
import com.zera.android.model.usecase.config.LoadFlags

class SingIn {
    private val getSelfUser = GetSelfUser()
    private val loadFlags = LoadFlags()

    suspend fun execute(email: String, password: String): SelfUserResponseDTO {
        val signInRequest = SingInRequestDTO(email, password)
        val response = ApiClient.authService.signIn(signInRequest)

        SqliteManager.saveSession(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            userId = response.userId,
            email = email,
            password = password,
        )

        return loadSignedInUser(response.userId)
    }

    suspend fun loadSignedInUser(userId: String): SelfUserResponseDTO {
        val selfUser = getSelfUser.execute(userId)
        SqliteManager.saveUnitId(selfUser.unitId)
        loadFlags.execute(selfUser)
        return selfUser
    }

    fun clearSession() {
        SqliteManager.clearSession()
        loadFlags.clear()
    }
}
