package com.zera.android.model.remote.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zera.android.model.entity.config.Environments
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.service.AuthService
import com.zera.android.model.remote.service.InvitationService
import com.zera.android.model.remote.service.SelfUserService
import com.zera.android.model.remote.service.RecyclingPlacesService
import com.zera.android.model.remote.service.RecyclingsService
import com.zera.android.model.remote.service.TelephoneService
import com.zera.android.model.remote.service.UsersService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object ApiClient {
    private val json = Json { ignoreUnknownKeys = true }

    private lateinit var retrofit: Retrofit
    lateinit var refreshAuthService: AuthService
        private set

    fun init(environments: Environments) {
        val baseUrl = environments.admCoreApiUrl.let { url ->
            if (url.endsWith("/")) url else "$url/"
        }
        val converter = json.asConverterFactory("application/json".toMediaType())
        val refreshClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("apiKey", environments.admCoreApiKey)
                        .build(),
                )
            }
            .build()
        refreshAuthService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(refreshClient)
            .addConverterFactory(converter)
            .build()
            .create(AuthService::class.java)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .header("apiKey", environments.admCoreApiKey)

                val accessToken = SqliteManager.getAccessToken()
                if (!accessToken.isNullOrBlank()) {
                    requestBuilder.header("Authorization", "Bearer $accessToken")
                }

                chain.proceed(requestBuilder.build())
            }
            .authenticator(SessionRefresh.authenticator)
            .build()
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converter)
            .build()
    }

    val authService: AuthService by lazy { retrofit.create(AuthService::class.java) }

    val selfUserService: SelfUserService by lazy { retrofit.create(SelfUserService::class.java) }

    val invitationService: InvitationService by lazy { retrofit.create(InvitationService::class.java) }

    val usersService: UsersService by lazy { retrofit.create(UsersService::class.java) }

    val telephoneService: TelephoneService by lazy { retrofit.create(TelephoneService::class.java) }

    val recyclingPlacesService: RecyclingPlacesService by lazy {
        retrofit.create(RecyclingPlacesService::class.java)
    }

    val recyclingsService: RecyclingsService by lazy {
        retrofit.create(RecyclingsService::class.java)
    }
}
