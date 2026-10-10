package com.zera.android.view.screens.employee

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.outros.ScanArea
import com.zera.android.view.components.texts.SubtitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.employee.ScanViewModel

/**
 * Tela de escanear item do operário.
 *
 * O quadrado escuro no centro é a [ScanArea]: com permissão de câmera, mostra a câmera do
 * usuário; sem ela, mostra o botão "Permitir câmera", que faz o pedido.
 */
@Composable
fun ScanScreen(
    viewModel: ScanViewModel = viewModel()
) {
    val state by viewModel.state
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = viewModel::onCameraPermissionChanged,
    )

    // O usuário pode conceder/revogar a permissão pelas configurações e voltar ao app,
    // então reconferimos sempre que a tela é retomada (inclusive na primeira vez).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onCameraPermissionChanged(
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED
                )
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    EmployeeScaffold(
        title = "Scanear item",
        currentRoute = Route.Scan,
        goBack = true,
        showActions = false,
        fabIcon = null,
        scrollable = false,
    ) {
        SubtitleText(
            text = "Posicione a etiqueta dentro da área",
            modifier = Modifier.padding(horizontal = Spacing.small),
        )

        Spacer(modifier = Modifier.weight(1f))

        ScanArea(
            hasCameraPermission = state.hasCameraPermission,
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            // TODO: repassar para o ScanViewModel (tratamento da leitura ainda não existe)
            onBarcodeDetected = {},
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            ZeraButton(
                text = "Cadastrar sem etiqueta",
                onClick = viewModel::onRegisterWithoutLabelClick,
                fillMaxWidth = true,
            )
            ZeraButton(
                text = "Cadastrar modelo",
                onClick = viewModel::onRegisterModelClick,
                type = ZeraButtonType.Tertiary,
                fillMaxWidth = true,
            )
        }

        Spacer(modifier = Modifier.height(Spacing.large))
    }
}

@Composable
@Preview(heightDp = 800, showBackground = true)
fun ScanScreenPreview() {
    ZeraTheme {
        ScanScreen()
    }
}
