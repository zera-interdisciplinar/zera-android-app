package com.zera.android.view.screens.shared

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.zera.android.view.theme.palette
import com.zera.android.viewmodel.shared.ProfileViewModel
import com.zera.android.viewmodel.shared.ProfileViewModel.Companion.roleCaption

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val errorPalette = ZeraColorFamily.Red.palette()
    val successPalette = ZeraColorFamily.Green.palette()

    LaunchedEffect(state.snackbarMessage) {
        val message = state.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.onSnackbarShown()
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        if (bytes != null) {
            viewModel.onPhotoPicked(bytes, mime)
        }
    }

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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                val palette = if (state.snackbarIsError) errorPalette else successPalette
                Snackbar(
                    snackbarData = data,
                    containerColor = palette.base,
                    contentColor = palette.onBase,
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
            if (state.isLoading) {
                CircularProgressIndicator()
            }

            state.errorMessage?.let { message ->
                CaptionText(text = message, color = MaterialTheme.colorScheme.error)
            }

            Avatar(
                initials = state.initials,
                photoUrl = state.photoUrl,
                isLoading = state.isUploadingPhoto,
                onClick = {
                    if (!state.isSaving && !state.isUploadingPhoto) {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    }
                },
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
