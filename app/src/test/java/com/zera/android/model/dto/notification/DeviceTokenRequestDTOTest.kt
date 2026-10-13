package com.zera.android.model.dto.notification

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class DeviceTokenRequestDTOTest {
    private val json = Json { encodeDefaults = false }

    @Test
    fun registerPayloadIncludesAndroidPlatform() {
        val payload = json.encodeToString(
            DeviceTokenRequestDTO.serializer(),
            DeviceTokenRequestDTO(token = "fcm-token", platform = "ANDROID"),
        )
        assertEquals("""{"token":"fcm-token","platform":"ANDROID"}""", payload)
    }

    @Test
    fun deletePayloadOmitsPlatform() {
        val payload = json.encodeToString(
            DeviceTokenRequestDTO.serializer(),
            DeviceTokenRequestDTO(token = "fcm-token"),
        )
        assertEquals("""{"token":"fcm-token"}""", payload)
        assertFalse(payload.contains("platform"))
    }
}
