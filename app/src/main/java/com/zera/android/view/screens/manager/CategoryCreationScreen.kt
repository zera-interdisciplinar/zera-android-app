package com.zera.android.view.screens.manager

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
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.manager.CategoryCreationViewModel

@Composable
fun CategoryCreationScreen(
    viewModel: CategoryCreationViewModel = viewModel(),
) {
    val state by viewModel.state
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Cadastro da categoria",
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
                    enabled = !state.isSaving,
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
                text = "Preencha os dados da categoria para usá-la no cadastro de modelos",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            state.errorMessage?.let { message ->
                CaptionText(text = message, color = MaterialTheme.colorScheme.error)
            }
            ZeraTextInput(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = "Categoria",
                placeholder = "Nome da categoria",
                imeAction = ImeAction.Next,
            )
            ZeraTextInput(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = "Descrição",
                placeholder = "Detalhes opcionais",
                imeAction = ImeAction.Done,
            )
        }
    }
}

@Composable
@Preview(heightDp = 800)
private fun CategoryCreationScreenPreview() {
    ZeraTheme {
        CategoryCreationScreen()
    }
}
