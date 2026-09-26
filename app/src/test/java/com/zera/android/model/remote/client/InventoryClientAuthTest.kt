package com.zera.android.model.remote.client

import com.zera.android.model.entity.config.Environments
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InventoryClientAuthTest {
    private val original = Request.Builder()
        .url("https://inventory.example.com/api/v1/dashboard/home")
        .build()

    @Test
    fun sendsApiKeyBearerAndUnitId() {
        val request = InventoryClient.authenticatedRequest(
            original = original,
            apiKey = "inv-key",
            accessToken = "user-token",
            unitId = "unit-1",
        )

        assertEquals("inv-key", request.header("apiKey"))
        assertEquals("Bearer user-token", request.header("Authorization"))
        assertEquals("unit-1", request.header("X-Unit-Id"))
    }

    @Test
    fun omitsBearerAndUnitWhenSessionIsMissing() {
        val request = InventoryClient.authenticatedRequest(
            original = original,
            apiKey = "inv-key",
            accessToken = null,
            unitId = null,
        )

        assertEquals("inv-key", request.header("apiKey"))
        assertNull(request.header("Authorization"))
        assertNull(request.header("X-Unit-Id"))
    }

    @Test
    fun usesInventoryApiKeyWhenPresent() {
        val environments = Environments(
            admCoreApiUrl = "https://adm.example.com",
            admCoreApiKey = "adm-key",
            inventoryApiUrl = "https://inventory.example.com",
            inventoryApiKey = "inv-key",
        )
        assertEquals("inv-key", InventoryClient.resolveApiKey(environments))
    }

    @Test
    fun fallsBackToAdmCoreApiKeyWhenInventoryKeyIsBlank() {
        val environments = Environments(
            admCoreApiUrl = "https://adm.example.com",
            admCoreApiKey = "adm-key",
            inventoryApiUrl = "https://inventory.example.com",
        )
        assertEquals("adm-key", InventoryClient.resolveApiKey(environments))
    }
}
