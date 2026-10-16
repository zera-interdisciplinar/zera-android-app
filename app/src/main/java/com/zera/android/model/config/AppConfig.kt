package com.zera.android.model.config

import com.zera.android.model.entity.config.Environments
import kotlinx.serialization.json.JsonObject

object AppConfig {
    lateinit var environments: Environments
        private set

    var flags: JsonObject = JsonObject(emptyMap())
        private set

    fun updateEnvironments(value: Environments) {
        environments = value
    }

    fun updateFlags(value: JsonObject) {
        flags = value
    }

    fun clearFlags() {
        flags = JsonObject(emptyMap())
    }
}
