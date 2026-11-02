package com.zera.android.viewmodel.main.chat

import com.zera.android.model.dto.ai.ConversationSummaryDTO
import com.zera.android.view.theme.icons.ZeraIcon
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.UUID

class ChatConversationsMappingTest {
    @Test
    fun usesThreadIdAndOmitsClockWithoutTimestamp() {
        val row = ChatConversationsViewModel.rowFrom(
            ConversationSummaryDTO(threadId = "t-1", preview = "Quais os itens"),
        )
        assertEquals("t-1", row?.threadId)
        assertEquals("Quais os itens", row?.preview)
        assertNull(row?.timeLabel)
    }

    @Test
    fun fallsBackToIdWhenThreadIdIsBlank() {
        val row = ChatConversationsViewModel.rowFrom(
            ConversationSummaryDTO(id = "legacy-id", preview = "EcoPonto"),
        )
        assertEquals("legacy-id", row?.threadId)
    }

    @Test
    fun dropsRowsWithoutId() {
        assertNull(ChatConversationsViewModel.rowFrom(ConversationSummaryDTO(preview = "x")))
    }

    @Test
    fun keepsTheSameAvatarForTheSameThread() {
        val first = ChatConversationsViewModel.rowFrom(ConversationSummaryDTO(threadId = "t-1", preview = "a"))
        val second = ChatConversationsViewModel.rowFrom(ConversationSummaryDTO(threadId = "t-1", preview = "b"))
        assertEquals(first?.avatar, second?.avatar)
    }

    @Test
    fun distributesAllAvatarsAcrossThreads() {
        val avatars = (0 until 100)
            .map { ChatConversationsViewModel.avatarFor(UUID.nameUUIDFromBytes("t-$it".toByteArray()).toString()) }
            .toSet()
        assertEquals(
            setOf(ZeraIcon.ChatbotBase, ZeraIcon.ChatbotWink, ZeraIcon.ChatbotHappy, ZeraIcon.ChatbotDoubt),
            avatars,
        )
    }

    @Test
    fun formatsIsoTimestampAsLocalClock() {
        val label = ChatConversationsViewModel.clockLabel("2026-10-13T12:11:00Z")
        assertEquals(5, label?.length)
        assertEquals(':', label?.get(2))
    }
}
