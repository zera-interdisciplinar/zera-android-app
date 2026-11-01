package com.zera.android.view.screens.main.items.manager

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.cards.ItemSummaryCard
import com.zera.android.view.components.forms.EditItemForm
import com.zera.android.view.components.inputs.ZeraInputPopup
import com.zera.android.view.components.lists.EditableFieldRow
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.navigation.SideBarOverlay
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.components.overlays.BottomSheet
import com.zera.android.view.components.overlays.PhotoViewerDialog
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.media.PhotoCapture
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.main.items.ItemDetailsState
import com.zera.android.viewmodel.main.items.ItemDetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// Alturas aproximadas dos blocos reais; o esqueleto só indica a disposição.
private val SkeletonSummaryCardHeight = 120.dp
private val SkeletonFieldRowHeight = 56.dp
private const val SkeletonFieldRowCount = 9

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailsScreen(
    itemId: String,
    viewModel: ItemDetailsViewModel = viewModel()
) {
    val state by viewModel.state
    var showSideBar by rememberSaveable { mutableStateOf(false) }
    var showRejectPopup by remember { mutableStateOf(false) }
    val actionsEnabled = !state.isLoading && !state.isSubmitting && state.itemId.isNotBlank()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Caminho do arquivo que a câmera está preenchendo; salvo para sobreviver à morte do processo.
    var capturePath by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { saved ->
        val file = capturePath?.let(::File)
        capturePath = null
        if (file == null) return@rememberLauncherForActivityResult
        if (!saved) {
            file.delete()
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val bytes = withContext(Dispatchers.IO) {
                PhotoCapture.readCompressed(file).also { file.delete() }
            }
            viewModel.onPhotoTaken(bytes)
        }
    }

    val launchCamera = {
        val (file, uri) = PhotoCapture.newTarget(context)
        capturePath = file.path
        cameraLauncher.launch(uri)
    }

    // Com CAMERA declarada no manifest, o TakePicture lança SecurityException sem a permissão concedida.
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) launchCamera() else viewModel.onCameraPermissionDenied()
    }

    val onPhotoAdd = {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) launchCamera() else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                UpperNavBar(
                    title = "Detalhes do Item",
                    goBack = true,
                    onSideBarClick = { showSideBar = true },
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
                                text = if (state.isSubmitting) "Enviando..." else "Aprovar",
                                onClick = viewModel::onApproveClick,
                                style = ZeraColorFamily.Green,
                                enabled = actionsEnabled,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    ZeraButton(
                        text = "Editar",
                        onClick = viewModel::onEditClick,
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
                if (state.isLoading) {
                    ItemDetailsSkeleton()
                } else {
                    state.errorMessage?.let { message ->
                        CaptionText(text = message, color = MaterialTheme.colorScheme.error)
                    }

                    ItemSummaryCard(
                        itemName = state.itemName,
                        itemSubtitle = state.itemSubtitle,
                        status = state.status,
                        condition = state.condition,
                        photoUrl = state.photoUrl,
                        onPhotoAdd = onPhotoAdd,
                        onPhotoInspect = viewModel::onPhotoInspect,
                        isPhotoUploading = state.isUploadingPhoto,
                    )

                    if (state.canSendToMaintenance) {
                        ZeraButton(
                            text = if (state.isSubmitting) "Enviando..." else "Colocar em manutenção",
                            onClick = viewModel::onSendToMaintenanceClick,
                            type = ZeraButtonType.Secondary,
                            style = ZeraColorFamily.Yellow,
                            enabled = actionsEnabled,
                            fillMaxWidth = true,
                        )
                    }

                    EditableFieldRow(label = "Nome", value = state.itemName)
                    EditableFieldRow(label = "Categoria", value = state.category)
                    EditableFieldRow(label = "Material", value = state.material)
                    EditableFieldRow(label = "Condição", value = state.condition)
                    EditableFieldRow(label = "Número de série", value = state.serialNumber)
                    EditableFieldRow(label = "Data de aquisição", value = state.acquiredAt)
                    EditableFieldRow(label = "Observações", value = state.notes)
                    EditableFieldRow(label = "Cadastrado Por", value = state.registeredBy)
                    EditableFieldRow(label = "Data de Cadastro", value = state.registeredAt)
                }
            }
        }

        SideBarOverlay(
            visible = showSideBar,
            onDismiss = { showSideBar = false },
            items = state.sideBarItems,
            currentRoute = Route.ItemDetails(itemId),
        )
    }

    state.editDraft?.let { draft ->
        BottomSheet(
            onDismissRequest = viewModel::onEditDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            EditItemForm(
                name = draft.name,
                onNameChange = viewModel::onEditNameChange,
                condition = draft.condition,
                conditionOptions = ItemDetailsViewModel.conditionOptions,
                onConditionChange = viewModel::onEditConditionChange,
                usageIntensity = draft.usageIntensity,
                onUsageIntensityChange = viewModel::onEditUsageIntensityChange,
                hasDamage = draft.hasDamage,
                onHasDamageChange = viewModel::onEditHasDamageChange,
                damageType = draft.damageType,
                damageOptions = ItemDetailsViewModel.damageOptions,
                onDamageTypeChange = viewModel::onEditDamageTypeChange,
                notes = draft.notes,
                onNotesChange = viewModel::onEditNotesChange,
                onSave = viewModel::onEditSave,
                onCancel = viewModel::onEditDismiss,
                modifier = Modifier.verticalScroll(rememberScrollState()),
                nameError = draft.nameError,
                damageTypeError = draft.damageTypeError,
                notesError = draft.notesError,
                errorMessage = state.errorMessage,
                isSubmitting = state.isSubmitting,
            )
        }
    }

    val photoUrl = state.photoUrl
    if (state.showPhotoViewer && photoUrl != null) {
        PhotoViewerDialog(
            photoUrl = photoUrl,
            onChangeClick = {
                viewModel.onPhotoViewerDismiss()
                onPhotoAdd()
            },
            onRemoveClick = viewModel::onRemovePhotoClick,
            onDismissRequest = viewModel::onPhotoViewerDismiss,
            contentDescription = state.itemName,
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

/** Esqueleto do card-resumo e das linhas de campos, emitido direto na `Column` da tela. */
@Composable
private fun ItemDetailsSkeleton() {
    SkeletonBox(height = SkeletonSummaryCardHeight, borderRadius = Radius.large)
    repeat(SkeletonFieldRowCount) {
        SkeletonBox(height = SkeletonFieldRowHeight)
    }
}

// Com itemId em branco a tela não dispara a carga real, então o state injetado é preservado.
@Composable
@Preview(heightDp = 900)
fun ItemDetailsScreenPreview() {
    val previewViewModel = ItemDetailsViewModel().apply {
        state.value = ItemDetailsState(
            itemId = "2659646",
            itemName = "Placa de vídeo",
            itemSubtitle = "RTX 3060",
            status = ItemStatus.PendingApproval,
            category = "Informática",
            material = "Placa de circuito",
            condition = "Usado",
            serialNumber = "SN-48213",
            acquiredAt = "1 de Jul 2026",
            notes = "Pequeno arranhão na lateral.",
            registeredBy = "Carlos Lima",
            registeredAt = "12/10/2026",
            canReview = true,
            isLoading = false,
        )
    }
    ZeraTheme {
        ItemDetailsScreen(itemId = "", viewModel = previewViewModel)
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [ItemDetailsState] nasce `true`). */
@Composable
@Preview(heightDp = 900)
fun ItemDetailsScreenLoadingPreview() {
    val previewViewModel = ItemDetailsViewModel().apply {
        state.value = ItemDetailsState()
    }
    ZeraTheme {
        ItemDetailsScreen(itemId = "265964", viewModel = previewViewModel)
    }
}
