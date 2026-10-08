package com.zera.android.model.remote.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zera.android.model.entity.config.Environments
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.service.ReportsService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit

object AiClient {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

    private lateinit var retrofit: Retrofit
    private var reports: ReportsService? = null

    fun init(environments: Environments) {
        val url = environments.aiApiUrl.trim()
        if (url.isEmpty()) return
        val apiKey = resolveApiKey(environments)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    authenticatedRequest(
                        original = chain.request(),
                        apiKey = apiKey,
                        accessToken = SqliteManager.getAccessToken(),
                    )
                )
            }
            .authenticator(SessionRefresh.authenticator)
            .zeraTimeouts()
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(InventoryClient.apiBaseUrl(url))
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        reports = retrofit.create(ReportsService::class.java)
    }

    val reportsService: ReportsService
        get() = reports ?: error("URL da API de IA não configurada no boot.")

    internal fun resolveApiKey(environments: Environments): String =
        environments.aiApiKey.ifBlank { environments.admCoreApiKey }

    internal fun authenticatedRequest(
        original: Request,
        apiKey: String,
        accessToken: String?,
    ): Request {
        val requestBuilder = original.newBuilder()
            .header("apiKey", apiKey)

        if (!accessToken.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        return requestBuilder.build()
    }
}
