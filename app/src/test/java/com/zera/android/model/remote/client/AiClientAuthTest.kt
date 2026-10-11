package com.zera.android.model.remote.client

import com.zera.android.model.entity.config.Environments
import com.zera.android.model.usecase.ai.GenerateDisposalReport
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AiClientAuthTest {
    private val original = Request.Builder()
        .url("https://ai.example.com/api/v1/reports")
        .build()

    @Test
    fun sendsApiKeyAndBearer() {
        val request = AiClient.authenticatedRequest(
            original = original,
            apiKey = "ai-key",
            accessToken = "user-token",
        )

        assertEquals("ai-key", request.header("apiKey"))
        assertEquals("Bearer user-token", request.header("Authorization"))
    }

    @Test
    fun omitsBearerWhenSessionIsMissing() {
        val request = AiClient.authenticatedRequest(
            original = original,
            apiKey = "ai-key",
            accessToken = null,
        )

        assertEquals("ai-key", request.header("apiKey"))
        assertNull(request.header("Authorization"))
    }

    @Test
    fun fallsBackToAdmCoreApiKeyWhenAiKeyIsBlank() {
        val environments = Environments(
            admCoreApiUrl = "https://adm.example.com",
            admCoreApiKey = "adm-key",
            inventoryApiUrl = "https://inventory.example.com",
        )
        assertEquals("adm-key", AiClient.resolveApiKey(environments))
    }

    @Test
    fun reportForbiddenMeansUnitCannotBeResolved() {
        assertEquals(
            "A unidade deste usuário não pode gerar o relatório.",
            GenerateDisposalReport.failureMessage(403),
        )
        assertEquals(null, GenerateDisposalReport.failureMessage(422))
    }
}
