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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.cards.ItemSummaryCard
import com.zera.android.view.components.inputs.ZeraChipsGroup
import com.zera.android.view.components.inputs.ZeraInputPopup
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.lists.EditableFieldRow
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.overlays.PopupDialog
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.manager.ItemDetailsState
import com.zera.android.viewmodel.manager.ItemDetailsViewModel

@Composable
fun ItemDetailsScreen(
    itemId: String,
    viewModel: ItemDetailsViewModel = viewModel()
) {
    val state by viewModel.state
    var showEditPopup by remember { mutableStateOf(false) }
    var showRejectPopup by remember { mutableStateOf(false) }
    val actionsEnabled = !state.isLoading && !state.isMutating && state.itemId.isNotBlank()

    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Detalhes do Item",
                goBack = true,
                onBackClick = viewModel::onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.small),
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = Spacing.medium, vertical = Spacing.small),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                if (state.canReview) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                ZeraButton(
                    text = "Editar",
                    onClick = { showEditPopup = true },
                    type = ZeraButtonType.Secondary,
                    enabled = actionsEnabled,
                    fillMaxWidth = true,
                )
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

            ItemSummaryCard(
                itemId = state.itemId,
                itemName = state.itemName,
                itemSubtitle = state.itemSubtitle,
                status = state.status,
                condition = state.condition,
            )

            EditableFieldRow(
                label = "Nome",
                value = state.itemName,
                onEditClick = {},
                onConfirm = viewModel::onNameConfirm,
                validate = ItemDetailsViewModel::nameValidationError,
            )
            EditableFieldRow(label = "Categoria", value = state.category)
            EditableFieldRow(label = "Material", value = state.material)
            EditableFieldRow(
                label = "Condição",
                value = state.condition,
                onEditClick = {},
                onConfirm = viewModel::onConditionConfirm,
                validate = { input ->
                    if (ItemDetailsViewModel.conditionCodeFrom(input) == null) {
                        "Use Novo, Usado, Semidanificado ou Danificado."
                    } else {
                        null
                    }
                },
            )
            EditableFieldRow(
                label = "Número de série",
                value = state.serialNumber,
                onEditClick = {},
                onConfirm = viewModel::onSerialConfirm,
                validate = ItemDetailsViewModel::serialValidationError,
            )
            EditableFieldRow(
                label = "Observações",
                value = state.notes,
                onEditClick = {},
                onConfirm = viewModel::onNotesConfirm,
                validate = ItemDetailsViewModel::notesValidationError,
            )
            EditableFieldRow(label = "Cadastrado Por", value = state.registeredBy)
            EditableFieldRow(label = "Data de Cadastro", value = state.registeredAt)
        }
    }

    if (showEditPopup) {
        EditItemPopup(
            state = state,
            onDismissRequest = { showEditPopup = false },
            onConfirm = { name, condition, serial, notes ->
                viewModel.onEditConfirm(name, condition, serial, notes)
                showEditPopup = false
            },
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
            validate = ItemDetailsViewModel::reasonValidationError,
        )
    }
}

@Composable
private fun EditItemPopup(
    state: ItemDetailsState,
    onDismissRequest: () -> Unit,
    onConfirm: (name: String, condition: String, serialNumber: String, notes: String) -> Unit,
) {
    var name by remember(state.itemName) { mutableStateOf(state.itemName) }
    var condition by remember(state.condition) {
        mutableStateOf(state.condition.ifBlank { ItemDetailsViewModel.conditionOptions.first() })
    }
    var serialNumber by remember(state.serialNumber) { mutableStateOf(state.serialNumber) }
    var notes by remember(state.notes) { mutableStateOf(state.notes) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var conditionError by remember { mutableStateOf<String?>(null) }
    var serialError by remember { mutableStateOf<String?>(null) }
    var notesError by remember { mutableStateOf<String?>(null) }

    PopupDialog(onDismissRequest = onDismissRequest) {
        TitleText(text = "Editar item", bold = true)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            ZeraTextInput(
                value = name,
                onValueChange = {
                    name = it
                    nameError = null
                },
                label = "Nome",
                imeAction = ImeAction.Next,
                isError = nameError != null,
                errorMessage = nameError,
            )
            ZeraChipsGroup(
                options = ItemDetailsViewModel.conditionOptions,
                selected = condition,
                onSelectedChange = {
                    condition = it
                    conditionError = null
                },
                label = "Condição",
                stacked = true,
            )
            conditionError?.let { CaptionText(text = it, color = MaterialTheme.colorScheme.error) }
            ZeraTextInput(
                value = serialNumber,
                onValueChange = {
                    serialNumber = it
                    serialError = null
                },
                label = "Número de série",
                imeAction = ImeAction.Next,
                isError = serialError != null,
                errorMessage = serialError,
            )
            ZeraTextInput(
                value = notes,
                onValueChange = {
                    notes = it
                    notesError = null
                },
                label = "Observações",
                imeAction = ImeAction.Done,
                isError = notesError != null,
                errorMessage = notesError,
            )
        }
        ZeraButton(
            text = "Salvar",
            onClick = {
                nameError = ItemDetailsViewModel.nameValidationError(name)
                conditionError = if (ItemDetailsViewModel.conditionCodeFrom(condition) == null) {
                    "Selecione a condição do item."
                } else {
                    null
                }
                serialError = ItemDetailsViewModel.serialValidationError(serialNumber)
                notesError = ItemDetailsViewModel.notesValidationError(notes)
                if (nameError == null && conditionError == null && serialError == null && notesError == null) {
                    onConfirm(name, condition, serialNumber, notes)
                }
            },
            style = ZeraColorFamily.Green,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
@Preview(heightDp = 900)
fun ItemDetailsScreenPreview() {
    ZeraTheme {
        ItemDetailsScreen(itemId = "265964")
    }
}
