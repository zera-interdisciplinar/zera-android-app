package com.zera.android.model.remote.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zera.android.model.local.SharedPreferencesManager
import com.zera.android.model.remote.service.AuthService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import com.zera.android.model.remote.service.SelfUserService
import com.zera.android.model.remote.service.InvitationService

object ApiClient {
    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
                .addHeader("apiKey", "zera1405")

            val accessToken = SharedPreferencesManager.getAccessToken()
            if (!accessToken.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $accessToken")
            }

            chain.proceed(requestBuilder.build())
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://35.247.253.238/qa/administrative/api/v1/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    // service for auth
    val authService: AuthService by lazy { retrofit.create(AuthService::class.java) }
    
    // service for self user
    val selfUserService: SelfUserService by lazy { retrofit.create(SelfUserService::class.java) }
    
    // service for invitation
    val invitationService: InvitationService by lazy { retrofit.create(InvitationService::class.java) }
}
