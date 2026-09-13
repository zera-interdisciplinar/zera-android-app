package com.zera.android.view.components.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Spacing
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
 */
data class Product(
    val id: String,
    val name: String,
    val icon: ZeraIcon? = null,
)

/**
 * Lista rolável de produtos: recebe os dados em [products] e renderiza um
 * [ProductListItem] para cada um.
 *
 * @param products produtos a exibir, na ordem em que devem aparecer.
 * @param onItemClick ação executada ao tocar em um item, recebendo o [Product] clicado.
 * @param modifier modificador externo opcional.
 * @param emptyContent conteúdo exibido quando [products] está vazio. Por padrão,
 *   uma mensagem de texto simples.
 */
@Composable
fun ProductList(
    products: List<Product>,
    onItemClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
    emptyContent: @Composable () -> Unit = { BodyText(text = "Nenhum produto encontrado") },
) {
    if (products.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            emptyContent()
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        items(products, key = { it.id }) { product ->
            ProductListItem(
                itemName = product.name,
                itemId = product.id,
                icon = product.icon,
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
                Product(id = "265964", name = "Placa de vídeo"),
                Product(id = "118203", name = "Teclado mecânico", icon = ZeraIcon.Box),
                Product(id = "330912", name = "Mouse sem fio"),
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
