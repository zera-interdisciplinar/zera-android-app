package com.zera.android.view.components.overlays

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme

/** Diâmetro do indicador de progresso exibido enquanto a foto carrega. */
private val LoadingIndicatorSize = 48.dp

/**
 * Popup com a foto ampliada (ex.: a foto de um item) e as ações "Trocar foto" e
 * "Remover foto" logo abaixo, como botões terciários.
 *
 * Monta em cima do [PopupDialog], sem o botão de fechar (X) — que ficaria por cima da
 * foto —, então fecha ao tocar fora ou apertar voltar. Segue a mesma convenção de
 * visibilidade: o chamador só compõe o [PhotoViewerDialog] enquanto ele deve aparecer.
 *
 * @param photoUrl URL da foto exibida. A foto aparece inteira ([ContentScale.Fit]) num
 *   quadrado, com um indicador de progresso por cima enquanto carrega.
 * @param onChangeClick ação do botão "Trocar foto".
 * @param onRemoveClick ação do botão "Remover foto".
 * @param onDismissRequest chamado ao tocar fora do popup ou apertar voltar.
 * @param modifier modificador externo opcional.
 * @param contentDescription descrição de acessibilidade da foto.
 */
@Composable
fun PhotoViewerDialog(
    photoUrl: String,
    onChangeClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    var isImageLoading by remember(photoUrl) { mutableStateOf(true) }

    PopupDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        hasDismissButton = false,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(Radius.large))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = photoUrl,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                onState = { imageState ->
                    isImageLoading = imageState is AsyncImagePainter.State.Loading
                },
            )

            if (isImageLoading) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(LoadingIndicatorSize),
                        color = Color.White,
                        strokeWidth = 3.dp,
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            ZeraButton(
                text = "Trocar foto",
                onClick = onChangeClick,
                type = ZeraButtonType.Tertiary,
                style = ZeraColorFamily.Blue,
                modifier = Modifier.weight(1f),
            )
            ZeraButton(
                text = "Remover foto",
                onClick = onRemoveClick,
                type = ZeraButtonType.Tertiary,
                style = ZeraColorFamily.Red,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview
@Composable
private fun PhotoViewerDialogPreview() {
    ZeraTheme {
        PhotoViewerDialog(
            photoUrl = "https://example.com/foto.jpg",
            onChangeClick = {},
            onRemoveClick = {},
            onDismissRequest = {},
        )
    }
}
