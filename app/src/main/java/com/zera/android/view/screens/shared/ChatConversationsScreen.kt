package com.zera.android.view.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.chat.ConversationRow
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.zeraColors
import com.zera.android.viewmodel.shared.ChatConversationsViewModel

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

    Scaffold(
        containerColor = MaterialTheme.zeraColors.brandContainer,
        topBar = {
            UpperNavBar(
                title = "Conversas",
                goBack = true,
                showActions = false,
                backgroundVariant = true,
                variantContentColor = MaterialTheme.zeraColors.onBrandContainer,
                onBackClick = viewModel::onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.small),
            )
        },
        floatingActionButton = {
            IconButton(
                icon = ZeraIcon.Plus,
                onClick = viewModel::onNewConversationClick,
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
                                onClick = { viewModel.onConversationClick(item.threadId) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(heightDp = 900)
private fun ChatConversationsScreenPreview() {
    ZeraTheme {
        ChatConversationsScreen()
    }
}
