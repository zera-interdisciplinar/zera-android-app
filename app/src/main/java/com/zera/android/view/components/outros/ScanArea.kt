package com.zera.android.view.components.outros

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme

private val ScanAreaAspectRatio = 328f / 248f
private val ScanFrameHeight = 84.dp

/**
 * Área de escaneamento: um quadrado escuro com duas aparências.
 *
 * - Com [hasCameraPermission] `true`: mostra a imagem da câmera traseira (CameraX), a
 *   moldura onde a etiqueta deve ser posicionada e o [statusText] embaixo.
 * - Com `false`: mostra o aviso "Sem acesso à câmera" e o botão "Permitir câmera", que
 *   chama [onRequestPermission].
 *
 * O componente **não** pede a permissão sozinho: quem o usa controla [hasCameraPermission]
 * e faz o pedido em [onRequestPermission].
 *
 * @param hasCameraPermission se o app já tem a permissão `CAMERA`.
 * @param onRequestPermission ação do botão "Permitir câmera" (deve pedir a permissão).
 * @param modifier modificador externo opcional.
 * @param statusText texto exibido na parte de baixo da área quando a câmera está ligada.
 */
@Composable
fun ScanArea(
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
    statusText: String = "Leitura automática ativada",
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ScanAreaAspectRatio)
            .clip(RoundedCornerShape(Radius.xLarge))
            .background(MaterialTheme.colorScheme.onSurface),
    ) {
        if (hasCameraPermission) {
            CameraPreview(modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = Spacing.xLarge)
                    .padding(horizontal = Spacing.large)
                    .fillMaxWidth()
                    .height(ScanFrameHeight)
                    .border(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(Radius.large),
                    ),
            )
            BodyText(
                text = statusText,
                bold = true,
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Spacing.medium)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(Radius.medium),
                    )
                    .padding(horizontal = Spacing.small, vertical = Spacing.micro),
            )
        } else {
            NoCameraAccess(
                onRequestPermission = onRequestPermission,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/** Aviso de falta de permissão com o botão que pede acesso à câmera. */
@Composable
private fun NoCameraAccess(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = Spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.large, Alignment.CenterVertically),
    ) {
        BodyText(
            text = "Sem acesso à câmera",
            bold = true,
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            textAlign = TextAlign.Center,
        )
        ZeraButton(
            text = "Permitir câmera",
            onClick = onRequestPermission,
            fillMaxWidth = true,
            style = ZeraColorFamily.Yellow,
        )
    }
}

/**
 * Imagem ao vivo da câmera traseira, ligada ao ciclo de vida da tela: abre quando entra
 * na composição e é desligada quando sai (ou quando o lifecycle para).
 *
 * No preview do Android Studio ([LocalInspectionMode]) não existe câmera nem CameraX, então
 * nada é desenhado — a área fica só com o fundo escuro.
 */
@Composable
private fun CameraPreview(modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) {
        Box(modifier)
    } else {
        LiveCameraPreview(modifier)
    }
}

@Composable
private fun LiveCameraPreview(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // COMPATIBLE usa TextureView, que respeita o clip de cantos arredondados do pai.
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(lifecycleOwner) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        val preview = Preview.Builder().build()
        var cameraProvider: ProcessCameraProvider? = null

        providerFuture.addListener({
            try {
                val provider = providerFuture.get()
                cameraProvider = provider
                preview.surfaceProvider = previewView.surfaceProvider
                provider.unbind(preview)
                provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview)
            } catch (_: Exception) {
                // Sem câmera disponível (ou em uso por outro app): a área segue só com o fundo escuro.
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose { cameraProvider?.unbind(preview) }
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}

@Composable
@ComposePreview(showBackground = true)
private fun ScanAreaNoPermissionPreview() {
    ZeraTheme {
        ScanArea(
            hasCameraPermission = false,
            onRequestPermission = {},
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
