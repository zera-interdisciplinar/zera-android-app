package com.zera.android.view.screens.main.employees

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.cards.InviteCard
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.feedback.ErrorFeedbackPopup
import com.zera.android.view.components.lists.EmployeeItem
import com.zera.android.view.components.lists.EmployeeList
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.scaffolds.ManagerScaffold
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.main.employees.EmployeesState
import com.zera.android.viewmodel.main.employees.EmployeesViewModel
import com.zera.android.viewmodel.main.employees.PendingInviteCard

// Alturas aproximadas dos blocos reais; o esqueleto só indica a disposição.
private val SkeletonSummaryCardHeight = 88.dp
private val SkeletonEmployeeItemHeight = 72.dp
private const val SkeletonEmployeeCount = 4

@Composable
fun EmployeesScreen(
    viewModel: EmployeesViewModel = viewModel(),
) {
    val state by viewModel.state
    val clipboardManager = LocalClipboardManager.current

    LifecycleResumeEffect(Unit) {
        viewModel.onResume()
        onPauseOrDispose { }
    }

    ManagerScaffold(
        title = "Colaboradores",
        currentRoute = Route.Employees,
        onBackClick = viewModel::onBackClick,
    ) {
        if (state.isLoading) {
            EmployeesSkeleton()
        } else {
            ZeraBox(style = ZeraColorFamily.Blue) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.micro),
                    ) {
                        LabelText(text = "Equipe ativa", color = LocalContentColor.current)
                        HeadlineText(
                            text = "${state.activeEmployeesCount} pessoas",
                            bold = true,
                            color = LocalContentColor.current,
                        )
                    }
                    ZeraButton(
                        text = "Novo",
                        onClick = viewModel::onInviteClick,
                        style = ZeraColorFamily.Yellow,
                        icon = ZeraIcon.Plus,
                    )
                }
            }

            // Seção opcional, sem skeleton: entra quando os convites chegam.
            if (state.pendingInvites.isNotEmpty()) {
                TitleText(
                    text = "Convites pendentes",
                    bold = true,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                state.pendingInvites.forEach { invite ->
                    InviteCard(
                        code = invite.code,
                        name = invite.inviteeName,
                        expireTime = invite.expiresInHours,
                        onCopyCodeClick = {
                            clipboardManager.setText(AnnotatedString(invite.code))
                        },
                    )
                }
            }

            EmployeeList(
                employees = state.employees,
                onItemClick = viewModel::onEmployeeClick,
                contentPadding = PaddingValues(Spacing.none),
                modifier = Modifier.heightIn(max = 400.dp),
                emptyContent = {
                    if (!state.employeesLoadFailed) BodyText(text = "Nenhum colaborador encontrado")
                },
            )
        }
    }

    state.errorMessage?.let { message ->
        ErrorFeedbackPopup(description = message, onDismissRequest = viewModel::onErrorDismiss)
    }
}

/**
 * Esqueleto do card "Equipe ativa" e da lista de colaboradores, emitido direto na `Column` do
 * scaffold. Os convites pendentes são opcionais e ficam de fora.
 */
@Composable
private fun EmployeesSkeleton() {
    SkeletonBox(height = SkeletonSummaryCardHeight, borderRadius = Radius.large)

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
        repeat(SkeletonEmployeeCount) {
            SkeletonBox(height = SkeletonEmployeeItemHeight, borderRadius = Radius.large)
        }
    }
}

@Composable
@Preview(heightDp = 900)
fun EmployeesScreenPreview() {
    val previewViewModel = EmployeesViewModel().apply {
        state.value = EmployeesState(
            activeEmployeesCount = 8,
            pendingInvites = listOf(
                PendingInviteCard(code = "ZRA-4821", inviteeName = "Ana Paula", expiresInHours = 20),
            ),
            employees = listOf(
                EmployeeItem(id = "invite-ZRA-4821", name = "Ana Paula", role = "Operador", isPending = true),
                EmployeeItem(id = "1", name = "Carlos Lima", role = "Operador"),
                EmployeeItem(id = "2", name = "Maria Souza", role = "Gestor"),
            ),
            isLoadingCount = false,
            isLoadingEmployees = false,
            isLoadingInvites = false,
        )
    }
    ZeraTheme {
        EmployeesScreen(viewModel = previewViewModel)
    }
}

/** A mesma tela com as cargas em andamento (as flags de [EmployeesState] nascem `true`). */
@Composable
@Preview(heightDp = 900)
fun EmployeesScreenLoadingPreview() {
    val previewViewModel = EmployeesViewModel().apply {
        state.value = EmployeesState()
    }
    ZeraTheme {
        EmployeesScreen(viewModel = previewViewModel)
    }
}
