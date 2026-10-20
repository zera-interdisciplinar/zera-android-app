package com.zera.android.view.screens.main.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.chat.ConversationRow
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.zeraColors
import com.zera.android.viewmodel.main.chat.ChatConversationsState
import com.zera.android.viewmodel.main.chat.ChatConversationsViewModel
import com.zera.android.viewmodel.main.chat.ConversationRowItem

private val SkeletonAvatarSize = 40.dp
private const val SkeletonRowCount = 7

@Composable
fun ChatConversationsScreen(
    viewModel: ChatConversationsViewModel = viewModel(),
) {
    val state by viewModel.state
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.load()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    ChatConversationsContent(
        state = state,
        onConversationClick = viewModel::onConversationClick,
        onNewConversationClick = viewModel::onNewConversationClick,
        onBackClick = viewModel::onBackClick,
    )
}

/**
 * Conteúdo da tela sem o [ChatConversationsViewModel], para as previews não dispararem o `load()`
 * do retorno à tela (que reativaria `isLoading` e esconderia o estado que se quer ver).
 */
@Composable
private fun ChatConversationsContent(
    state: ChatConversationsState,
    onConversationClick: (String) -> Unit,
    onNewConversationClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.zeraColors.brandContainer,
        topBar = {
            UpperNavBar(
                title = "Conversas",
                goBack = true,
                showActions = false,
                backgroundVariant = true,
                variantContentColor = MaterialTheme.zeraColors.onBrandContainer,
                onBackClick = onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.small),
            )
        },
        floatingActionButton = {
            IconButton(
                icon = ZeraIcon.Plus,
                onClick = onNewConversationClick,
                contentDescription = "Nova conversa",
                size = 56.dp,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = Radius.xLarge, topEnd = Radius.xLarge),
                ),
        ) {
            when {
                state.isLoading -> {
                    ChatConversationsSkeleton()
                }
                state.errorMessage != null && state.items.isEmpty() -> {
                    BodyText(
                        text = state.errorMessage.orEmpty(),
                        modifier = Modifier.padding(Spacing.medium),
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.items, key = { it.threadId }) { item ->
                            ConversationRow(
                                preview = item.preview,
                                timeLabel = item.timeLabel,
                                onClick = { onConversationClick(item.threadId) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Esqueleto das linhas de conversa: avatar do assistente e uma linha de texto. */
@Composable
private fun ChatConversationsSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds(),
    ) {
        repeat(SkeletonRowCount) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.medium, vertical = Spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                SkeletonBox(
                    width = SkeletonAvatarSize,
                    height = SkeletonAvatarSize,
                    borderRadius = SkeletonAvatarSize,
                )
                SkeletonBox(modifier = Modifier.weight(1f), height = 16.dp)
            }
        }
    }
}

@Composable
@Preview(heightDp = 900)
private fun ChatConversationsScreenPreview() {
    ZeraTheme {
        ChatConversationsContent(
            state = ChatConversationsState(
                items = listOf(
                    ConversationRowItem(threadId = "1", preview = "Como cadastrar um produto?", timeLabel = "14:32"),
                    ConversationRowItem(threadId = "2", preview = "Falar com o suporte", timeLabel = "09:10"),
                    ConversationRowItem(threadId = "3", preview = "", timeLabel = null),
                ),
                isLoading = false,
            ),
            onConversationClick = {},
            onNewConversationClick = {},
            onBackClick = {},
        )
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [ChatConversationsState] nasce `true`). */
@Composable
@Preview(heightDp = 900)
private fun ChatConversationsScreenLoadingPreview() {
    ZeraTheme {
        ChatConversationsContent(
            state = ChatConversationsState(),
            onConversationClick = {},
            onNewConversationClick = {},
            onBackClick = {},
        )
    }
}
