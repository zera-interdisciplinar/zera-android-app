package com.zera.android.view.screens.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.cards.ShortcutCard
import com.zera.android.view.components.cards.StockOccupationCard
import com.zera.android.view.components.lists.NotificationList
import com.zera.android.view.components.lists.ProductList
import com.zera.android.view.components.navigation.BottomNavBar
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.manager.ManagerHomeViewModel

@Composable
fun ManagerHomeScreen(
    viewModel: ManagerHomeViewModel = viewModel()
) {
    val state by viewModel.state

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            UpperNavBar(
                title = "Visão geral",
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.small),
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Route.ManagerHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = Spacing.medium, vertical = Spacing.small),
            )
        },
        floatingActionButton = {
            IconButton(
                icon = ZeraIcon.Chatbot,
                onClick = { /* TODO: abrir chatbot */ },
                contentDescription = "Assistente virtual",
                size = 56.dp,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.medium, vertical = Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Column(modifier = Modifier.padding(horizontal = Spacing.small)) {
                BodyText(text = state.userName, bold = true)
                CaptionText(text = state.userRole)
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
                    onClick = { /* TODO: navegar para itens */ },
                    modifier = Modifier.weight(1f),
                )
                ShortcutCard(
                    label = "Funcionários",
                    value = state.totalEmployees,
                    labelIcon = ZeraIcon.Group,
                    onClick = { /* TODO: navegar para funcionários */ },
                    modifier = Modifier.weight(1f),
                )
            }

            TitleText(
                text = "Alertas importantes",
                bold = true,
                color = MaterialTheme.colorScheme.onSurface,
            )
            NotificationList(
                notifications = state.notifications,
                onItemClick = { /* TODO: abrir alerta */ },
                contentPadding = PaddingValues(Spacing.none),
                modifier = Modifier.heightIn(max = 400.dp),
            )

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
                    onClick = { /* TODO: navegar para lista de itens */ },
                )
            }
            ProductList(
                products = state.latestProducts,
                onItemClick = { /* TODO: abrir item */ },
                contentPadding = PaddingValues(Spacing.none),
                modifier = Modifier.heightIn(max = 400.dp),
            )
        }
    }
}

@Composable
@Preview(heightDp = 900)
fun ManagerHomeScreenPreview() {
    ZeraTheme {
        ManagerHomeScreen()
    }
}
