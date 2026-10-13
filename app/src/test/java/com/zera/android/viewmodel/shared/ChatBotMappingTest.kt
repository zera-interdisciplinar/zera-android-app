package com.zera.android.viewmodel.shared

import com.zera.android.model.dto.ai.ConversationMessageDTO
import com.zera.android.model.dto.ai.ProcessMessageResponseDTO
import com.zera.android.model.dto.ai.RedirectToDTO
import com.zera.android.model.entity.chat.MessageRole
import com.zera.android.model.entity.user.UserRole
import com.zera.android.view.navigation.Route
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ChatBotMappingTest {
    @Test
    fun dropsSystemAndEmptyMessages() {
        val mapped = ChatBotViewModel.messagesFrom(
            listOf(
                ConversationMessageDTO(role = "system", content = "hidden"),
                ConversationMessageDTO(role = "user", content = "Oi"),
                ConversationMessageDTO(role = "assistant", content = "  "),
                ConversationMessageDTO(role = "assistant", content = "Olá"),
            ),
        )
        assertEquals(listOf(MessageRole.User, MessageRole.Assistant), mapped.map { it.role })
        assertEquals(listOf("Oi", "Olá"), mapped.map { it.content })
    }

    @Test
    fun blockedResponseStillBecomesAssistantMessage() {
        val message = ChatBotViewModel.assistantMessageFrom(
            ProcessMessageResponseDTO(content = "Não posso ajudar com isso.", blocked = true),
            UserRole.MANAGER,
        )
        assertEquals(MessageRole.Assistant, message.role)
        assertEquals("Não posso ajudar com isso.", message.content)
        assertNull(message.navigateTo)
    }

    @Test
    fun redirectIsAttachedWhenRoleAllows() {
        val message = ChatBotViewModel.assistantMessageFrom(
            ProcessMessageResponseDTO(
                content = "Baterias vão para pontos de coleta.",
                redirectTo = RedirectToDTO(screen = "Recycling", message = "Ver pontos próximos"),
            ),
            UserRole.EMPLOYEE,
        )
        assertEquals(Route.Recycling, message.navigateTo)
        assertEquals("Ver pontos próximos", message.navigateToLabel)
    }

    @Test
    fun redirectIsDroppedWhenRoleForbids() {
        val message = ChatBotViewModel.assistantMessageFrom(
            ProcessMessageResponseDTO(
                content = "Use o scanner.",
                redirectTo = RedirectToDTO(screen = "Scan", message = "Escanear"),
            ),
            UserRole.MANAGER,
        )
        assertNull(message.navigateTo)
        assertEquals("", message.navigateToLabel)
    }
}
