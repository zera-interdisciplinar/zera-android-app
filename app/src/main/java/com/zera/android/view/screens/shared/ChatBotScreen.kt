package com.zera.android.view.screens.shared

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.chat.ChatScroll
import com.zera.android.view.components.chat.MessageTextBox
import com.zera.android.view.components.chat.SuggestionList
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.zeraColors
import com.zera.android.viewmodel.shared.ChatBotViewModel

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
            ChatScroll(
                messages = state.messages,
                loadingMessage = state.isLoadingMessage,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.medium, vertical = Spacing.small),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                if (state.messages.isEmpty()) {
                    SuggestionList(
                        suggestions = state.suggestions,
                        onSuggestionClick = viewModel::onSuggestionClick,
                    )
                }
                MessageTextBox(
                    value = state.input,
                    onValueChange = viewModel::onInputChange,
                    onSend = viewModel::onSend,
                    enabled = !state.isLoadingMessage,
                )
            }
        }
    }
}

@Composable
@Preview(heightDp = 900)
fun ChatBotScreenPreview() {
    ZeraTheme {
        ChatBotScreen(threadId = "preview-thread")
    }
}
