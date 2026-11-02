package com.zera.android.view.screens.main.employees

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.feedback.ErrorFeedbackPopup
import com.zera.android.view.components.inputs.ZeraDropdownInput
import com.zera.android.view.components.inputs.ZeraInputType
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.main.employees.EmployeeRegisterState
import com.zera.android.viewmodel.main.employees.EmployeeRegisterViewModel

@Composable
fun EmployeeRegisterScreen(
    viewModel: EmployeeRegisterViewModel = viewModel(),
) {
    val state by viewModel.state

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Cadastrar Operário",
                goBack = true,
                showActions = false,
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
                    .padding(horizontal = Spacing.large, vertical = Spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                ZeraButton(
                    text = if (state.isSubmitting) "Enviando..." else "Criar convite",
                    onClick = viewModel::onCreateInviteClick,
                    fillMaxWidth = true,
                    enabled = !state.isSubmitting,
                )
                CaptionText(text = "O código vale por 7 dias.")
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
                text = "Convide uma pessoa para sua equipe",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            ZeraTextInput(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = "Nome",
                placeholder = "Digite seu nome completo",
                imeAction = ImeAction.Next,
                enabled = !state.isSubmitting,
                isError = state.nameError != null,
                errorMessage = state.nameError,
            )
            ZeraTextInput(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = "E-mail",
                placeholder = "nome.sobrenome@empresa.com",
                type = ZeraInputType.Email,
                imeAction = ImeAction.Done,
                enabled = !state.isSubmitting,
                isError = state.emailError != null,
                errorMessage = state.emailError,
            )
            ZeraDropdownInput(
                value = state.unit,
                values = state.unitOptions,
                onValueChange = viewModel::onUnitChange,
                label = "Unidade",
                placeholder = "Selecione a unidade",
                enabled = !state.isSubmitting,
            )
        }
    }

    state.errorMessage?.let { message ->
        ErrorFeedbackPopup(description = message, onDismissRequest = viewModel::onErrorDismiss)
    }
}

@Composable
@Preview(heightDp = 800)
private fun EmployeeRegisterScreenPreview() {
    val previewViewModel = EmployeeRegisterViewModel().apply {
        state.value = EmployeeRegisterState(
            name = "Gustavo Amex Macal",
            email = "amex@zera.com.br",
            unit = "Matriz",
            unitOptions = listOf("Matriz", "Filial Campinas"),
        )
    }
    ZeraTheme {
        EmployeeRegisterScreen(viewModel = previewViewModel)
    }
}
