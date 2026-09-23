package com.zera.android.view.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.components.outros.ItemStatusTag
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Card branco simples com o resumo de um item: título (nome + subtítulo), o ID
 * como legenda e uma [ItemStatusTag] — usado na tela de confirmação de aprovação
 * ([com.zera.android.view.screens.manager.ItemApprovedScreen]), diferente do
 * [ItemSummaryCard] (card azul com miniatura, usado na tela de detalhe do item).
 *
 * @param itemName nome do item.
 * @param itemSubtitle subtítulo complementar (ex.: o modelo do item).
 * @param itemId ID do item, exibido como legenda ("ID {itemId}").
 * @param status status do item, exibido como [ItemStatusTag].
 * @param modifier modificador externo opcional. O card já ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 */
@Composable
fun ApprovedItemCard(
    itemName: String,
    itemSubtitle: String,
    itemId: String,
    status: ItemStatus,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.xLarge),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            BodyText(text = "$itemName · $itemSubtitle", bold = true)
            CaptionText(text = "ID $itemId")
            ItemStatusTag(status = status)
        }
    }
}

@Preview()
@Composable
private fun ApprovedItemCardPreview() {
    ZeraTheme {
        ApprovedItemCard(
            itemName = "Placa de vídeo",
            itemSubtitle = "Notebook Mac",
            itemId = "265964",
            status = ItemStatus.PendingApproval,
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
