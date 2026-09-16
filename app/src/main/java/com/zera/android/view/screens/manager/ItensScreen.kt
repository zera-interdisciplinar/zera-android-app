package com.zera.android.view.screens.manager

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.inputs.ZeraChipsGroup
import com.zera.android.view.components.inputs.ZeraSearchInput
import com.zera.android.view.components.lists.ProductList
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.manager.ItensViewModel

@Composable
fun ItensScreen(
    viewModel: ItensViewModel = viewModel(),
) {
    val state by viewModel.state

    ManagerScaffold(
        title = "Itens",
        scrollable = false,
        onFabClick = { /* TODO: abrir chatbot */ },
    ) {
        ZeraSearchInput(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            placeholder = "Pesquisar por ID, Nome ou Material...",
            onSearch = {}
        )
        ZeraChipsGroup(
            options = state.filterOptions,
            selected = state.selectedFilter,
            onSelectedChange = viewModel::onFilterChange,
        )
        TitleText(text = state.totalItemsLabel, bold = true)
        ProductList(
            products = state.items,
            onItemClick = { /* TODO: abrir detalhes do item */ },
            contentPadding = PaddingValues(vertical = Spacing.small),
            modifier = Modifier.weight(1f),
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
