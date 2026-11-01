package com.zera.android.view.screens.main.items.manager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.cards.ApprovedItemCard
import com.zera.android.view.components.layouts.SuccessScreenLayout
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.main.items.ItemApprovedViewModel

@Composable
fun ItemApprovedScreen(
    itemId: String,
    itemName: String,
    itemSubtitle: String,
    viewModel: ItemApprovedViewModel = viewModel()
) {
    val state by viewModel.state

    LaunchedEffect(itemId, itemName, itemSubtitle) {
        viewModel.setItem(itemId, itemName, itemSubtitle)
    }

    SuccessScreenLayout(
        title = "Item Aprovado",
        message = "O operário será notificado sobre a aprovação",
        onCloseClick = viewModel::onCloseClick,
        onBackToHomeClick = viewModel::onBackToHomeClick,
        content = {
            ApprovedItemCard(
                itemName = state.itemName,
                itemSubtitle = state.itemSubtitle,
                itemId = state.itemId,
                status = state.status,
            )
        },
        actions = {
            ZeraButton(
                text = "Ver itens em aprovação",
                onClick = viewModel::onViewPendingItemsClick,
                style = ZeraColorFamily.Yellow,
                fillMaxWidth = true,
            )
        },
    )
}

@Composable
@Preview(heightDp = 900)
fun ItemApprovedScreenPreview() {
    ZeraTheme {
        ItemApprovedScreen(
            itemId = "265964",
            itemName = "Placa de vídeo",
            itemSubtitle = "Notebook Mac",
        )
    }
}
