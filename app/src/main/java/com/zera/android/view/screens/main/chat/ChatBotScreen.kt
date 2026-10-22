package com.zera.android.view.screens.main.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.model.entity.chat.Message
import com.zera.android.model.entity.chat.MessageRole
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.chat.ChatScroll
import com.zera.android.view.components.chat.MessageTextBox
import com.zera.android.view.components.chat.SuggestionList
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.zeraColors
import com.zera.android.viewmodel.main.chat.ChatBotState
import com.zera.android.viewmodel.main.chat.ChatBotViewModel
import java.time.LocalDateTime

@Composable
fun ChatBotScreen(
    threadId: String,
    viewModel: ChatBotViewModel = viewModel(key = threadId) {
        ChatBotViewModel(threadId)
    },
) {
    val state by viewModel.state

    Scaffold(
        containerColor = MaterialTheme.zeraColors.brandContainer,
        topBar = {
            UpperNavBar(
                title = "Zé Bot",
                goBack = true,
                showActions = false,
                backgroundVariant = true,
                variantContentColor = MaterialTheme.zeraColors.onBrandContainer,
                onBackClick = viewModel::onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.small),
                actions = {
                    IconButton(
                        icon = ZeraIcon.Clock,
                        onClick = viewModel::onHistoryClick,
                        contentDescription = "Histórico de conversas",
                        type = ZeraButtonType.Base,
                        size = 34.dp,
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = Radius.xLarge, topEnd = Radius.xLarge),
                )
                .navigationBarsPadding()
                .imePadding(),
        ) {
            // Só o histórico alterna: a barra de digitação continua real durante o carregamento.
            if (state.isLoading) {
                ChatHistorySkeleton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            } else {
                ChatScroll(
                    messages = state.messages,
                    loadingMessage = state.isSendingMessage,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.medium, vertical = Spacing.small),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                // As sugestões esperam o histórico: com a conversa ainda por chegar, a lista está vazia.
                if (!state.isLoading && state.messages.isEmpty()) {
                    SuggestionList(
                        suggestions = state.suggestions,
                        onSuggestionClick = viewModel::onSuggestionClick,
                    )
                }
                MessageTextBox(
                    value = state.input,
                    onValueChange = viewModel::onInputChange,
                    onSend = viewModel::onSend,
                    enabled = !state.isSendingMessage,
                )
            }
        }
    }
}

/** Esqueleto do histórico: bolhas alternadas do assistente (esquerda) e do usuário (direita). */
@Composable
private fun ChatHistorySkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clipToBounds()
            .padding(Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        SkeletonBox(width = 240.dp, height = 72.dp, borderRadius = Radius.large)
        SkeletonBox(
            modifier = Modifier.align(Alignment.End),
            width = 180.dp,
            height = 44.dp,
            borderRadius = Radius.large,
        )
        SkeletonBox(width = 280.dp, height = 96.dp, borderRadius = Radius.large)
        SkeletonBox(
            modifier = Modifier.align(Alignment.End),
            width = 140.dp,
            height = 44.dp,
            borderRadius = Radius.large,
        )
    }
}

@Composable
@Preview(heightDp = 900)
fun ChatBotScreenPreview() {
    val previewViewModel = ChatBotViewModel(threadId = "preview-thread").apply {
        state.value = ChatBotState(
            messages = listOf(
                Message(MessageRole.User, "Como cadastrar um produto?", LocalDateTime.now()),
                Message(
                    MessageRole.Assistant,
                    "Toque em Escanear na tela inicial ou use Adicionar novo item para cadastrar manualmente.",
                    LocalDateTime.now(),
                ),
            ),
            isLoading = false,
        )
    }
    ZeraTheme {
        ChatBotScreen(threadId = "preview-thread", viewModel = previewViewModel)
    }
}

/** A mesma tela com o histórico em andamento (`isLoading` de [ChatBotState] nasce `true`). */
@Composable
@Preview(heightDp = 900)
fun ChatBotScreenLoadingPreview() {
    val previewViewModel = ChatBotViewModel(threadId = "preview-thread").apply {
        state.value = ChatBotState()
    }
    ZeraTheme {
        ChatBotScreen(threadId = "preview-thread", viewModel = previewViewModel)
    }
}
