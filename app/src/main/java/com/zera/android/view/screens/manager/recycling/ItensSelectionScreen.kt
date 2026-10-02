package com.zera.android.view.screens.manager.recycling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.inputs.SelectBoxList
import com.zera.android.view.components.inputs.ZeraSearchInput
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.SubtitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.manager.ItensSelectionViewModel

@Composable
fun ItensSelectionScreen(
    viewModel: ItensSelectionViewModel = viewModel(),
) {
    val state by viewModel.state

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Selecionar Itens",
                goBack = true,
                showActions = false,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.small),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Spacing.medium, vertical = Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            SubtitleText(text = "Quais produtos serão descartados?")
            ZeraSearchInput(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                onSearch = viewModel::onSearch,
                placeholder = "Buscar itens cadastrados",
            )
            state.errorMessage?.let { message ->
                CaptionText(text = message, color = MaterialTheme.colorScheme.error)
            }
            SelectBoxList(
                options = state.options,
                selected = state.selectedValues,
                onSelectedChange = viewModel::onSelectedValuesChange,
                hasSelectAll = true,
                onEndReached = viewModel::loadNextPage,
                isLoadingMore = state.isLoadingMore,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            ZeraButton(
                text = "Escolher data",
                onClick = viewModel::onChooseDateClick,
                enabled = state.canChooseDate,
                fillMaxWidth = true,
            )
        }
    }
}

@Composable
@Preview(heightDp = 800)
private fun ItensSelectionScreenPreview() {
    ZeraTheme {
        ItensSelectionScreen()
    }
}
