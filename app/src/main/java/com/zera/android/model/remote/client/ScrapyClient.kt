package com.zera.android.model.remote.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zera.android.BuildConfig
import com.zera.android.model.entity.config.Environments
import com.zera.android.model.entity.scrapy.FlagsRequest
import com.zera.android.model.remote.service.ScrapyService
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object ScrapyClient {
    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("apikey", BuildConfig.SCRAPY_API_KEY)
                .build()
            chain.proceed(request)
        }
        .build()

    private val scrapyService: ScrapyService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(normalizeBaseUrl(BuildConfig.SCRAPY_API_URL))
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        retrofit.create(ScrapyService::class.java)
    }

    suspend fun boot(): Environments = scrapyService.boot()

    suspend fun flags(attrs: Map<String, JsonElement> = emptyMap()): JsonObject =
        scrapyService.flags(FlagsRequest(attrs))

    private fun normalizeBaseUrl(url: String): String {
        val withoutTrailingSlash = url.trimEnd('/')
        val withVersion = if (withoutTrailingSlash.endsWith("/v1")) {
            withoutTrailingSlash
        } else {
            "$withoutTrailingSlash/v1"
        }
        return "$withVersion/"
    }
}
