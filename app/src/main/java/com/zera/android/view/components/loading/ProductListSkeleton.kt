package com.zera.android.view.components.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

private val ProductItemSkeletonHeight = 80.dp
private const val DefaultCount = 6

/**
 * Esqueleto de uma lista de [com.zera.android.view.components.lists.ProductList]: [count] linhas
 * só para indicar a disposição, sem reproduzir o conteúdo.
 *
 * O que passar da área disponível é cortado, então pode ser usado em telas sem rolagem
 * (ex.: com `Modifier.weight(1f)`) sem invadir a barra inferior.
 *
 * @param modifier modificador externo opcional.
 * @param count quantidade de linhas.
 */
@Composable
fun ProductListSkeleton(
    modifier: Modifier = Modifier,
    count: Int = DefaultCount,
) {
    Column(
        modifier = modifier.clipToBounds(),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        repeat(count) {
            SkeletonBox(height = ProductItemSkeletonHeight, borderRadius = Radius.large)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductListSkeletonPreview() {
    ZeraTheme {
        ProductListSkeleton(modifier = Modifier.padding(Spacing.medium), count = 3)
    }
}
