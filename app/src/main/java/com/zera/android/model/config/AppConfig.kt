package com.zera.android.model.config

import com.zera.android.model.entity.config.Environments
import com.zera.android.model.local.SqliteManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

object AppConfig {
    private val json = Json { ignoreUnknownKeys = true }

    lateinit var environments: Environments
        private set

    var flags: JsonObject = JsonObject(emptyMap())
        private set

    fun updateEnvironments(value: Environments) {
        SqliteManager.saveEnvironments(json.encodeToString(Environments.serializer(), value))
    }

    fun loadEnvironments(): Environments? =
        SqliteManager.getEnvironments()?.let(::decodeEnvironments)

    internal fun decodeEnvironments(raw: String): Environments? =
        runCatching { json.decodeFromString<Environments>(raw) }.getOrNull()

    fun updateFlags(value: JsonObject) {
        flags = value
    }

    fun clearFlags() {
        flags = JsonObject(emptyMap())
    }
}
