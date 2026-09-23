package com.zera.android.view.components.outros

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette

/** Diâmetro padrão do avatar. */
private val DefaultAvatarSize = 120.dp

/**
 * Avatar circular do usuário: mostra a foto quando [photoUrl] é informada, ou as
 * [initials] como substituto quando não há foto.
 *
 * @param initials iniciais a exibir quando não há foto (ex.: "NF"). O cálculo a
 *   partir do nome completo é responsabilidade de quem chama (ver `ViewModel`),
 *   não deste componente. Também usada como descrição de acessibilidade da foto.
 * @param modifier modificador externo opcional.
 * @param photoUrl URL da foto de perfil. Quando `null` (padrão), mostra [initials].
 *   **Ainda não carrega a foto de fato** — não há biblioteca de carregamento de
 *   imagem (ex.: Coil) no projeto; por enquanto só troca as iniciais por um ícone
 *   de placeholder. `TODO`: renderizar a foto de verdade quando essa lib for adicionada.
 * @param size diâmetro do círculo.
 * @param style família de cor do avatar. Ver [ZeraColorFamily].
 */
@Composable
fun Avatar(
    initials: String,
    modifier: Modifier = Modifier,
    photoUrl: String? = null,
    size: Dp = DefaultAvatarSize,
    style: ZeraColorFamily = ZeraColorFamily.Blue,
) {
    val palette = style.palette()
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = palette.container,
        contentColor = palette.onContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (photoUrl != null) {
                // TODO: trocar por um AsyncImage (ou equivalente) apontando para photoUrl
                // assim que uma lib de carregamento de imagem for adicionada ao projeto.
                ZeraIcon(
                    icon = ZeraIcon.Placeholder,
                    contentDescription = initials,
                    tint = palette.onContainer,
                )
            } else {
                HeadlineText(text = initials, bold = true, color = palette.onContainer)
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
