package com.zera.android.model.dto.ai

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ChatDtoTest {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = false }

    @Test
    fun processMessageRequestIsSnakeCaseWithUserIdLikeReports() {
        val payload = json.encodeToString(
            ProcessMessageRequestDTO.serializer(),
            ProcessMessageRequestDTO(
                userId = "user-1",
                threadId = "thread-1",
                unitId = "unit-1",
                content = "Oi",
            ),
        )
        assertEquals(
            """{"user_id":"user-1","thread_id":"thread-1","unit_id":"unit-1","content":"Oi"}""",
            payload,
        )
    }

    @Test
    fun processMessageResponseReadsRedirectTo() {
        val parsed = json.decodeFromString(
            ProcessMessageResponseDTO.serializer(),
            """{"content":"texto","blocked":false,"redirect_to":{"screen":"Scan","message":"Escanear"},"agent_trace":["faq"]}""",
        )
        assertEquals("texto", parsed.content)
        assertEquals("Scan", parsed.redirectTo?.screen)
        assertEquals("Escanear", parsed.redirectTo?.message)
    }

    @Test
    fun conversationsPageReadsEmptyItems() {
        val parsed = json.decodeFromString(
            ConversationsPageDTO.serializer(),
            """{"items":[],"page":1,"page_size":20,"total":0}""",
        )
        assertEquals(0, parsed.items.size)
        assertEquals(0, parsed.total)
    }

    @Test
    fun conversationsPageReadsPreview() {
        val parsed = json.decodeFromString(
            ConversationsPageDTO.serializer(),
            """{"items":[{"thread_id":"abc","preview":"Olá mundo","last_message_at":"2026-10-13T12:11:00Z"}],"page":1,"page_size":20,"total":1}""",
        )
        assertEquals("abc", parsed.items.single().resolvedThreadId())
        assertEquals("Olá mundo", parsed.items.single().preview)
        assertEquals("2026-10-13T12:11:00Z", parsed.items.single().lastMessageAt)
    }

    @Test
    fun redirectToMayBeNull() {
        val parsed = json.decodeFromString(
            ProcessMessageResponseDTO.serializer(),
            """{"content":"ok","blocked":true,"redirect_to":null}""",
        )
        assertNull(parsed.redirectTo)
        assertEquals(true, parsed.blocked)
    }
}
