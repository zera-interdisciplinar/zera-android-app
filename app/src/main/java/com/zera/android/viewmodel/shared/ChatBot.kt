package com.zera.android.viewmodel.shared

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.ai.ConversationMessageDTO
import com.zera.android.model.dto.ai.ProcessMessageResponseDTO
import com.zera.android.model.entity.chat.Message
import com.zera.android.model.entity.chat.MessageRole
import com.zera.android.model.entity.user.UserRole
import com.zera.android.model.usecase.ai.GetChatConversation
import com.zera.android.model.usecase.ai.ProcessChatMessage
import com.zera.android.model.usecase.auth.GetSelfUser
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class ChatBotState(
    val messages: List<Message> = emptyList(),
    val input: String = "",
    val isLoadingMessage: Boolean = false,
    val isLoadingHistory: Boolean = false,
    val suggestions: List<String> = listOf(
        "Como cadastrar um produto?",
        "Falar com o suporte",
    ),
)

class ChatBotViewModel(
    private val threadId: String,
    private val getConversation: GetChatConversation = GetChatConversation(),
    private val processChatMessage: ProcessChatMessage = ProcessChatMessage(),
    private val getSelfUser: GetSelfUser = GetSelfUser(),
) : ZeraViewModel() {
    private val _state = mutableStateOf(ChatBotState())
    val state = _state
    private var userRole: String = UserRole.MANAGER

    init {
        loadHistory()
        loadRole()
    }

    private fun loadRole() {
        viewModelScope.launch {
            runCatching { getSelfUser.execute() }
                .onSuccess { userRole = it.role }
        }
    }

    private fun loadHistory() {
        _state.value = _state.value.copy(isLoadingHistory = true)
        viewModelScope.launch {
            try {
                val messages = messagesFrom(getConversation.execute(threadId))
                _state.value = _state.value.copy(messages = messages, isLoadingHistory = false)
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _state.value = _state.value.copy(isLoadingHistory = false)
            }
        }
    }

    fun onHistoryClick() {
        ZeraNavigator.push(Route.ChatConversations, ScreenAnimation.SlideHorizontal)
    }

    fun onInputChange(newValue: String) {
        _state.value = _state.value.copy(input = newValue)
    }

    fun onSend() {
        send(state.value.input)
    }

    fun onSuggestionClick(suggestion: String) {
        send(suggestion)
    }

    private fun send(raw: String) {
        val text = raw.trim()
        if (text.isEmpty() || state.value.isLoadingMessage) return
        if (text.length > MAX_CONTENT) return
        val userMessage = Message(MessageRole.User, text, LocalDateTime.now())
        _state.value = _state.value.copy(
            messages = _state.value.messages + userMessage,
            input = "",
            isLoadingMessage = true,
        )
        viewModelScope.launch {
            try {
                val response = processChatMessage.execute(threadId, text)
                val role = runCatching { getSelfUser.execute().role }.getOrElse { userRole }
                userRole = role
                _state.value = _state.value.copy(
                    messages = _state.value.messages + assistantMessageFrom(response, role),
                    isLoadingMessage = false,
                )
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _state.value = _state.value.copy(
                    messages = _state.value.messages + Message(
                        role = MessageRole.Assistant,
                        content = ProcessChatMessage.GENERIC_FAILURE,
                        sentAt = LocalDateTime.now(),
                    ),
                    isLoadingMessage = false,
                )
            }
        }
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    companion object {
        private const val MAX_CONTENT = 8000

        fun messagesFrom(raw: List<ConversationMessageDTO>): List<Message> {
            val now = LocalDateTime.now()
            return raw.mapNotNull { dto ->
                val role = when (dto.role.lowercase()) {
                    "user" -> MessageRole.User
                    "assistant" -> MessageRole.Assistant
                    else -> return@mapNotNull null
                }
                val content = dto.content.trim()
                if (content.isEmpty()) return@mapNotNull null
                Message(role = role, content = content, sentAt = now)
            }
        }

        fun assistantMessageFrom(
            response: ProcessMessageResponseDTO,
            role: String,
            sentAt: LocalDateTime = LocalDateTime.now(),
        ): Message {
            val redirect = ChatRedirect.resolve(
                screen = response.redirectTo?.screen,
                message = response.redirectTo?.message,
                role = role,
            )
            return Message(
                role = MessageRole.Assistant,
                content = response.content,
                sentAt = sentAt,
                navigateTo = redirect?.first,
                navigateToLabel = redirect?.second.orEmpty(),
            )
        }
    }
}
