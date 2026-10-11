package com.zera.android.view.components.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Dado de uma opção exibida em [OptionList].
 *
 * @param description descrição da opção, exibida abaixo do nome.
 * @param name nome da opção.
 * @param icon ícone do catálogo [ZeraIcon] exibido no item. Quando `null`, o
 *   [OptionListItem] usa seu ícone padrão.
 * @param statusText texto curto de status. Quando não `null`, o item exibe uma tag
 *   de status em vez da seta de "avançar".
 * @param statusStyle família de cor da tag de status. Só tem efeito quando [statusText] não é `null`.
 */
data class OptionItem(
    val description: String,
    val name: String,
    val icon: ZeraIcon? = null,
    val statusText: String? = null,
    val statusStyle: ZeraColorFamily = ZeraColorFamily.Yellow,
    val id: String? = null,
)

/**
 * Lista rolável de opções: recebe os dados em [options] e renderiza um
 * [OptionListItem] para cada uma.
 *
 * A lista ocupa apenas a altura do seu conteúdo. Quando for colocada dentro de um
 * container com rolagem vertical, limite a altura pelo [modifier].
 *
 * @param options opções a exibir, na ordem em que devem aparecer.
 * @param onItemClick ação executada ao tocar em um item, recebendo a [OptionItem] clicada.
 *   Quando `null`, os itens não exibem ação.
 * @param modifier modificador externo opcional.
 * @param contentPadding espaçamento interno entre a borda da lista e os itens.
 * @param emptyContent conteúdo exibido quando [options] está vazio.
 * @param onEndReached chamado ao chegar perto do fim da lista, para carregar a próxima página.
 * @param isLoadingMore quando verdadeiro, exibe um indicador no rodapé da lista.
 * @param hasIcon quando `false`, os itens não exibem o quadrado de ícone (só nome e descrição).
 */
@Composable
fun OptionList(
    options: List<OptionItem>,
    onItemClick: ((OptionItem) -> Unit)? = null,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Spacing.medium),
    emptyContent: @Composable () -> Unit = { BodyText(text = "Nenhuma opção encontrada") },
    onEndReached: () -> Unit = {},
    isLoadingMore: Boolean = false,
    hasIcon: Boolean = true,
) {
    if (options.isEmpty()) {
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

    val listState = rememberLazyListState()
    LaunchedEffect(listState, options.size, isLoadingMore) {
        if (isLoadingMore) return@LaunchedEffect
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: return@snapshotFlow false
            val total = info.totalItemsCount
            total > 0 && lastVisible >= total - 4
        }
            .distinctUntilChanged()
            .collect { nearEnd ->
                if (nearEnd) onEndReached()
            }
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        items(options) { option ->
            OptionListItem(
                itemName = option.name,
                description = option.description,
                icon = option.icon,
                statusText = option.statusText,
                statusStyle = option.statusStyle,
                hasIcon = hasIcon,
                onClick = { onItemClick?.invoke(option) },
                onClickDisabled = onItemClick == null,
            )
        }
        if (isLoadingMore) {
            item(key = "loading-more") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.small),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OptionListPreview() {
    ZeraTheme {
        OptionList(
            options = listOf(
                OptionItem(description = "14 itens neste modelo", name = "Placa de vídeo"),
                OptionItem(description = "Modelo para escritório", name = "Teclado mecânico", icon = ZeraIcon.Box),
            ),
            onItemClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OptionListEmptyPreview() {
    ZeraTheme {
        OptionList(options = emptyList())
    }
}