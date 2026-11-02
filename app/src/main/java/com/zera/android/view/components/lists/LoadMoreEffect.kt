package com.zera.android.view.components.lists

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/** Quantos itens antes do fim a próxima página começa a carregar. */
private const val DefaultThreshold = 4

/**
 * Chama [onEndReached] quando o usuário **chega** perto do fim de uma lista paginada, ou seja, na
 * passagem de "longe do fim" para "perto do fim".
 *
 * - Página carregada com sucesso: [itemCount] muda, o efeito reinicia e, se o usuário continua no
 *   fim (ou a lista ainda não encheu a tela), pede a próxima.
 * - Página que falhou: [itemCount] não muda e nada é pedido de novo sozinho; só quando o usuário
 *   sobe um pouco e desce outra vez até o fim. Assim uma falha não vira um loop de requisições.
 *
 * Quem chama ainda deve ignorar [onEndReached] enquanto uma página estiver carregando ou quando
 * não houver mais páginas.
 *
 * @param listState estado da `LazyColumn` paginada.
 * @param itemCount quantidade de itens de dados já carregados (sem rodapé de loading).
 * @param onEndReached carrega a próxima página.
 * @param threshold quantos itens antes do fim o carregamento começa.
 */
@Composable
fun LoadMoreEffect(
    listState: LazyListState,
    itemCount: Int,
    onEndReached: () -> Unit,
    threshold: Int = DefaultThreshold,
) {
    val currentOnEndReached by rememberUpdatedState(onEndReached)
    LaunchedEffect(listState, itemCount, threshold) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: return@snapshotFlow false
            itemCount > 0 && lastVisible >= itemCount - threshold
        }
            .distinctUntilChanged()
            .collect { nearEnd ->
                if (nearEnd) currentOnEndReached()
            }
    }
}
