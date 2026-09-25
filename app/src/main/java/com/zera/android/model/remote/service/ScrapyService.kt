package com.zera.android.model.remote.service

import com.zera.android.model.entity.config.Environments
import com.zera.android.model.entity.scrapy.FlagsRequest
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

interface ScrapyService {
    @POST("boot")
    suspend fun boot(): Environments

    @POST("flags")
    suspend fun flags(@Body request: FlagsRequest): JsonObject
}
