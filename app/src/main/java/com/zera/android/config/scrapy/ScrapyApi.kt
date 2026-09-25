package com.zera.android.config.scrapy

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zera.android.config.Environments
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class ScrapyApi(
    private val apiKey: String,
    private val baseUrl: String,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var retrofit: Retrofit
    private lateinit var scrapyService: ScrapyService
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("apikey", apiKey)
                .build()
            chain.proceed(request)
        }
        .build()

    fun setup() {
        val normalizedBaseUrl = normalizeBaseUrl(baseUrl)
        retrofit = Retrofit.Builder()
            .baseUrl(normalizedBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        scrapyService = retrofit.create(ScrapyService::class.java)
    }

    suspend fun boot(): Environments {
        return scrapyService.boot()
    }

    suspend fun flags(attrs: Map<String, JsonElement> = emptyMap()): JsonObject {
        return scrapyService.flags(FlagsRequest(attrs))
    }

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
