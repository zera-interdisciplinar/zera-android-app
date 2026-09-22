package com.zera.android.model.usecase.auth

import com.zera.android.model.remote.client.ApiClient
import com.zera.android.model.entity.auth.SingInRequestDTO
import com.zera.android.model.local.SharedPreferencesManager
import com.zera.android.model.entity.user.SelfUserResponseDTO
class SingIn {
    suspend fun execute(email: String, password: String): SelfUserResponseDTO {
        // build the sign in request
        val signInRequest = SingInRequestDTO(email, password)

        val response = ApiClient.authService.signIn(signInRequest)

        // save the access token and refresh token in shared preferences
        SharedPreferencesManager.saveAccessToken(response.accessToken)
        SharedPreferencesManager.saveRefreshToken(response.refreshToken)
        
        // get the self user to discover the user role and redirect to the correct screen
        val selfUser = ApiClient.selfUserService.getSelfUser(response.userId)

        return selfUser
    }
}