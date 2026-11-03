package com.zera.android.view.screens.main.models

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.feedback.ErrorFeedbackPopup
import com.zera.android.view.components.forms.SelectMaterialsForm
import com.zera.android.view.components.inputs.ZeraDropdownInput
import com.zera.android.view.components.inputs.ZeraSelectInput
import com.zera.android.view.components.inputs.ZeraInputType
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.overlays.BottomSheet
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.main.models.ModelCreationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelCreationScreen(
    viewModel: ModelCreationViewModel = viewModel(),
) {
    val state by viewModel.state
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Cadastro do modelo",
                goBack = true,
                showActions = false,
                modifier = Modifier.statusBarsPadding(),
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = Spacing.large, vertical = Spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                ZeraButton(
                    text = "Concluir Cadastro",
                    onClick = {
                        keyboardController?.hide()
                        viewModel.onCreateClick()
                    },
                    fillMaxWidth = true,
                    enabled = !state.isSaving && !state.isLoading,
                )
                ZeraButton(
                    text = "Cancelar",
                    onClick = viewModel::onCancelClick,
                    fillMaxWidth = true,
                    type = ZeraButtonType.Tertiary,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.large, vertical = Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            BodyText(
                text = "Preencha os dados essenciais para que possa ser reutilizado nos próximos itens",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            ZeraTextInput(
                value = state.modelName,
                onValueChange = viewModel::onModelNameChange,
                label = "Modelo",
                placeholder = "Nome do modelo",
                imeAction = ImeAction.Next,
                isError = state.nameError != null,
                errorMessage = state.nameError,
            )
            ZeraDropdownInput(
                value = state.category,
                values = state.categoryOptions,
                onValueChange = viewModel::onCategoryChange,
                label = "Categoria",
                placeholder = "Selecione uma categoria",
                isError = state.categoryError != null,
                errorMessage = state.categoryError,
            )
            ZeraSelectInput(
                value = state.materialsLabel,
                onClick = viewModel::onMaterialFieldClick,
                label = "Materiais",
                placeholder = "Selecione os materiais",
                enabled = state.materialOptions.isNotEmpty(),
                isError = state.materialError != null,
                errorMessage = state.materialError,
            )
            ZeraTextInput(
                value = state.brand,
                onValueChange = viewModel::onBrandChange,
                label = "Marca",
                placeholder = "Ex: Eletrônico, mecânico, chip",
                imeAction = ImeAction.Next,
                isError = state.brandError != null,
                errorMessage = state.brandError,
            )
            ZeraTextInput(
                value = state.weight,
                onValueChange = viewModel::onWeightChange,
                label = "Peso estimado (kg)",
                placeholder = "Opcional. Ex: 2,5",
                type = ZeraInputType.Decimal,
                imeAction = ImeAction.Next,
                isError = state.weightError != null,
                errorMessage = state.weightError,
            )
            ZeraTextInput(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                label = "Observação",
                placeholder = "Detalhes opcionais",
                imeAction = ImeAction.Done,
                isError = state.notesError != null,
                errorMessage = state.notesError,
            )
        }
    }

    if (state.isMaterialSheetVisible) {
        BottomSheet(
            onDismissRequest = viewModel::onMaterialSheetDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            SelectMaterialsForm(
                options = state.materialOptions,
                selected = state.draftMaterials,
                onSelectedChange = viewModel::onDraftMaterialsChange,
                onConfirm = viewModel::onMaterialsConfirm,
            )
        }
    }

    state.errorMessage?.let { message ->
        ErrorFeedbackPopup(description = message, onDismissRequest = viewModel::onErrorDismiss)
    }
}

@Composable
@Preview(heightDp = 800)
private fun ModelCreationScreenPreview() {
    ZeraTheme {
        ModelCreationScreen()
    }
}