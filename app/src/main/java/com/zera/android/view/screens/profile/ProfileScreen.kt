package com.zera.android.view.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.feedback.ErrorFeedbackPopup
import com.zera.android.view.components.forms.EditProfileForm
import com.zera.android.view.components.lists.EditableFieldRow
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.outros.Avatar
import com.zera.android.view.components.overlays.ConfirmationPopup
import com.zera.android.view.components.overlays.BottomSheet
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette
import com.zera.android.viewmodel.profile.ProfileState
import com.zera.android.viewmodel.profile.ProfileViewModel
import com.zera.android.viewmodel.profile.ProfileViewModel.Companion.roleCaption

// Alturas aproximadas dos blocos reais; o esqueleto só indica a disposição.
private val SkeletonAvatarSize = 120.dp
private val SkeletonButtonHeight = 48.dp
private val SkeletonFieldRowHeight = 56.dp
private const val SkeletonFieldRowCount = 4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
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
                Snackbar(
                    snackbarData = data,
                    containerColor = successPalette.base,
                    contentColor = successPalette.onBase,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Spacing.medium, vertical = Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            // O conteúdo rola dentro do peso restante; o botão de sair fica sempre colado embaixo.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                if (state.isLoading) {
                    ProfileSkeleton()
                } else {

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
                        text = "Editar",
                        onClick = viewModel::onEditClick,
                        style = ZeraColorFamily.Blue,
                        type = ZeraButtonType.Secondary,
                        enabled = !state.isSaving,
                    )

                    ZeraButton(
                        text = "Configurações",
                        onClick = viewModel::onSettingsClick,
                        style = ZeraColorFamily.Blue,
                        type = ZeraButtonType.Secondary,
                    )

                    EditableFieldRow(label = "Nome", value = state.fullName)
                    EditableFieldRow(label = "E-mail", value = state.email)
                    EditableFieldRow(label = "Telefone", value = state.phone)
                    EditableFieldRow(label = "Cargo", value = state.position)
                }
            }

            if (state.isLoading) {
                SkeletonBox(height = SkeletonButtonHeight, borderRadius = Radius.large)
            } else {
                ZeraButton(
                    text = "Sair da conta",
                    onClick = viewModel::onLogoutClick,
                    fillMaxWidth = true,
                    style = ZeraColorFamily.Red,
                    type = ZeraButtonType.Secondary,
                    enabled = !state.isSaving,
                    loading = state.isLoggingOut,
                )
            }
        }
    }

    state.editDraft?.let { draft ->
        BottomSheet(
            onDismissRequest = viewModel::onEditDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            EditProfileForm(
                name = draft.fullName,
                onNameChange = viewModel::onEditNameChange,
                email = draft.email,
                onEmailChange = viewModel::onEditEmailChange,
                onSave = viewModel::onEditSave,
                onCancel = viewModel::onEditDismiss,
                modifier = Modifier.verticalScroll(rememberScrollState()),
                nameEnabled = state.canEditName,
                emailEnabled = state.canEditEmail,
                nameError = draft.nameError,
                emailError = draft.emailError,
                isSubmitting = state.isSaving,
            )
        }
    }

    if (state.showLogoutDialog) {
        ConfirmationPopup(
            title = "Sair da conta?",
            message = "Você precisará entrar novamente para acessar o aplicativo.",
            confirmButtonText = "Sair",
            onConfirm = viewModel::onLogoutConfirm,
            onDismissRequest = viewModel::onLogoutDismiss,
        )
    }

    state.errorMessage?.let { message ->
        ErrorFeedbackPopup(description = message, onDismissRequest = viewModel::onErrorDismiss)
    }
}

/**
 * Esqueleto do avatar, nome/cargo, botões "Editar" e "Configurações" e campos, emitido direto na `Column` da tela.
 * O botão "Sair da conta" fica fora da rolagem e tem seu próprio esqueleto em [ProfileScreen].
 */
@Composable
private fun ProfileSkeleton() {
    SkeletonBox(
        width = SkeletonAvatarSize,
        height = SkeletonAvatarSize,
        borderRadius = SkeletonAvatarSize,
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.micro),
    ) {
        SkeletonBox(width = 180.dp, height = 28.dp)
        SkeletonBox(width = 120.dp, height = 14.dp)
    }

    repeat(2) {
        SkeletonBox(width = 160.dp, height = SkeletonButtonHeight, borderRadius = Radius.large)
    }

    repeat(SkeletonFieldRowCount) {
        SkeletonBox(height = SkeletonFieldRowHeight)
    }
}

@Composable
@Preview(heightDp = 900)
fun ProfileScreenPreview() {
    val previewViewModel = ProfileViewModel().apply {
        state.value = ProfileState(
            displayName = "Carlos Lima",
            fullName = "Carlos Eduardo Lima",
            role = "EMPLOYEE",
            company = "Zera Recicla",
            initials = "CL",
            email = "carlos@zera.com",
            phone = "(11) 91234-5678",
            position = "Operador",
            isLoading = false,
        )
    }
    ZeraTheme {
        ProfileScreen(viewModel = previewViewModel)
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [ProfileState] é `true` ao abrir). */
@Composable
@Preview(heightDp = 900)
fun ProfileScreenLoadingPreview() {
    val previewViewModel = ProfileViewModel().apply {
        state.value = ProfileState(isLoading = true)
    }
    ZeraTheme {
        ProfileScreen(viewModel = previewViewModel)
    }
}
