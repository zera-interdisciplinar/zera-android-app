package com.zera.android.view.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.components.outros.ItemStatusTag
import com.zera.android.view.components.outros.Tag
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette
import com.zera.android.view.theme.icons.ZeraIcon

/** Tamanho (largura e altura) do quadrado de miniatura. */
private val ThumbnailSize = 72.dp

/**
 * Card de destaque no topo de uma tela de detalhe de item: miniatura à esquerda,
 * nome/subtítulo do item e uma [Tag] de status à direita.
 *
 * @param itemName nome do item, em destaque.
 * @param itemSubtitle subtítulo complementar (ex.: o modelo do item).
 * @param status status de workflow do item (`PENDING_APPROVAL`, `IN_STOCK`, …),
 *   exibido como [ItemStatusTag]. Independente da [condition].
 * @param condition condição física do item (ex.: "Usado"), exibida como [Tag] no card.
 * @param modifier modificador externo opcional. O card já ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 * @param icon ícone do catálogo [ZeraIcon] exibido na miniatura. Quando `null`
 *   (padrão), usa [ZeraIcon.Placeholder]. Ignorado quando há [photoUrl].
 * @param photoUrl URL da foto do item. Quando informada, a miniatura mostra a foto
 *   (recortada para preencher o quadrado) no lugar do [icon], com um indicador de
 *   progresso por cima enquanto ela carrega; quando `null` ou em branco (padrão),
 *   mostra o ícone.
 * @param onPhotoClick chamado ao tocar na miniatura (ex.: para abrir a foto ampliada).
 *   A miniatura só fica clicável quando há [photoUrl] e este callback não é `null` (padrão).
 */
@Composable
fun ItemSummaryCard(
    itemName: String,
    itemSubtitle: String,
    status: ItemStatus? = null,
    condition: String = "",
    modifier: Modifier = Modifier,
    icon: ZeraIcon? = null,
    photoUrl: String? = null,
    onPhotoClick: (() -> Unit)? = null,
) {
    var isImageLoading by remember(photoUrl) { mutableStateOf(!photoUrl.isNullOrBlank()) }
    val thumbnailClick = onPhotoClick?.takeIf { !photoUrl.isNullOrBlank() }

    ZeraBox(
        modifier = modifier.fillMaxWidth(),
        style = ZeraColorFamily.Blue,
        shape = RoundedCornerShape(Radius.xLarge),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Surface(
                modifier = Modifier.size(ThumbnailSize),
                shape = RoundedCornerShape(Radius.large),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .let { if (thumbnailClick != null) it.clickable(onClick = thumbnailClick) else it },
                    contentAlignment = Alignment.Center,
                ) {
                    if (photoUrl.isNullOrBlank()) {
                        ZeraIcon(
                            icon = icon ?: ZeraIcon.Placeholder,
                            contentDescription = null,
                            tint = ZeraColorFamily.Blue.palette().onContainer,
                        )
                    } else {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = itemName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            onState = { imageState ->
                                isImageLoading = imageState is AsyncImagePainter.State.Loading
                            },
                        )
                    }

                    if (isImageLoading) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(alpha = 0.45f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(ThumbnailSize * 0.32f),
                                color = Color.White,
                                strokeWidth = 3.dp,
                            )
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.micro)) {
                    TitleText(text = itemName, bold = true, color = LocalContentColor.current)
                    BodyText(text = itemSubtitle, color = LocalContentColor.current)
                }
                if (condition.isNotBlank() || status != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        if (condition.isNotBlank()) {
                            Tag(text = condition, style = conditionTagStyle(condition))
                        }
                        status?.let { ItemStatusTag(status = it) }
                    }
                }
            }
        }
    }
}

private fun conditionTagStyle(condition: String): ZeraColorFamily = when (condition) {
    "Novo" -> ZeraColorFamily.Green
    "Danificado" -> ZeraColorFamily.Red
    else -> ZeraColorFamily.Yellow
}

@Preview(showBackground = true)
@Composable
private fun ItemSummaryCardPreview() {
    ZeraTheme {
        ItemSummaryCard(
            itemName = "Placa de vídeo",
            itemSubtitle = "Notebook Mac",
            status = ItemStatus.PendingApproval,
            condition = "Usado",
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
