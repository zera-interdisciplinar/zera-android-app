package com.zera.android.view.components.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.valentinilk.shimmer.shimmer
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Bloco cinza com brilho animado (shimmer), usado como esqueleto de um conteúdo que ainda carrega.
 *
 * Para um esqueleto circular, como o de um avatar, use largura e altura iguais e um
 * [borderRadius] grande o bastante (o raio é limitado à metade do lado menor).
 *
 * Usa a lib Compose Shimmer (`Modifier.shimmer()`).
 *
 * @param height altura do bloco.
 * @param width largura do bloco; se omitida, o bloco ocupa toda a largura disponível.
 * @param borderRadius raio dos cantos; por padrão [Radius.medium].
 * @param modifier modificador externo opcional.
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    height: Dp,
    width: Dp = Dp.Unspecified,
    borderRadius: Dp = Radius.medium,
) {
    Box(
        modifier = modifier
            .then(if (width.isSpecified) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .shimmer()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(borderRadius),
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun SkeletonBoxPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            SkeletonBox(width = 48.dp, height = 48.dp, borderRadius = 48.dp)
            SkeletonBox(height = 16.dp)
            SkeletonBox(width = 200.dp, height = 16.dp)
        }
    }
}
