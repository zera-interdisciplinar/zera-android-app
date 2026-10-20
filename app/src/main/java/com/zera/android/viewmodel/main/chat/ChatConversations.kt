package com.zera.android.viewmodel.main.chat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.ai.ConversationSummaryDTO
import com.zera.android.model.usecase.ai.ListChatConversations
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

data class ConversationRowItem(
    val threadId: String,
    val preview: String,
    val timeLabel: String? = null,
)

data class ChatConversationsState(
    val items: List<ConversationRowItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class ChatConversationsViewModel(
    private val listConversations: ListChatConversations = ListChatConversations(),
) : ZeraViewModel() {
    private val _state = mutableStateOf(ChatConversationsState())
    val state = _state

    private var hasLoaded = false

    /** Chamada a cada retorno à tela; `isLoading` só sobe na primeira carga, as seguintes são silenciosas. */
    fun load() {
        _state.value = _state.value.copy(isLoading = !hasLoaded, errorMessage = null)
        viewModelScope.launch {
            try {
                val page = listConversations.execute()
                hasLoaded = true
                _state.value = ChatConversationsState(
                    items = page.items.mapNotNull(::rowFrom),
                    isLoading = false,
                )
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Não foi possível carregar as conversas.",
                )
            }
        }
    }

    fun onConversationClick(threadId: String) {
        ZeraNavigator.push(Route.ChatBot(threadId), ScreenAnimation.SlideHorizontal)
    }

    fun onNewConversationClick() {
        ZeraNavigator.push(Route.ChatBot(UUID.randomUUID().toString()), ScreenAnimation.SlideHorizontal)
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    companion object {
        private val clockFormat = DateTimeFormatter.ofPattern("HH:mm")

        fun rowFrom(dto: ConversationSummaryDTO): ConversationRowItem? {
            val threadId = dto.resolvedThreadId().trim()
            if (threadId.isEmpty()) return null
            return ConversationRowItem(
                threadId = threadId,
                preview = dto.preview.trim(),
                timeLabel = clockLabel(dto.lastMessageAt ?: dto.updatedAt ?: dto.createdAt),
            )
        }

        fun clockLabel(raw: String?): String? {
            if (raw.isNullOrBlank()) return null
            val instant = runCatching { Instant.parse(raw) }.getOrNull()
                ?: runCatching { OffsetDateTime.parse(raw).toInstant() }.getOrNull()
                ?: return null
            return instant.atZone(ZoneId.systemDefault()).toLocalTime().format(clockFormat)
        }
    }
}
