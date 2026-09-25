package com.zera.android.config.scrapy

import com.zera.android.config.Environments
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

interface ScrapyService {
    @POST("boot")
    suspend fun boot(): Environments

    @POST("flags")
    suspend fun flags(@Body request: FlagsRequest): JsonObject
}
