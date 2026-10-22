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

private val NotificationSkeletonHeight = 96.dp
private const val DefaultCount = 3

/**
 * Esqueleto de uma lista de [com.zera.android.view.components.lists.NotificationList]: [count]
 * cartões só para indicar a disposição, sem reproduzir o conteúdo.
 *
 * @param modifier modificador externo opcional.
 * @param count quantidade de cartões.
 */
@Composable
fun NotificationListSkeleton(
    modifier: Modifier = Modifier,
    count: Int = DefaultCount,
) {
    Column(
        modifier = modifier.clipToBounds(),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        repeat(count) {
            SkeletonBox(height = NotificationSkeletonHeight, borderRadius = Radius.xLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationListSkeletonPreview() {
    ZeraTheme {
        NotificationListSkeleton(modifier = Modifier.padding(Spacing.medium), count = 2)
    }
}
