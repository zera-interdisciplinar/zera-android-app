    package com.zera.android.view.screens.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.cards.ShortcutCard
import com.zera.android.view.components.cards.StockOccupationCard
import com.zera.android.view.components.lists.NotificationList
import com.zera.android.view.components.lists.ProductList
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.employee.EmployeeHomeViewModel

@Composable
fun EmployeeHomeScreen(
    viewModel: EmployeeHomeViewModel = viewModel()
) {
    val state by viewModel.state

    EmployeeScaffold(
        title = "Visão geral",
        currentRoute = Route.EmployeeHome,
        onFabClick = { /* TODO: abrir chatbot */ },
    ) {
        Column(modifier = Modifier.padding(horizontal = Spacing.small)) {
            BodyText(text = state.userName, bold = true)
            CaptionText(text = state.userRole)
        }

        StockOccupationCard(itemCount = state.stockItemCount, occupation = state.stockOccupation)

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            ZeraButton(
                text = "Escanear",
                onClick = viewModel::onScanClick,
                modifier = Modifier.weight(1f),
                fillMaxWidth = true,
                style = ZeraColorFamily.Yellow,
                icon = ZeraIcon.QrCode,
            )
            ZeraButton(
                text = "Adicionar Modelo",
                onClick = viewModel::onAddModelClick,
                modifier = Modifier.weight(1f),
                fillMaxWidth = true,
                style = ZeraColorFamily.Green,
                icon = ZeraIcon.Plus,
            )
        }

        TitleText(
            text = "Atalhos",
            bold = true,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            ShortcutCard(
                label = "Manutenção",
                value = state.maintenanceCount,
                labelIcon = ZeraIcon.Wrench,
                onClick = viewModel::onMaintenanceCardClick,
                modifier = Modifier.weight(1f),
            )
            ShortcutCard(
                label = "Descartes",
                value = state.discardCount,
                labelIcon = ZeraIcon.Recycle,
                onClick = viewModel::onDiscardCardClick,
                modifier = Modifier.weight(1f),
            )
        }

        TitleText(
            text = "Pendências",
            bold = true,
            color = MaterialTheme.colorScheme.onSurface,
        )
        NotificationList(
            notifications = state.pendingNotifications,
            onItemClick = { viewModel.onPendingClick() },
            contentPadding = PaddingValues(Spacing.none),
            modifier = Modifier.heightIn(max = 400.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TitleText(
                text = "Últimos itens",
                bold = true,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            LabelText(
                text = "Ver Todos",
                bold = true,
                color = MaterialTheme.colorScheme.primary,
                onClick = viewModel::onSeeAllItemsClick,
            )
        }
        ProductList(
            products = state.latestProducts,
            onItemClick = { product ->
                ZeraNavigator.push(Route.ItemDetails(itemId = product.id))
            },
            contentPadding = PaddingValues(Spacing.none),
            modifier = Modifier.heightIn(max = 600.dp),
        )
    }
}

@Composable
@Preview(heightDp = 900)
fun EmployeeHomeScreenPreview() {
    ZeraTheme {
        EmployeeHomeScreen()
    }
}
