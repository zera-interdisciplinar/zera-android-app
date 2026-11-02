package com.zera.android.view.screens.main.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.cards.ItemSummaryCard
import com.zera.android.view.components.feedback.ErrorFeedbackPopup
import com.zera.android.view.components.forms.EditCategoryForm
import com.zera.android.view.components.lists.EditableFieldRow
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.navigation.SideBarOverlay
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.overlays.BottomSheet
import com.zera.android.view.components.overlays.ConfirmationPopup
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.main.categories.CategoryDetailsState
import com.zera.android.viewmodel.main.categories.CategoryDetailsViewModel

// Alturas aproximadas dos blocos reais; o esqueleto só indica a disposição.
private val SkeletonSummaryCardHeight = 120.dp
private val SkeletonFieldRowHeight = 56.dp
private const val SkeletonFieldRowCount = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailsScreen(
    categoryId: String,
    viewModel: CategoryDetailsViewModel = viewModel(),
) {
    val state by viewModel.state
    var showSideBar by rememberSaveable { mutableStateOf(false) }
    val actionsEnabled = !state.isLoading && !state.isSubmitting && state.categoryId.isNotBlank()

    LaunchedEffect(categoryId) {
        viewModel.loadCategory(categoryId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                UpperNavBar(
                    title = "Detalhes da categoria",
                    goBack = true,
                    onSideBarClick = { showSideBar = true },
                    onBackClick = viewModel::onBackClick,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = Spacing.small),
                )
            },
            bottomBar = {
                if (state.canEdit) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
                    ) {
                        ZeraButton(
                            text = "Editar",
                            onClick = viewModel::onEditClick,
                            type = ZeraButtonType.Secondary,
                            enabled = actionsEnabled,
                            fillMaxWidth = true,
                        )
                    }
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
                if (state.isLoading) {
                    CategoryDetailsSkeleton()
                } else {
                    ItemSummaryCard(
                        itemName = state.name.ifBlank { "Categoria" },
                        itemSubtitle = state.description,
                        icon = ZeraIcon.Category,
                    )

                    EditableFieldRow(label = "Nome", value = state.name)
                    EditableFieldRow(label = "Descrição", value = state.description)
                    EditableFieldRow(label = "Data de Cadastro", value = state.registeredAt)

                    if (state.canDelete) {
                        ZeraButton(
                            text = "Excluir categoria",
                            onClick = viewModel::onDeleteClick,
                            type = ZeraButtonType.Secondary,
                            style = ZeraColorFamily.Red,
                            enabled = actionsEnabled,
                            fillMaxWidth = true,
                        )
                    }
                }
            }
        }

        SideBarOverlay(
            visible = showSideBar,
            onDismiss = { showSideBar = false },
            items = state.sideBarItems,
            currentRoute = Route.CategoryDetails(categoryId),
        )
    }

    state.editDraft?.let { draft ->
        BottomSheet(
            onDismissRequest = viewModel::onEditDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            EditCategoryForm(
                name = draft.name,
                onNameChange = viewModel::onEditNameChange,
                description = draft.description,
                onDescriptionChange = viewModel::onEditDescriptionChange,
                onSave = viewModel::onEditSave,
                onCancel = viewModel::onEditDismiss,
                modifier = Modifier.verticalScroll(rememberScrollState()),
                nameError = draft.nameError,
                descriptionError = draft.descriptionError,
                isSubmitting = state.isSubmitting,
            )
        }
    }

    if (state.showDeleteConfirmation) {
        ConfirmationPopup(
            title = "Excluir essa categoria?",
            message = "A categoria será excluída definitivamente.",
            confirmButtonText = "Excluir",
            onConfirm = viewModel::onDeleteConfirm,
            onDismissRequest = viewModel::onDeleteDismiss,
        )
    }

    state.errorMessage?.let { message ->
        ErrorFeedbackPopup(description = message, onDismissRequest = viewModel::onErrorDismiss)
    }
}

/** Esqueleto do card-resumo e dos campos, emitido direto na `Column` da tela. */
@Composable
private fun CategoryDetailsSkeleton() {
    SkeletonBox(height = SkeletonSummaryCardHeight, borderRadius = Radius.large)
    repeat(SkeletonFieldRowCount) {
        SkeletonBox(height = SkeletonFieldRowHeight)
    }
}

// Com categoryId em branco a tela não dispara a carga real, então o state injetado é preservado.
@Composable
@Preview(heightDp = 900)
private fun CategoryDetailsScreenPreview() {
    val previewViewModel = CategoryDetailsViewModel().apply {
        state.value = CategoryDetailsState(
            categoryId = "c1",
            name = "Informática",
            description = "Notebooks e monitores",
            registeredAt = "12 de Out 2026 - 9h30",
            canEdit = true,
            isLoading = false,
        )
    }
    ZeraTheme {
        CategoryDetailsScreen(categoryId = "", viewModel = previewViewModel)
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [CategoryDetailsState] nasce `true`). */
@Composable
@Preview(heightDp = 900)
private fun CategoryDetailsScreenLoadingPreview() {
    val previewViewModel = CategoryDetailsViewModel().apply {
        state.value = CategoryDetailsState()
    }
    ZeraTheme {
        CategoryDetailsScreen(categoryId = "c1", viewModel = previewViewModel)
    }
}
