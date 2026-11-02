package com.zera.android.view.screens.main.recycling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.feedback.ErrorFeedbackPopup
import com.zera.android.view.components.inputs.ZeraChipsGroup
import com.zera.android.view.components.inputs.ZeraSearchInput
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.components.lists.OptionList
import com.zera.android.view.components.loading.OptionListSkeleton
import com.zera.android.view.components.overlays.BottomSheet
import com.zera.android.view.components.scaffolds.EmployeeScaffold
import com.zera.android.view.components.scaffolds.ManagerScaffold
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.main.recycling.ScheduledDisposalsState
import com.zera.android.viewmodel.main.recycling.ScheduledDisposalsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduledDisposalsScreen(
    viewModel: ScheduledDisposalsViewModel = viewModel(),
) {
    val state by viewModel.state
    var showFiltersSheet by rememberSaveable { mutableStateOf(false) }
    var draftDestinations by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var draftPeriod by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedFilterChips = state.selectedDestinations + listOfNotNull(state.selectedPeriod)

    val body: @Composable ColumnScope.() -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            ZeraSearchInput(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = "Pesquisar por local, item ou código...",
                onSearch = viewModel::onSearch,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                icon = ZeraIcon.Filter,
                onClick = {
                    draftDestinations = state.selectedDestinations
                    draftPeriod = state.selectedPeriod
                    showFiltersSheet = true
                },
                contentDescription = "Filtros",
                type = ZeraButtonType.Base,
            )
        }

        if (selectedFilterChips.isNotEmpty()) {
            ZeraChipsGroup(
                options = selectedFilterChips,
                selectedValues = selectedFilterChips,
                onSelectedValuesChange = viewModel::onAppliedFilterChipsChange,
                selectMany = true,
            )
        }

        // Só a região da lista alterna: busca e filtros ficam reais, senão o campo perderia o foco
        // quando uma busca (que reativa isLoading) recarrega a lista.
        if (state.isLoading) {
            OptionListSkeleton(modifier = Modifier.weight(1f))
        } else {
            OptionList(
                options = state.disposals,
                onItemClick = viewModel::onDisposalClick,
                contentPadding = PaddingValues(vertical = Spacing.small),
                modifier = Modifier.weight(1f),
                onEndReached = viewModel::loadNextPage,
                isLoadingMore = state.isLoadingMore,
                emptyContent = {
                    if (!state.loadFailed) BodyText(text = "Nenhum descarte")
                },
            )
        }
    }

    if (state.isEmployee) {
        EmployeeScaffold(
            title = "Histórico de descartes",
            currentRoute = Route.ScheduledDisposals,
            goBack = true,
            scrollable = false,
            fabIcon = null,
            content = body,
        )
    } else {
        ManagerScaffold(
            title = "Histórico de descartes",
            currentRoute = Route.ScheduledDisposals,
            goBack = true,
            scrollable = false,
            fabIcon = null,
            content = body,
        )
    }

    if (showFiltersSheet) {
        BottomSheet(
            onDismissRequest = { showFiltersSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            TitleText(
                text = "Escolha quais filtros deseja aplicar no histórico",
                bold = true,
            )
            ZeraChipsGroup(
                label = "Destino",
                options = state.destinationOptions,
                selectedValues = draftDestinations,
                onSelectedValuesChange = { draftDestinations = it },
                selectMany = true,
                stacked = true,
            )
            ZeraChipsGroup(
                label = "Data do descarte",
                options = state.periodOptions,
                selected = draftPeriod,
                onSelectedChange = { picked ->
                    draftPeriod = if (picked == draftPeriod) null else picked
                },
                stacked = true,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ZeraButton(
                    text = "Aplicar filtros",
                    onClick = {
                        viewModel.applyFilters(draftDestinations, draftPeriod)
                        showFiltersSheet = false
                    },
                    fillMaxWidth = true,
                    style = ZeraColorFamily.Blue,
                )
            }
        }
    }

    state.errorMessage?.let { message ->
        ErrorFeedbackPopup(description = message, onDismissRequest = viewModel::onErrorDismiss)
    }
}

@Composable
@Preview(heightDp = 900)
private fun ScheduledDisposalsScreenPreview() {
    val previewViewModel = ScheduledDisposalsViewModel().apply {
        state.value = ScheduledDisposalsState(
            disposals = listOf(
                OptionItem(name = "Cooperativa Verde", description = "12/10/2026 · 4 itens"),
                OptionItem(name = "EcoPonto Centro", description = "05/10/2026 · 2 itens"),
                OptionItem(name = "Aterro Municipal", description = "28/09/2026 · 7 itens"),
            ),
            isLoading = false,
        )
    }
    ZeraTheme {
        ScheduledDisposalsScreen(viewModel = previewViewModel)
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [ScheduledDisposalsState] nasce `true`). */
@Composable
@Preview(heightDp = 900)
private fun ScheduledDisposalsScreenLoadingPreview() {
    val previewViewModel = ScheduledDisposalsViewModel().apply {
        state.value = ScheduledDisposalsState()
    }
    ZeraTheme {
        ScheduledDisposalsScreen(viewModel = previewViewModel)
    }
}
