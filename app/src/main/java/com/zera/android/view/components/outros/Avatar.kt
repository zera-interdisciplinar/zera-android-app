package com.zera.android.view.components.outros

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette

/** Diâmetro padrão do avatar. */
private val DefaultAvatarSize = 120.dp

/** Diâmetro do selo de edição exibido no canto inferior direito quando [Avatar] é clicável. */
private val EditBadgeSize = 32.dp

/** Tamanho do ícone dentro do selo de edição. */
private val EditBadgeIconSize = 16.dp

/**
 * Avatar circular do usuário: mostra a foto quando [photoUrl] é informada, ou as
 * [initials] como substituto quando não há foto.
 *
 * @param initials iniciais a exibir quando não há foto (ex.: "NF"). O cálculo a
 *   partir do nome completo é responsabilidade de quem chama (ver `ViewModel`),
 *   não deste componente. Também usada como descrição de acessibilidade da foto.
 * @param modifier modificador externo opcional.
 * @param photoUrl URL da foto de perfil. Quando `null` (padrão), mostra [initials].
 * @param size diâmetro do círculo.
 * @param style família de cor do avatar. Ver [ZeraColorFamily].
 * @param onClick chamado ao tocar no avatar (ex.: para alterar a foto). Quando `null`
 *   (padrão), o avatar não é clicável e não exibe o selo de edição. Quando informado,
 *   um selo com ícone de lápis aparece no canto inferior direito, indicando que o
 *   avatar pode ser editado.
 * @param isLoading quando `true`, cobre o avatar com um indicador de progresso
 *   (upload da foto em andamento).
 */
@Composable
fun Avatar(
    initials: String,
    modifier: Modifier = Modifier,
    photoUrl: String? = null,
    size: Dp = DefaultAvatarSize,
    style: ZeraColorFamily = ZeraColorFamily.Blue,
    onClick: (() -> Unit)? = null,
    isLoading: Boolean = false,
) {
    val palette = style.palette()
    var isImageLoading by remember(photoUrl) { mutableStateOf(!photoUrl.isNullOrBlank()) }
    Box(
    ){
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape),
        ) {
            Surface(
                modifier = Modifier
                    .matchParentSize()
                    .let { if (onClick != null) it.clickable(onClick = onClick) else it },
                shape = CircleShape,
                color = palette.container,
                contentColor = palette.onContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (photoUrl != null) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = initials,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            onState = { imageState ->
                                isImageLoading = imageState is AsyncImagePainter.State.Loading
                            },
                        )
                    } else {
                        HeadlineText(text = initials, bold = true, color = palette.onContainer)
                    }
                }
            }

            if (isLoading || isImageLoading) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size * 0.32f),
                        color = Color.White,
                        strokeWidth = 3.dp,
                    )
                }
            }
        }
        if (onClick != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(EditBadgeSize)
                    .clip(CircleShape)
                    .background(palette.base),
                contentAlignment = Alignment.Center,
            ) {
                ZeraIcon(
                    icon = ZeraIcon.Edit,
                    contentDescription = null,
                    size = EditBadgeIconSize,
                    tint = palette.onBase,
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun AvatarInitialsPreview() {
    ZeraTheme {
        Avatar(initials = "KM")
    }
}

@Preview(showBackground = true)
@Composable
private fun AvatarPhotoPreview() {
    ZeraTheme {
        Avatar(initials = "NF", photoUrl = "https://example.com/foto.jpg")
    }
}

@Preview(showBackground = true)
@Composable
private fun AvatarEditablePreview() {
    ZeraTheme {
        Avatar(initials = "KM", onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun AvatarLoadingPreview() {
    ZeraTheme {
        Avatar(initials = "KM", onClick = {}, isLoading = true)
    }
}
