package com.zera.android.view.components.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

/**
 * Dado de um produto exibido em [ProductList].
 *
 * @param id identificador do produto, usado como [ProductListItem.itemId] e como
 *   chave de recomposição da lista.
 * @param name nome do produto.
 * @param icon ícone do catálogo [ZeraIcon] exibido no item. Quando `null`, o
 *   [ProductListItem] usa seu ícone padrão.
 * @param statusText texto curto de status (ex.: "Pendente"). Quando não `null`,
 *   o item exibe uma tag de status em vez da seta de "avançar".
 * @param statusStyle família de cor da tag de status. Só tem efeito quando [statusText] não é `null`.
 */
data class ProductItem(
    val id: String,
    val name: String,
    val icon: ZeraIcon? = null,
    val statusText: String? = null,
    val statusStyle: ZeraColorFamily = ZeraColorFamily.Yellow,
)

/**
 * Lista rolável de produtos: recebe os dados em [products] e renderiza um
 * [ProductListItem] para cada um.
 *
 * A lista ocupa apenas a altura do seu conteúdo. Quando for colocada dentro de um
 * container com rolagem vertical, limite a altura pelo [modifier]
 * (ex.: `Modifier.heightIn(max = 400.dp)`).
 *
 * @param products produtos a exibir, na ordem em que devem aparecer.
 * @param onItemClick ação executada ao tocar em um item, recebendo o [ProductItem] clicado.
 * @param modifier modificador externo opcional.
 * @param contentPadding espaçamento interno entre a borda da lista e os itens.
 * @param emptyContent conteúdo exibido quando [products] está vazio. Por padrão,
 *   uma mensagem de texto simples.
 */
@Composable
fun ProductList(
    products: List<ProductItem>,
    onItemClick: (ProductItem) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Spacing.medium),
    emptyContent: @Composable () -> Unit = { BodyText(text = "Nenhum produto encontrado") },
) {
    if (products.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(contentPadding),
            contentAlignment = Alignment.Center,
        ) {
            emptyContent()
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        items(products, key = { it.id }) { product ->
            ProductListItem(
                itemName = product.name,
                itemId = product.id,
                icon = product.icon,
                statusText = product.statusText,
                statusStyle = product.statusStyle,
                onClick = { onItemClick(product) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductListPreview() {
    ZeraTheme {
        ProductList(
            products = listOf(
                ProductItem(id = "265964", name = "Placa de vídeo"),
                ProductItem(id = "118203", name = "Teclado mecânico", icon = ZeraIcon.Box),
                ProductItem(id = "330912", name = "Mouse sem fio"),
            ),
            onItemClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductListEmptyPreview() {
    ZeraTheme {
        ProductList(
            products = emptyList(),
            onItemClick = {},
        )
    }
}
