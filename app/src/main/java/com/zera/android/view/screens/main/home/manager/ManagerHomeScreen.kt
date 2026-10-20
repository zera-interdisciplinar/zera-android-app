package com.zera.android.view.screens.main.home.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.zera.android.view.components.cards.ShortcutCard
import com.zera.android.view.components.cards.StockOccupationCard
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.components.lists.NotificationList
import com.zera.android.view.components.lists.ProductItem
import com.zera.android.view.components.lists.ProductList
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.scaffolds.ManagerScaffold
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.main.home.manager.ManagerHomeState
import com.zera.android.viewmodel.main.home.manager.ManagerHomeViewModel

// Alturas aproximadas dos blocos reais, para o esqueleto não "pular" quando o conteúdo chega.
private val SkeletonTitleHeight = 24.dp
private val SkeletonStockCardHeight = 120.dp
private val SkeletonShortcutCardHeight = 112.dp

@Composable
fun ManagerHomeScreen(
    viewModel: ManagerHomeViewModel = viewModel()
) {
    val state by viewModel.state

    ManagerScaffold(
        title = "Visão geral",
        currentRoute = Route.ManagerHome,
    ) {
        if (state.isLoading) {
            ManagerHomeSkeleton()
        } else {
            Column(modifier = Modifier.padding(horizontal = Spacing.small)) {
                BodyText(text = state.userName, bold = true)
                CaptionText(text = state.userRole)
            }

            state.errorMessage?.let { message ->
                CaptionText(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = Spacing.small),
                )
            }

            StockOccupationCard(itemCount = state.stockItemCount, occupation = state.stockOccupation)

            TitleText(
                text = "Resumo geral",
                bold = true,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                ShortcutCard(
                    label = "Itens",
                    value = state.totalItems,
                    labelIcon = ZeraIcon.Box,
                    onClick = viewModel::onItemCardClick,
                    modifier = Modifier.weight(1f),
                    description = state.itemsChangeLabel.takeIf { it.isNotBlank() },
                    descriptionColor = if (state.itemsChangePositive) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
                ShortcutCard(
                    label = "Funcionários",
                    value = state.totalEmployees,
                    labelIcon = ZeraIcon.Group,
                    onClick = viewModel::onEmployeesCardClick,
                    modifier = Modifier.weight(1f),
                    description = state.employeesDescription,
                    descriptionColor = MaterialTheme.colorScheme.secondary,
                )
            }

            // Seção opcional, sem skeleton: entra quando os alertas chegam, sem piscar o estado vazio.
            if (!state.isLoadingAlerts) {
                TitleText(
                    text = "Alertas importantes",
                    bold = true,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                NotificationList(
                    notifications = state.notifications,
                    onItemClick = viewModel::onAlertClick,
                    contentPadding = PaddingValues(Spacing.none),
                    modifier = Modifier.heightIn(max = 400.dp),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TitleText(
                    text = "Últimos Itens",
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
}

/**
 * Esqueleto da home do gestor: espelha a ordem dos blocos de [ManagerHomeScreen], emitidos
 * direto na [Column] do [ManagerScaffold] para herdar o mesmo espaçamento da tela real.
 */
@Composable
private fun ManagerHomeSkeleton() {
    Column(
        modifier = Modifier.padding(horizontal = Spacing.small),
        verticalArrangement = Arrangement.spacedBy(Spacing.micro),
    ) {
        SkeletonBox(width = 140.dp, height = 16.dp)
        SkeletonBox(width = 80.dp, height = 12.dp)
    }

    SkeletonBox(height = SkeletonStockCardHeight, borderRadius = Radius.xLarge)

    SkeletonBox(width = 120.dp, height = SkeletonTitleHeight)
    // A altura vem do Row; os blocos só dividem a largura e preenchem essa altura.
    Row(
        modifier = Modifier.height(SkeletonShortcutCardHeight),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        repeat(2) {
            SkeletonBox(modifier = Modifier.weight(1f), borderRadius = Radius.large)
        }
    }
}

@Composable
@Preview(heightDp = 900)
fun ManagerHomeScreenPreview() {
    val previewViewModel = ManagerHomeViewModel().apply {
        state.value = ManagerHomeState(
            userName = "Maria Souza",
            userRole = "Gestor",
            stockItemCount = 300,
            stockOccupation = 0.6f,
            totalItems = "300",
            itemsChangeLabel = "↑ 12%",
            totalEmployees = "8",
            notifications = listOf(
                NotificationItem(
                    id = "1",
                    label = "Item sem destino",
                    text = "Mouse sem fio · aguardando destino",
                    style = ZeraColorFamily.Yellow,
                ),
                NotificationItem(
                    id = "2",
                    label = "Estoque acima de 80%",
                    style = ZeraColorFamily.Red,
                ),
            ),
            latestProducts = listOf(
                ProductItem(id = "2659646", name = "Placa de vídeo"),
                ProductItem(id = "1182036", name = "Teclado mecânico"),
                ProductItem(id = "3300981", name = "Monitor 24\""),
            ),
            isLoadingUser = false,
            isLoadingInventory = false,
            isLoadingAlerts = false,
            isLoadingEmployees = false,
        )
    }
    ZeraTheme {
        ManagerHomeScreen(viewModel = previewViewModel)
    }
}

/** A mesma tela com todas as cargas em andamento (as flags de [ManagerHomeState] nascem `true`). */
@Composable
@Preview(heightDp = 900)
fun ManagerHomeScreenLoadingPreview() {
    val previewViewModel = ManagerHomeViewModel().apply {
        state.value = ManagerHomeState()
    }
    ZeraTheme {
        ManagerHomeScreen(viewModel = previewViewModel)
    }
}
