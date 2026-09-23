package com.zera.android.view.screens.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
            Avatar(initials = state.initials, photoUrl = state.photoUrl)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.micro),
            ) {
                HeadlineText(text = state.displayName, bold = true)
                CaptionText(text = "${state.role} · ${state.company}")
            }

            ZeraButton(
                text = "Alterar foto",
                onClick = viewModel::onChangePhotoClick,
                style = ZeraColorFamily.Yellow,
                type = ZeraButtonType.Secondary,
            )
            ZeraButton(
                text = "Configurações",
                onClick = viewModel::onSettingsClick,
                style = ZeraColorFamily.Blue,
                type = ZeraButtonType.Secondary,
            )

            EditableFieldRow(
                label = "Nome",
                value = state.fullName,
                onEditClick = viewModel::onEditNameClick,
            )
            EditableFieldRow(
                label = "E-mail",
                value = state.email,
                inputType = ZeraInputType.Email,
                onEditClick = viewModel::onEditEmailClick,
                validate = { email ->
                    if (email.contains("@")) null else "E-mail inválido"
                },
            )
            EditableFieldRow(
                label = "Telefone",
                value = state.phone,
                inputType = ZeraInputType.Phone,
                onEditClick = viewModel::onEditPhoneClick,
                validate = { phone ->
                    if (phone.count(Char::isDigit) >= 10) null else "Telefone inválido"
                },
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
