package com.zera.android.model.usecase.config

import com.zera.android.model.config.AppConfig
import com.zera.android.model.remote.client.ScrapyClient
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class LoadFlags {
    suspend fun execute(attrs: Map<String, JsonElement> = emptyMap()): JsonObject {
        val flags = ScrapyClient.flags(attrs)
        AppConfig.updateFlags(flags)
        return flags
    }
}
