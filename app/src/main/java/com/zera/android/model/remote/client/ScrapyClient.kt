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
        .zeraTimeouts()
        .build()

    private val scrapyService: ScrapyService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(serviceBaseUrl(BuildConfig.SCRAPY_API_URL, stripApiV1 = false))
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        retrofit.create(ScrapyService::class.java)
    }

    suspend fun boot(): Environments = scrapyService.boot()

    suspend fun flags(attrs: Map<String, JsonElement> = emptyMap()): JsonObject =
        scrapyService.flags(FlagsRequest(attrs))
}
