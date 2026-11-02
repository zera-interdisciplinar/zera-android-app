package com.zera.android.view.screens.main.models

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.zera.android.view.components.inputs.ZeraInputPopup
import com.zera.android.view.components.lists.EditableFieldRow
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.navigation.SideBarOverlay
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.main.models.ModelDetailsState
import com.zera.android.viewmodel.main.models.ModelDetailsViewModel

// Alturas aproximadas dos blocos reais; o esqueleto só indica a disposição.
private val SkeletonSummaryCardHeight = 120.dp
private val SkeletonFieldRowHeight = 56.dp
private const val SkeletonFieldRowCount = 4

@Composable
fun ModelDetailsScreen(
    modelId: String,
    viewModel: ModelDetailsViewModel = viewModel(),
) {
    val state by viewModel.state
    var showSideBar by rememberSaveable { mutableStateOf(false) }
    var showRejectPopup by remember { mutableStateOf(false) }
    val actionsEnabled = !state.isLoading && !state.isSubmitting && state.modelId.isNotBlank()
    val editClick = if (state.canEdit && actionsEnabled) ({}) else null

    LaunchedEffect(modelId) {
        viewModel.loadModel(modelId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                UpperNavBar(
                    title = "Detalhes do modelo",
                    goBack = true,
                    onSideBarClick = { showSideBar = true },
                    onBackClick = viewModel::onBackClick,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = Spacing.small),
                )
            },
            bottomBar = {
                if (state.canReview) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    ) {
                        ZeraButton(
                            text = "Recusar",
                            onClick = { showRejectPopup = true },
                            type = ZeraButtonType.Secondary,
                            style = ZeraColorFamily.Red,
                            enabled = actionsEnabled,
                            modifier = Modifier.weight(1f),
                        )
                        ZeraButton(
                            text = if (state.isSubmitting) "Enviando..." else "Aprovar",
                            onClick = viewModel::onApproveClick,
                            enabled = actionsEnabled,
                            modifier = Modifier.weight(1f),
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
                    ModelDetailsSkeleton()
                } else {
                    if (state.reviewHint.isNotBlank()) {
                        CaptionText(text = state.reviewHint)
                    }

                    ItemSummaryCard(
                        itemName = state.name.ifBlank { "Modelo" },
                        itemSubtitle = state.manufacturer,
                        status = state.status,
                        condition = state.category,
                    )

                    EditableFieldRow(
                        label = "Nome",
                        value = state.name,
                        onEditClick = editClick,
                        onConfirm = viewModel::onNameConfirm,
                        validate = ModelDetailsViewModel::nameValidationError,
                    )
                    EditableFieldRow(
                        label = "Marca",
                        value = state.manufacturer,
                        onEditClick = editClick,
                        onConfirm = viewModel::onManufacturerConfirm,
                        validate = ModelDetailsViewModel::manufacturerValidationError,
                    )
                    EditableFieldRow(label = "Categoria", value = state.category)
                    EditableFieldRow(label = "Material", value = state.material)
                    if (state.notes.isNotBlank()) {
                        EditableFieldRow(label = "Observações", value = state.notes)
                    }
                    if (state.rejectionReason.isNotBlank()) {
                        EditableFieldRow(label = "Motivo da recusa", value = state.rejectionReason)
                    }
                }
            }
        }

        SideBarOverlay(
            visible = showSideBar,
            onDismiss = { showSideBar = false },
            items = state.sideBarItems,
            currentRoute = Route.ModelDetails(modelId),
        )
    }

    if (showRejectPopup && state.canReview) {
        ZeraInputPopup(
            label = "Motivo da recusa",
            value = "",
            onDismissRequest = { showRejectPopup = false },
            onConfirm = { reason ->
                viewModel.onRejectConfirm(reason)
                showRejectPopup = false
            },
            validate = ModelDetailsViewModel::reasonValidationError,
        )
    }

    state.errorMessage?.let { message ->
        ErrorFeedbackPopup(description = message, onDismissRequest = viewModel::onErrorDismiss)
    }
}

/**
 * Esqueleto do card-resumo e dos campos que sempre aparecem (nome, marca, categoria e material),
 * emitido direto na `Column` da tela. Observações e motivo da recusa são opcionais e ficam de fora.
 */
@Composable
private fun ModelDetailsSkeleton() {
    SkeletonBox(height = SkeletonSummaryCardHeight, borderRadius = Radius.large)
    repeat(SkeletonFieldRowCount) {
        SkeletonBox(height = SkeletonFieldRowHeight)
    }
}

// Com modelId em branco a tela não dispara a carga real, então o state injetado é preservado.
@Composable
@Preview(heightDp = 900)
private fun ModelDetailsScreenPreview() {
    val previewViewModel = ModelDetailsViewModel().apply {
        state.value = ModelDetailsState(
            modelId = "m1",
            name = "Placa de vídeo",
            manufacturer = "NVIDIA",
            category = "Informática",
            material = "Placa de circuito",
            status = ItemStatus.PendingApproval,
            canReview = true,
            isLoading = false,
        )
    }
    ZeraTheme {
        ModelDetailsScreen(modelId = "", viewModel = previewViewModel)
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [ModelDetailsState] nasce `true`). */
@Composable
@Preview(heightDp = 900)
private fun ModelDetailsScreenLoadingPreview() {
    val previewViewModel = ModelDetailsViewModel().apply {
        state.value = ModelDetailsState()
    }
    ZeraTheme {
        ModelDetailsScreen(modelId = "m1", viewModel = previewViewModel)
    }
}
