package com.zera.android.viewmodel.shared

import com.zera.android.model.dto.ai.ConversationSummaryDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

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
    fun formatsIsoTimestampAsLocalClock() {
        val label = ChatConversationsViewModel.clockLabel("2026-10-13T12:11:00Z")
        assertEquals(5, label?.length)
        assertEquals(':', label?.get(2))
    }
}
