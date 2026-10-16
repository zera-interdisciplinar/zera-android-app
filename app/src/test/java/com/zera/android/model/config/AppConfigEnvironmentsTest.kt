package com.zera.android.model.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppConfigEnvironmentsTest {
    @Test
    fun decodesSavedBootPayload() {
        val raw = """
            {
              "ms-adm-core-url": "https://adm.example/",
              "ms-adm-core-api-key": "adm",
              "ms-inventory-url": "https://inv.example/",
              "ms-inventory-api-key": "inv"
            }
        """.trimIndent()

        val environments = AppConfig.decodeEnvironments(raw)

        assertEquals("https://adm.example/", environments?.admCoreApiUrl)
        assertEquals("inv", environments?.inventoryApiKey)
    }

    @Test
    fun ignoresCorruptPayload() {
        assertNull(AppConfig.decodeEnvironments("{"))
    }
}
