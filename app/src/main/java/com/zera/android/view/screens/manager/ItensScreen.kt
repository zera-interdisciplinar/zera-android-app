package com.zera.android.view.screens.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.inputs.ZeraChipsGroup
import com.zera.android.view.components.inputs.ZeraSearchInput
import com.zera.android.view.components.lists.ProductList
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.manager.ItensViewModel

@Composable
fun ItensScreen(
    viewModel: ItensViewModel = viewModel(),
) {
    val state by viewModel.state

    ManagerScaffold(
        title = "Itens",
        currentRoute = Route.Itens,
        goBack = true,
        scrollable = false,
        onFabClick = { /* TODO: abrir chatbot */ },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            ZeraSearchInput(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = "Pesquisar por ID ou Nome...",
                onSearch = viewModel::onSearch,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                icon = ZeraIcon.Filter,
                onClick = {},
                contentDescription = "Filtros",
                type = ZeraButtonType.Base
            )
        }

        ZeraChipsGroup(
            options = state.filterOptions,
            selected = state.selectedFilter,
            onSelectedChange = viewModel::onFilterChange,
        )
        state.errorMessage?.let { message ->
            CaptionText(text = message, color = MaterialTheme.colorScheme.error)
        }
        TitleText(text = state.totalItemsLabel, bold = true)
        ProductList(
            products = state.items,
            onItemClick = { product ->
                ZeraNavigator.push(Route.ItemDetails(itemId = product.id))
            },
            contentPadding = PaddingValues(vertical = Spacing.small),
            modifier = Modifier.weight(1f),
            onEndReached = viewModel::loadNextPage,
            isLoadingMore = state.isLoadingMore,
        )
    }
}

@Composable
@Preview(heightDp = 900)
fun ItensScreenPreview() {
    ZeraTheme {
        ItensScreen()
    }
}
