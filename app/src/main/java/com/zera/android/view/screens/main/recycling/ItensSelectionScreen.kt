package com.zera.android.view.screens.main.recycling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.feedback.ErrorFeedbackPopup
import com.zera.android.view.components.inputs.SelectBoxList
import com.zera.android.view.components.inputs.SelectOption
import com.zera.android.view.components.inputs.ZeraSearchInput
import com.zera.android.view.components.loading.OptionListSkeleton
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.SubtitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.main.recycling.ItensSelectionState
import com.zera.android.viewmodel.main.recycling.ItensSelectionViewModel
import com.zera.android.viewmodel.main.recycling.formatDistanceMeters

@Composable
fun ItensSelectionScreen(
    placeId: String,
    placeName: String,
    placeAddress: String,
    distanceMeters: Long,
    isOpen: Boolean? = null,
    description: String = "",
    openingDays: List<String> = emptyList(),
    openingHourLabels: List<String> = emptyList(),
    recyclingBusinessId: String = "",
    contactEmail: String = "",
    viewModel: ItensSelectionViewModel = viewModel(),
) {
    val state by viewModel.state

    LaunchedEffect(placeId, placeName, placeAddress, distanceMeters, recyclingBusinessId) {
        viewModel.bindPlace(
            placeId, placeName, placeAddress, distanceMeters,
            isOpen, description, openingDays, openingHourLabels, recyclingBusinessId, contactEmail,
        )
    }

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
            SubtitleText(text = placeName.ifBlank { "Recicladora" })
            CaptionText(
                text = listOf(placeAddress, formatDistanceMeters(distanceMeters))
                    .filter { it.isNotBlank() }
                    .joinToString(" · "),
            )
            SubtitleText(text = "Quais produtos serão descartados?")
            ZeraSearchInput(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                onSearch = viewModel::onSearch,
                placeholder = "Buscar itens cadastrados",
            )
            // Só a região da lista alterna: o cabeçalho, a busca e o botão ficam reais.
            if (state.isLoading) {
                OptionListSkeleton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            } else {
                SelectBoxList(
                    options = state.options,
                    selected = state.selectedValues,
                    onSelectedChange = viewModel::onSelectedValuesChange,
                    hasSelectAll = true,
                    onEndReached = viewModel::loadNextPage,
                    isLoadingMore = state.isLoadingMore,
                    emptyContent = {
                        if (!state.loadFailed) BodyText(text = "Nenhuma opção disponível")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            }
            ZeraButton(
                text = "Continuar",
                onClick = viewModel::onChooseDateClick,
                enabled = state.canChooseDate,
                fillMaxWidth = true,
            )
        }
    }

    state.errorMessage?.let { message ->
        ErrorFeedbackPopup(description = message, onDismissRequest = viewModel::onErrorDismiss)
    }
}

@Composable
@Preview(heightDp = 800)
private fun ItensSelectionScreenPreview() {
    val previewViewModel = ItensSelectionViewModel().apply {
        state.value = ItensSelectionState(
            options = listOf(
                SelectOption(name = "Notebook", value = "1", description = "ID 2659646 · Informática"),
                SelectOption(name = "Teclado mecânico", value = "2", description = "ID 1182036 · Informática"),
                SelectOption(name = "Monitor 24\"", value = "3", description = "ID 3300981 · Informática"),
            ),
            isLoading = false,
        )
    }
    ZeraTheme {
        ItensSelectionScreen(
            placeId = "places/ChIJ",
            placeName = "Cooperativa Recicla SP",
            placeAddress = "Rua X, 123",
            distanceMeters = 1240,
            viewModel = previewViewModel,
        )
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [ItensSelectionState] nasce `true`). */
@Composable
@Preview(heightDp = 800)
private fun ItensSelectionScreenLoadingPreview() {
    val previewViewModel = ItensSelectionViewModel().apply {
        state.value = ItensSelectionState()
    }
    ZeraTheme {
        ItensSelectionScreen(
            placeId = "places/ChIJ",
            placeName = "Cooperativa Recicla SP",
            placeAddress = "Rua X, 123",
            distanceMeters = 1240,
            viewModel = previewViewModel,
        )
    }
}
