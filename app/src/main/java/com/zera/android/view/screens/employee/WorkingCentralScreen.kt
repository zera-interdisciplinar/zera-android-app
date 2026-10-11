package com.zera.android.view.screens.employee

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
import com.zera.android.view.components.lists.NotificationList
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.employee.WorkingCentralViewModel

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
        onFabClick = { /* TODO: abrir chatbot */ },
    ) {
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

@Composable
@Preview(heightDp = 900)
fun WorkingCentralScreenPreview() {
    ZeraTheme {
        WorkingCentralScreen()
    }
}
