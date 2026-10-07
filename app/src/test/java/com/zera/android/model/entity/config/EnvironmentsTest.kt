package com.zera.android.model.entity.config

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class EnvironmentsTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesInventoryUrlFromBootPayload() {
        val payload = """
            {
              "ms-adm-core-url": "https://adm.example.com",
              "ms-adm-core-api-key": "adm-key",
              "ms-inventory-url": "https://inventory.example.com"
            }
        """.trimIndent()

        val environments = json.decodeFromString<Environments>(payload)

        assertEquals("https://adm.example.com", environments.admCoreApiUrl)
        assertEquals("adm-key", environments.admCoreApiKey)
        assertEquals("https://inventory.example.com", environments.inventoryApiUrl)
        assertEquals("", environments.inventoryApiKey)
    }

    @Test
    fun parsesInventoryApiKeyWhenPresent() {
        val payload = """
            {
              "ms-adm-core-url": "https://adm.example.com",
              "ms-adm-core-api-key": "adm-key",
              "ms-inventory-url": "https://inventory.example.com",
              "ms-inventory-api-key": "inv-key"
            }
        """.trimIndent()

        val environments = json.decodeFromString<Environments>(payload)

        assertEquals("inv-key", environments.inventoryApiKey)
        assertEquals("", environments.supabaseUrl)
    }

    @Test
    fun parsesSupabaseFieldsWhenPresent() {
        val payload = """
            {
              "ms-adm-core-url": "https://adm.example.com",
              "ms-adm-core-api-key": "adm-key",
              "ms-inventory-url": "https://inventory.example.com",
              "supabase-url": "https://abc.supabase.co",
              "supabase-anon-key": "anon-key",
              "supabase-avatars-bucket": "avatars"
            }
        """.trimIndent()

        val environments = json.decodeFromString<Environments>(payload)

        assertEquals("https://abc.supabase.co", environments.supabaseUrl)
        assertEquals("anon-key", environments.supabaseAnonKey)
        assertEquals("avatars", environments.supabaseAvatarsBucket)
        assertEquals("", environments.aiApiUrl)
        assertEquals("", environments.aiApiKey)
    }

    @Test
    fun parsesAiFieldsWhenPresent() {
        val payload = """
            {
              "ms-adm-core-url": "https://adm.example.com",
              "ms-adm-core-api-key": "adm-key",
              "ms-inventory-url": "https://inventory.example.com",
              "ms-ai-url": "https://ai.example.com",
              "ms-ai-api-key": "ai-key"
            }
        """.trimIndent()

        val environments = json.decodeFromString<Environments>(payload)

        assertEquals("https://ai.example.com", environments.aiApiUrl)
        assertEquals("ai-key", environments.aiApiKey)
    }
}
