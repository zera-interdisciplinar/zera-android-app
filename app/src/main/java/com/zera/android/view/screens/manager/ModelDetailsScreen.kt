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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.cards.ItemSummaryCard
import com.zera.android.view.components.inputs.ZeraInputPopup
import com.zera.android.view.components.lists.EditableFieldRow
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.manager.ModelDetailsViewModel

@Composable
fun ModelDetailsScreen(
    modelId: String,
    viewModel: ModelDetailsViewModel = viewModel(),
) {
    val state by viewModel.state
    var showRejectPopup by remember { mutableStateOf(false) }
    val actionsEnabled = !state.isLoading && !state.isMutating && state.modelId.isNotBlank()
    val editClick = if (state.canEdit && actionsEnabled) ({}) else null

    LaunchedEffect(modelId) {
        viewModel.loadModel(modelId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Detalhes do modelo",
                goBack = true,
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
                        text = if (state.isMutating) "Enviando..." else "Aprovar",
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
            state.errorMessage?.let { message ->
                CaptionText(text = message, color = MaterialTheme.colorScheme.error)
            }
            if (state.reviewHint.isNotBlank()) {
                CaptionText(text = state.reviewHint)
            }

            ItemSummaryCard(
                itemId = state.modelId,
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
}

@Composable
@Preview(heightDp = 900)
private fun ModelDetailsScreenPreview() {
    ZeraTheme {
        ModelDetailsScreen(modelId = "m1")
    }
}
