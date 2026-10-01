package com.zera.android.view.screens.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.inputs.ZeraInputType
import com.zera.android.view.components.lists.EditableFieldRow
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.outros.Avatar
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.shared.ProfileViewModel
import com.zera.android.viewmodel.shared.ProfileViewModel.Companion.roleCaption

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Perfil",
                goBack = true,
                showActions = false,
                onBackClick = viewModel::onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.small),
            )
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
            if (state.isLoading) {
                CircularProgressIndicator()
            }

            state.errorMessage?.let { message ->
                CaptionText(text = message, color = MaterialTheme.colorScheme.error)
            }

            Avatar(
                initials = state.initials,
                photoUrl = state.photoUrl,
                onClick = viewModel::onChangePhotoClick,
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.micro),
            ) {
                HeadlineText(text = state.displayName, bold = true)
                CaptionText(text = roleCaption(state.role, state.company))
            }

            ZeraButton(
                text = "Configurações",
                onClick = viewModel::onSettingsClick,
                style = ZeraColorFamily.Blue,
                type = ZeraButtonType.Secondary,
            )

            EditableFieldRow(
                label = "Nome",
                value = state.fullName,
                onEditClick = if (state.canEditName) ({}) else null,
                validate = { name ->
                    if (name.trim().isNotEmpty()) null else "Nome inválido"
                },
                onConfirm = viewModel::onNameConfirm,
            )
            EditableFieldRow(
                label = "E-mail",
                value = state.email,
                inputType = ZeraInputType.Email,
                onEditClick = if (state.canEditEmail) ({}) else null,
                validate = { email ->
                    if (email.contains("@")) null else "E-mail inválido"
                },
                onConfirm = viewModel::onEmailConfirm,
            )
            EditableFieldRow(
                label = "Telefone",
                value = state.phone,
                inputType = ZeraInputType.Phone,
                onEditClick = if (state.canEditPhone) ({}) else null,
                validate = { phone ->
                    val digits = phone.count(Char::isDigit)
                    if (digits in 10..11) null else "Telefone inválido"
                },
                onConfirm = viewModel::onPhoneConfirm,
            )
            EditableFieldRow(label = "Cargo", value = state.position)
        }
    }
}

@Composable
@Preview(heightDp = 900)
fun ProfileScreenPreview() {
    ZeraTheme {
        ProfileScreen()
    }
}
