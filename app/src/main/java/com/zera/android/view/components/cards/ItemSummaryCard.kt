package com.zera.android.view.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.components.outros.ItemStatusTag
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

/** Tamanho (largura e altura) do quadrado de miniatura. */
private val ThumbnailSize = 72.dp

/**
 * Card de destaque no topo de uma tela de detalhe de item: miniatura à esquerda,
 * ID/nome/subtítulo do item e uma [Tag] de status à direita.
 *
 * @param itemId ID do item, exibido como "ID {itemId}".
 * @param itemName nome do item, em destaque.
 * @param itemSubtitle subtítulo complementar (ex.: o modelo do item).
 * @param status status do item, exibido como [ItemStatusTag].
 * @param modifier modificador externo opcional. O card já ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 * @param icon ícone do catálogo [ZeraIcon] exibido na miniatura. Quando `null`
 *   (padrão), usa [ZeraIcon.Placeholder].
 */
@Composable
fun ItemSummaryCard(
    itemId: String,
    itemName: String,
    itemSubtitle: String,
    status: ItemStatus,
    modifier: Modifier = Modifier,
    icon: ZeraIcon? = null,
) {
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ZeraIcon(
                        icon = icon ?: ZeraIcon.Placeholder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                CaptionText(text = "ID $itemId", color = LocalContentColor.current)
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.micro)) {
                    TitleText(text = itemName, bold = true, color = LocalContentColor.current)
                    BodyText(text = itemSubtitle, color = LocalContentColor.current)
                }
                ItemStatusTag(status = status)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemSummaryCardPreview() {
    ZeraTheme {
        ItemSummaryCard(
            itemId = "265964",
            itemName = "Placa de vídeo",
            itemSubtitle = "Notebook Mac",
            status = ItemStatus.PendingApproval,
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
