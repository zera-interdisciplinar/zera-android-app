package com.zera.android.model.usecase.config

import com.zera.android.BuildConfig
import com.zera.android.model.config.AppConfig
import com.zera.android.model.entity.user.SelfUserResponseDTO
import com.zera.android.model.remote.client.ScrapyClient
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class LoadFlags {
    suspend fun execute(selfUser: SelfUserResponseDTO): JsonObject {
        val flags = ScrapyClient.flags(
            mapOf(
                "user_id" to JsonPrimitive(selfUser.userId),
                "role" to JsonPrimitive(selfUser.role),
                "unit_id" to JsonPrimitive(selfUser.unitId),
                "app_version" to JsonPrimitive(BuildConfig.VERSION_NAME),
            ),
        )
        AppConfig.updateFlags(flags)
        return flags
    }

    fun clear() {
        AppConfig.clearFlags()
    }
}
