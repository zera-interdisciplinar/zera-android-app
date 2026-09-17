package com.zera.android.view.screens.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.cards.ItemSummaryCard
import com.zera.android.view.components.lists.EditableFieldRow
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.manager.ItemDetailsViewModel

@Composable
fun ItemDetailsScreen(
    viewModel: ItemDetailsViewModel = viewModel()
) {
    val state by viewModel.state

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            UpperNavBar(
                title = "Detalhes do Item",
                goBack = true,
                onBackClick = viewModel::onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.small),
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = Spacing.medium, vertical = Spacing.small),
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                ZeraButton(
                    text = "Editar",
                    onClick = viewModel::onEditClick,
                    type = ZeraButtonType.Secondary,
                    modifier = Modifier.weight(1f),
                )
                ZeraButton(
                    text = "Aprovar",
                    onClick = viewModel::onApproveClick,
                    modifier = Modifier.weight(1f),
                )
            }
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
            ItemSummaryCard(
                itemId = state.itemId,
                itemName = state.itemName,
                itemSubtitle = state.itemSubtitle,
                statusText = state.statusText,
                statusStyle = state.statusStyle,
            )

            EditableFieldRow(label = "Categoria", value = state.category, onEditClick = viewModel::onEditClick)
            EditableFieldRow(label = "Material", value = state.material, onEditClick = viewModel::onEditClick)
            EditableFieldRow(label = "Condição", value = state.condition, onEditClick = viewModel::onEditClick)
            EditableFieldRow(label = "Cadastrado Por", value = state.registeredBy, onEditClick = viewModel::onEditClick)
            EditableFieldRow(label = "Data de Cadastro", value = state.registeredAt, onEditClick = viewModel::onEditClick)
        }
    }
}

@Composable
@Preview(heightDp = 900)
fun ItemDetailsScreenPreview() {
    ZeraTheme {
        ItemDetailsScreen()
    }
}
