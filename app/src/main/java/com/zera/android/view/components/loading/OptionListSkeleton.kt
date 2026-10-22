package com.zera.android.view.components.loading

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Esqueleto de uma lista de [com.zera.android.view.components.lists.OptionList]: [count] linhas
 * só para indicar a disposição, sem reproduzir o conteúdo.
 *
 * O `OptionListItem` tem a mesma altura do `ProductListItem`, então reaproveita o
 * [ProductListSkeleton]. Existe separado para o nome refletir a lista que está sendo substituída.
 *
 * @param modifier modificador externo opcional.
 * @param count quantidade de linhas.
 */
@Composable
fun OptionListSkeleton(
    modifier: Modifier = Modifier,
    count: Int = 6,
) {
    ProductListSkeleton(modifier = modifier, count = count)
}

@Preview(showBackground = true)
@Composable
private fun OptionListSkeletonPreview() {
    ZeraTheme {
        OptionListSkeleton(modifier = Modifier.padding(Spacing.medium), count = 3)
    }
}
