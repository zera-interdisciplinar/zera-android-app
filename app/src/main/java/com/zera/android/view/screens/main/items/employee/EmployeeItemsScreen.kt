package com.zera.android.view.screens.main.items.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.feedback.ErrorFeedbackPopup
import com.zera.android.view.components.inputs.ZeraChipsGroup
import com.zera.android.view.components.inputs.ZeraSearchInput
import com.zera.android.view.components.lists.ProductItem
import com.zera.android.view.components.lists.ProductList
import com.zera.android.view.components.loading.ProductListSkeleton
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.components.overlays.BottomSheet
import com.zera.android.view.components.scaffolds.EmployeeScaffold
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.main.items.ItensState
import com.zera.android.viewmodel.main.items.ItensViewModel

/**
 * Tela de itens do operário.
 *
 * Mesma estrutura da `ItensScreen` do gestor (busca, filtros, contagem e lista paginada,
 * reaproveitando o [ItensViewModel]), com duas diferenças: tem o botão "Adicionar novo item"
 * e os itens da lista não exibem a tag de status.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeItemsScreen(
    initialStatus: String? = null,
    viewModel: ItensViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ItensViewModel(initialStatuses = listOfNotNull(initialStatus)) }
        },
    ),
) {
    val state by viewModel.state
    var showFiltersSheet by rememberSaveable { mutableStateOf(false) }
    var draftStatuses by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var draftCategories by rememberSaveable { mutableStateOf(emptyList<String>()) }
    val selectedFilterChips = state.selectedStatuses + state.selectedCategories
    // O operário não vê o status do item na lista; sem statusText o item mostra a seta.
    val products = remember(state.items) { state.items.map { it.copy(statusText = null) } }

    EmployeeScaffold(
        title = "Itens",
        currentRoute = Route.EmployeeItems(),
        scrollable = false,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            ZeraSearchInput(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = "Pesquisar por ID, Nome ou Material...",
                onSearch = viewModel::onSearch,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                icon = ZeraIcon.Filter,
                onClick = {
                    draftStatuses = state.selectedStatuses
                    draftCategories = state.selectedCategories
                    showFiltersSheet = true
                },
                contentDescription = "Filtros",
                type = ZeraButtonType.Base,
            )
        }

        ZeraButton(
            text = "Adicionar novo item",
            onClick = { ZeraNavigator.push(Route.ManualRegister()) },
            fillMaxWidth = true,
            style = ZeraColorFamily.Blue,
            icon = ZeraIcon.Plus,
        )

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
            SkeletonBox(width = 100.dp, height = 24.dp)
            ProductListSkeleton(modifier = Modifier.weight(1f))
        } else {
            TitleText(text = state.totalItemsLabel, bold = true)
            ProductList(
                products = products,
                onItemClick = { product ->
                    ZeraNavigator.push(Route.ItemDetails(itemId = product.id))
                },
                contentPadding = PaddingValues(vertical = Spacing.small),
                modifier = Modifier.weight(1f),
                onEndReached = viewModel::loadNextPage,
                isLoadingMore = state.isLoadingMore,
                emptyContent = {
                    if (!state.loadFailed) BodyText(text = "Nenhum produto encontrado")
                },
            )
        }
    }

    if (showFiltersSheet) {
        BottomSheet(
            onDismissRequest = { showFiltersSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            TitleText(
                text = "Escolha quais filtros deseja aplicar em itens",
                bold = true,
            )
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.large),
            ) {
                ZeraChipsGroup(
                    label = "Status",
                    options = ItemStatus.entries.map { it.label },
                    selectedValues = draftStatuses,
                    onSelectedValuesChange = { draftStatuses = it },
                    selectMany = true,
                    stacked = true,
                )
                ZeraChipsGroup(
                    label = "Categoria",
                    options = state.categoryOptions,
                    selectedValues = draftCategories,
                    onSelectedValuesChange = { draftCategories = it },
                    selectMany = true,
                    stacked = true,
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ZeraButton(
                    text = "Aplicar filtros",
                    onClick = {
                        viewModel.applyFilters(draftStatuses, draftCategories)
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
fun EmployeeItemsScreenPreview() {
    val previewViewModel = ItensViewModel().apply {
        state.value = ItensState(
            totalItemsLabel = "3 itens",
            items = listOf(
                ProductItem(id = "2659646", name = "Placa de vídeo"),
                ProductItem(id = "1182036", name = "Teclado mecânico"),
                ProductItem(id = "3300981", name = "Monitor 24\""),
            ),
            isLoading = false,
        )
    }
    ZeraTheme {
        EmployeeItemsScreen(viewModel = previewViewModel)
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [ItensState] nasce `true`). */
@Composable
@Preview(heightDp = 900)
fun EmployeeItemsScreenLoadingPreview() {
    val previewViewModel = ItensViewModel().apply {
        state.value = ItensState()
    }
    ZeraTheme {
        EmployeeItemsScreen(viewModel = previewViewModel)
    }
}
