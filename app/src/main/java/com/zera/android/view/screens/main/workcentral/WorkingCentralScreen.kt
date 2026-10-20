package com.zera.android.view.screens.main.workcentral

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.cards.SummaryCard
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.components.lists.NotificationList
import com.zera.android.view.components.loading.NotificationListSkeleton
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.scaffolds.EmployeeScaffold
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.main.workcentral.WorkingCentralState
import com.zera.android.viewmodel.main.workcentral.WorkingCentralViewModel

// Alturas aproximadas dos blocos reais; o esqueleto só indica a disposição.
private val SkeletonSummaryCardHeight = 88.dp
private val SkeletonTitleHeight = 24.dp

/**
 * Central de Trabalho do operário: resumo do turno e lista de pendências prioritárias.
 *
 * A seção "Em manutenção" ainda não foi implementada.
 */
@Composable
fun WorkingCentralScreen(
    viewModel: WorkingCentralViewModel = viewModel(),
) {
    val state by viewModel.state

    EmployeeScaffold(
        title = "Central de Trabalho",
        currentRoute = Route.WorkingCentral,
    ) {
        if (state.isLoading) {
            WorkingCentralSkeleton()
        } else {
            SummaryCard(
                label = "Resumo do turno",
                text = state.shiftSummary,
            )

            TitleText(
                text = "Pendências prioritárias",
                bold = true,
                color = MaterialTheme.colorScheme.onSurface,
            )
            NotificationList(
                notifications = state.priorityNotifications,
                onItemClick = viewModel::onNotificationClick,
                contentPadding = PaddingValues(Spacing.none),
                modifier = Modifier.heightIn(max = 400.dp),
            )

            // TODO: seção "Em manutenção"
        }
    }
}

/** Esqueleto do resumo do turno e da lista de pendências, emitido direto na `Column` do scaffold. */
@Composable
private fun WorkingCentralSkeleton() {
    SkeletonBox(height = SkeletonSummaryCardHeight, borderRadius = Radius.large)

    SkeletonBox(width = 180.dp, height = SkeletonTitleHeight)
    NotificationListSkeleton()
}

@Composable
@Preview(heightDp = 900)
fun WorkingCentralScreenPreview() {
    val previewViewModel = WorkingCentralViewModel().apply {
        state.value = WorkingCentralState(
            maintenanceCount = 2,
            priorityNotifications = listOf(
                NotificationItem(
                    id = "1",
                    label = "Completar cadastro",
                    text = "Chip controlador · faltam foto, número de série",
                    style = ZeraColorFamily.Yellow,
                    resolvable = true,
                ),
                NotificationItem(
                    id = "2",
                    label = "Recusado pelo gestor",
                    text = "Monitor 24\" · imagem fora de foco",
                    style = ZeraColorFamily.Red,
                    resolvable = true,
                ),
            ),
            isLoading = false,
        )
    }
    ZeraTheme {
        WorkingCentralScreen(viewModel = previewViewModel)
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [WorkingCentralState] nasce `true`). */
@Composable
@Preview(heightDp = 900)
fun WorkingCentralScreenLoadingPreview() {
    val previewViewModel = WorkingCentralViewModel().apply {
        state.value = WorkingCentralState()
    }
    ZeraTheme {
        WorkingCentralScreen(viewModel = previewViewModel)
    }
}
