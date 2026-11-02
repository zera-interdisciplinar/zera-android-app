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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.feedback.ErrorFeedbackPopup
import com.zera.android.view.components.lists.EditableFieldRow
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.outros.Avatar
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.main.employees.EmployeeDetailsState
import com.zera.android.viewmodel.main.employees.EmployeeDetailsViewModel

@Composable
fun EmployeeDetailsScreen(
    employeeId: String,
    viewModel: EmployeeDetailsViewModel = viewModel(),
) {
    val state by viewModel.state

    LaunchedEffect(employeeId) {
        viewModel.loadEmployee(employeeId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Operário",
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
            ) {
                ZeraButton(
                    text = "Excluir perfil",
                    onClick = viewModel::onDeleteClick,
                    style = ZeraColorFamily.Red,
                    enabled = !state.isLoading,
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Avatar(
                initials = state.initials,
                photoUrl = state.photoUrl,
                style = ZeraColorFamily.Green,
                isLoading = state.isLoading,
            )
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                EditableFieldRow(label = "Nome", value = state.name)
                EditableFieldRow(label = "E-mail", value = state.email)
                EditableFieldRow(label = "Telefone", value = state.phone)
                EditableFieldRow(label = "Cargo", value = state.position)
            }
        }
    }

    state.errorMessage?.let { message ->
        ErrorFeedbackPopup(description = message, onDismissRequest = viewModel::onErrorDismiss)
    }
}

// Com employeeId em branco a tela não dispara a carga real, então o state injetado é preservado.
@Composable
@Preview(heightDp = 800)
private fun EmployeeDetailsScreenPreview() {
    val previewViewModel = EmployeeDetailsViewModel().apply {
        state.value = EmployeeDetailsState(
            employeeId = "user-1",
            name = "Gustavo Amex Macal",
            initials = "GM",
            email = "amex@zera.com.br",
            phone = "(11) 97520-6322",
            position = "Operário",
            isLoading = false,
        )
    }
    ZeraTheme {
        EmployeeDetailsScreen(employeeId = "", viewModel = previewViewModel)
    }
}
