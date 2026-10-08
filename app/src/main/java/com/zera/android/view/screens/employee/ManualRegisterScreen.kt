package com.zera.android.view.screens.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.inputs.ZeraDateInput
import com.zera.android.view.components.inputs.ZeraDropdownInput
import com.zera.android.view.components.inputs.ZeraInputType
import com.zera.android.view.components.inputs.ZeraRadioButton
import com.zera.android.view.components.inputs.ZeraSliderInput
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.employee.ManualRegisterViewModel

/**
 * Cadastro manual de item do operário (item sem etiqueta para escanear).
 *
 * Modelo, condição e "possui danos?" são dropdowns; só a observação é opcional.
 */
@Composable
fun ManualRegisterScreen(
    viewModel: ManualRegisterViewModel = viewModel(),
) {
    val state by viewModel.state
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Cadastro manual",
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
                        viewModel.onConfirmClick()
                    },
                    fillMaxWidth = true,
                    enabled = state.canSubmit,
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
                text = "Preencha os dados essenciais",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            ZeraTextInput(
                value = state.serialNumber,
                onValueChange = viewModel::onSerialNumberChange,
                label = "Número de série",
                placeholder = "Ex.: 11111111J",
                imeAction = ImeAction.Next,
            )
            ZeraTextInput(
                value = state.itemName,
                onValueChange = viewModel::onItemNameChange,
                label = "Item",
                placeholder = "Nome do item",
                imeAction = ImeAction.Next,
            )
            ZeraDropdownInput(
                value = state.model,
                values = state.modelOptions,
                onValueChange = viewModel::onModelChange,
                label = "Modelo",
                placeholder = "Selecione uma categoria",
            )
            ZeraTextInput(
                value = state.manufacturingYear,
                onValueChange = viewModel::onManufacturingYearChange,
                label = "Ano de fabricação",
                placeholder = "Ex.: 2022",
                type = ZeraInputType.Number,
                imeAction = ImeAction.Next,
            )
            ZeraDateInput(
                value = state.acquisitionDate,
                onValueChange = viewModel::onAcquisitionDateChange,
                label = "Data de aquisição",
                placeholder = "DD/MM/AAAA",
            )
            ZeraDropdownInput(
                value = state.condition,
                values = state.conditionOptions,
                onValueChange = viewModel::onConditionChange,
                label = "Condição",
                placeholder = "Novo, usado ou danificado",
            )
            ZeraSliderInput(
                value = state.usageIntensity,
                onValueChange = viewModel::onUsageIntensityChange,
                label = "Intensidade de uso",
            )
            ZeraRadioButton(
                selected = state.hasDamage,
                onClick = { viewModel.onHasDamageChange(state.hasDamage) },
                label = "Possui danos?",
            )
            if (state.hasDamage) {
                ZeraDropdownInput(
                    value = state.damageType,
                    values = state.damageTypeOptions,
                    onValueChange = viewModel::onDamageTypeChange,
                    label = "Tipo de Dano",
                    placeholder = "Selecione o tipo de dano",
                )
            }
            ZeraTextInput(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                label = "Observação",
                placeholder = "Detalhes opcionais",
                imeAction = ImeAction.Done,
            )
        }
    }
}

@Composable
@Preview(heightDp = 900)
private fun ManualRegisterScreenPreview() {
    ZeraTheme {
        ManualRegisterScreen()
    }
}
