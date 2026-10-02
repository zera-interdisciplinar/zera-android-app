    package com.zera.android.view.components.logo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.dp
import com.zera.android.R
import com.zera.android.view.theme.ZeraTheme

/** Proporção original do ic_logo.xml (viewport 595 x 154). */
private const val LOGO_ASPECT_RATIO = 595f / 154f

/**
 * Logo do app, com proporção fixa igual à do drawable original.
 *
 * @param modifier modificador externo opcional. Use-o para controlar o tamanho
 *   (ex.: `Modifier.width(200.dp)`); a altura acompanha [LOGO_ASPECT_RATIO].
 * @param tint cor que pinta o ícone inteiro (ex.: `Color.White` para a variação branca).
 *   Com [Color.Unspecified] (padrão), mantém as cores originais do vetor.
 */
@Composable
fun Logo(
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
) {
    Image(
        painter = painterResource(R.drawable.ic_logo),
        contentDescription = "Logo Zera",
        modifier = modifier.aspectRatio(LOGO_ASPECT_RATIO),
        colorFilter = if (tint.isSpecified) ColorFilter.tint(tint) else null,
    )
}

@Preview
@Composable
private fun LogoPreview() {
    ZeraTheme {
        Logo(modifier = Modifier.width(200.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun LogoWhitePreview() {
    ZeraTheme {
        Logo(modifier = Modifier.width(200.dp), tint = Color.White)
    }
}
