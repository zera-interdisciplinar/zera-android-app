package com.zera.android.config

import com.zera.android.BuildConfig
import com.zera.android.config.scrapy.ScrapyApi
import com.zera.android.model.remote.client.ApiClient
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object SetupApp {
    lateinit var environments: Environments
        private set

    var flags: JsonObject = JsonObject(emptyMap())
        private set

    private val scrapyApi = ScrapyApi(
        apiKey = BuildConfig.SCRAPY_API_KEY,
        baseUrl = BuildConfig.SCRAPY_API_URL,
    )

    suspend fun setup(): Environments {
        scrapyApi.setup()
        environments = scrapyApi.boot()
        ApiClient.init(environments)
        return environments
    }

    suspend fun loadFlags(attrs: Map<String, JsonElement> = emptyMap()): JsonObject {
        flags = scrapyApi.flags(attrs)
        return flags
    }
}
