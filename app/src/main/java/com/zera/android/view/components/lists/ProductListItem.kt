package com.zera.android.view.components.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

import com.zera.android.view.theme.palette

/** Tamanho (largura e altura) do quadrado de ícone. */
private val IconBoxSize = 48.dp

/**
 * Item clicável de uma lista de produtos: quadrado de ícone à esquerda, nome e
 * ID do produto ao centro, seta de "avançar" à direita.
 *
 * Usado para compor listas (ex.: um `ProductList`, criado depois) — este
 * composable só desenha a linha de um item.
 *
 * @param itemName nome do produto.
 * @param itemId ID do produto, exibido apenas para referência visual (ex.: "265964"
 *   vira "ID 265964").
 * @param onClick ação executada ao tocar no item.
 * @param modifier modificador externo opcional. O item já ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 * @param icon ícone do catálogo [ZeraIcon] exibido no quadrado à esquerda. Quando
 *   `null` (padrão), usa [ZeraIcon.Placeholder].
 */
@Composable
fun ProductListItem(
    itemName: String,
    itemId: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ZeraIcon? = null,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.large),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = Modifier.padding(Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val iconPalette = ZeraColorFamily.Blue.palette()
            Surface(
                modifier = Modifier.size(IconBoxSize),
                shape = RoundedCornerShape(Radius.large),
                color = iconPalette.container,
                contentColor = iconPalette.onContainer,
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ZeraIcon(
                        icon = icon ?: ZeraIcon.Placeholder,
                        contentDescription = null,
                        tint = iconPalette.onContainer,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.micro),
            ) {
                BodyText(text = itemName, bold = true)
                CaptionText(text = "ID $itemId")
            }
            ZeraIcon(
                icon = ZeraIcon.ProceedArrow,
                contentDescription = null,
                size = Spacing.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductListItemPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            ProductListItem(
                itemName = "Placa de vídeo",
                itemId = "265964",
                onClick = {},
            )
            ProductListItem(
                itemName = "Teclado mecânico",
                itemId = "118203",
                icon = ZeraIcon.Box,
                onClick = {},
            )
        }
    }
}
