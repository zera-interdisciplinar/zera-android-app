package com.zera.android.view.screens.main.employees

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.feedback.ErrorFeedbackPopup
import com.zera.android.view.components.lists.InviteItem
import com.zera.android.view.components.lists.InviteList
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.main.employees.PendingInvitesState
import com.zera.android.viewmodel.main.employees.PendingInvitesViewModel

// Altura aproximada do card real; o esqueleto só indica a disposição.
private val SkeletonInviteHeight = 104.dp
private const val SkeletonInviteCount = 6

/** Todos os convites pendentes do gestor. Aberta pelo "Ver Todos" da tela de Colaboradores. */
@Composable
fun PendingInvitesScreen(
    viewModel: PendingInvitesViewModel = viewModel(),
) {
    val state by viewModel.state
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Todos os convites",
                goBack = true,
                showActions = false,
                onBackClick = viewModel::onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.small),
            )
        },
    ) { innerPadding ->
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Spacing.medium, vertical = Spacing.small)
                    .clipToBounds(),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                repeat(SkeletonInviteCount) {
                    SkeletonBox(height = SkeletonInviteHeight, borderRadius = Radius.large)
                }
            }
        } else {
            InviteList(
                invites = state.invites,
                onCopyCodeClick = { invite ->
                    clipboardManager.setText(AnnotatedString(invite.code))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = Spacing.medium, vertical = Spacing.small),
                emptyContent = {
                    if (!state.loadFailed) BodyText(text = "Nenhum convite pendente")
                },
            )
        }
    }

    state.errorMessage?.let { message ->
        ErrorFeedbackPopup(description = message, onDismissRequest = viewModel::onErrorDismiss)
    }
}

@Composable
@Preview(heightDp = 900)
private fun PendingInvitesScreenPreview() {
    val previewViewModel = PendingInvitesViewModel().apply {
        state.value = PendingInvitesState(
            invites = listOf(
                InviteItem(code = "743731", inviteeName = "Genivaldo da Silva", expiresInHours = 12),
                InviteItem(code = "443729", inviteeName = "Pedro José", expiresInHours = 107),
                InviteItem(code = "105183", inviteeName = "Bertozi", expiresInHours = 168),
            ),
            isLoading = false,
        )
    }
    ZeraTheme {
        PendingInvitesScreen(viewModel = previewViewModel)
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [PendingInvitesState] nasce `true`). */
@Composable
@Preview(heightDp = 900)
private fun PendingInvitesScreenLoadingPreview() {
    val previewViewModel = PendingInvitesViewModel().apply {
        state.value = PendingInvitesState()
    }
    ZeraTheme {
        PendingInvitesScreen(viewModel = previewViewModel)
    }
}
