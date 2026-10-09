package com.zera.android.model.remote.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zera.android.model.entity.config.Environments
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.service.InventoryService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit

object InventoryClient {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

    private lateinit var retrofit: Retrofit

    fun init(environments: Environments) {
        val apiKey = resolveApiKey(environments)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    authenticatedRequest(
                        original = chain.request(),
                        apiKey = apiKey,
                        accessToken = SqliteManager.getAccessToken(),
                        unitId = SqliteManager.getUnitId(),
                    )
                )
            }
            .authenticator(SessionRefresh.authenticator)
            .zeraTimeouts()
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(serviceBaseUrl(environments.inventoryApiUrl, stripApiV1 = true))
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val inventoryService: InventoryService by lazy { retrofit.create(InventoryService::class.java) }

    internal fun resolveApiKey(environments: Environments): String =
        environments.inventoryApiKey.ifBlank { environments.admCoreApiKey }

    internal fun authenticatedRequest(
        original: Request,
        apiKey: String,
        accessToken: String?,
        unitId: String?,
    ): Request {
        val requestBuilder = original.newBuilder()
            .header("apiKey", apiKey)

        if (!accessToken.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        if (!unitId.isNullOrBlank()) {
            requestBuilder.header("X-Unit-Id", unitId)
        }

        return requestBuilder.build()
    }
}
