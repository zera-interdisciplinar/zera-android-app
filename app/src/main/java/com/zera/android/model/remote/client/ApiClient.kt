package com.zera.android.model.remote.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zera.android.model.entity.config.Environments
import com.zera.android.model.local.SharedPreferencesManager
import com.zera.android.model.remote.service.AuthService
import com.zera.android.model.remote.service.InvitationService
import com.zera.android.model.remote.service.SelfUserService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object ApiClient {
    private val json = Json { ignoreUnknownKeys = true }

    private lateinit var retrofit: Retrofit

    fun init(environments: Environments) {
        val baseUrl = environments.admCoreApiUrl.let { url ->
            if (url.endsWith("/")) url else "$url/"
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .header("apiKey", environments.admCoreApiKey)

                val accessToken = SharedPreferencesManager.getAccessToken()
                if (!accessToken.isNullOrBlank()) {
                    requestBuilder.header("Authorization", "Bearer $accessToken")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val authService: AuthService by lazy { retrofit.create(AuthService::class.java) }

    val selfUserService: SelfUserService by lazy { retrofit.create(SelfUserService::class.java) }

    val invitationService: InvitationService by lazy { retrofit.create(InvitationService::class.java) }
}
