package com.zera.android.view.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.model.entity.chat.Message
import com.zera.android.model.entity.chat.MessageRole
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import java.time.LocalDateTime

private const val WelcomeText = "Olá! Como posso ajudar você hoje?"

/**
 * Lista rolável com todas as mensagens do chat.
 *
 * As mensagens são desenhadas na ordem recebida e a lista rola sozinha para o fim sempre
 * que uma mensagem nova (ou o [loadingMessage]) aparece, então ela pode continuar se
 * acumulando. Quando [messages] está vazia, mostra uma mensagem de boas-vindas do assistente.
 *
 * @param messages mensagens da conversa, da mais antiga para a mais recente.
 * @param modifier modificador externo opcional.
 * @param loadingMessage quando `true`, exibe o [LoadingMessageContainer] depois da última
 *   mensagem — ele sempre vem por último.
 * @param contentPadding espaçamento interno da lista.
 */
@Composable
fun ChatScroll(
    modifier: Modifier = Modifier,
    messages: List<Message> = emptyList(),
    loadingMessage: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(Spacing.medium),
) {
    val listState = rememberLazyListState()
    val welcomeMessage = remember {
        Message(role = MessageRole.Assistant, content = WelcomeText, sentAt = LocalDateTime.now())
    }
    val displayedMessages = messages.ifEmpty { listOf(welcomeMessage) }
    val itemCount = displayedMessages.size + if (loadingMessage) 1 else 0

    LaunchedEffect(itemCount) {
        listState.animateScrollToItem(itemCount - 1)
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        items(displayedMessages) { message ->
            // TODO: extrair a rota/rótulo de navegação do conteúdo quando o back passar a enviá-los.
            MessageContainer(role = message.role, content = message.content)
        }
        if (loadingMessage) {
            item { LoadingMessageContainer() }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScrollPreview() {
    ZeraTheme {
        ChatScroll(
            messages = listOf(
                Message(MessageRole.Assistant, "Olá! Como posso ajudar você hoje?", LocalDateTime.now()),
                Message(MessageRole.User, "Onde descarto uma bateria de notebook?", LocalDateTime.now()),
                Message(
                    MessageRole.Assistant,
                    "Baterias devem ir para pontos de coleta de eletrônicos ou pilhas. Não descarte no lixo comum.",
                    LocalDateTime.now(),
                ),
            ),
            loadingMessage = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScrollEmptyPreview() {
    ZeraTheme {
        ChatScroll()
    }
}
