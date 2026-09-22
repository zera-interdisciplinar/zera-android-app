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
import com.zera.android.view.components.outros.Tag
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme

/**
 * Card branco simples com o resumo de um item: título (nome + subtítulo), o ID
 * como legenda e uma [Tag] de status — usado no popup de confirmação de aprovação
 * ([ItemApprovedDialog]), diferente do [ItemSummaryCard] (card azul com miniatura,
 * usado na tela de detalhe do item).
 *
 * @param itemName nome do item.
 * @param itemSubtitle subtítulo complementar (ex.: o modelo do item).
 * @param itemId ID do item, exibido como legenda ("ID {itemId}").
 * @param statusText texto curto exibido na [Tag] de status.
 * @param modifier modificador externo opcional. O card já ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 * @param statusStyle família de cor da [Tag] de status.
 */
@Composable
fun ApprovedItemCard(
    itemName: String,
    itemSubtitle: String,
    itemId: String,
    statusText: String,
    modifier: Modifier = Modifier,
    statusStyle: ZeraColorFamily = ZeraColorFamily.Yellow,
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
            Tag(text = statusText, style = statusStyle)
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
            statusText = "Em aprovação",
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
