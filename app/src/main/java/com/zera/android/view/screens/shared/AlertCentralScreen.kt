package com.zera.android.view.screens.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.components.lists.notificationItems
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

/** Quantos itens antes do fim da lista a próxima página começa a ser pedida. */
private const val LoadMoreThreshold = 3

// TODO: remover os dados de exemplo quando o ViewModel (usecase GetAlerts) alimentar a tela.
private val PlaceholderToday = listOf(
    NotificationItem(
        id = "today-1",
        label = "Rota de descarte incorreta",
        text = "Material reciclável foi enviado ao aterro.",
        style = ZeraColorFamily.Red,
        resolvable = true,
        actionLabel = "Ver produto e corrigir",
    ),
    NotificationItem(
        id = "today-2",
        label = "5 itens sem classificação",
        text = "A revisão melhora a precisão dos indicadores.",
        style = ZeraColorFamily.Yellow,
        resolvable = true,
        actionLabel = "Revisar produtos",
    ),
)

private val PlaceholderPrevious = listOf(
    NotificationItem(
        id = "previous-1",
        label = "Convite de Carol expira hoje",
        resolvable = true,
        actionLabel = "Reenviar convite",
        neutral = true,
    ),
    NotificationItem(
        id = "previous-2",
        label = "Meta mensal atingiu 90%",
        resolvable = true,
        actionLabel = "Ver dashboard",
        neutral = true,
    ),
)

/**
 * Central de Alertas: notificações do usuário (gestor ou operário), separadas em
 * "Hoje" e "Anteriores", com atalho para configurar as notificações.
 *
 * Tudo vive numa única `LazyColumn` para que as notificações possam ser carregadas
 * página a página: quando o usuário chega perto do fim da lista, [onLoadMore] é chamado.
 * Quem fornece os dados (o ViewModel) deve ignorar o pedido enquanto já houver uma página
 * em andamento e anexar as novas notificações ao fim de [previous], em ordem cronológica.
 *
 * Por enquanto é só a parte visual: os parâmetros têm dados de exemplo e as ações ainda
 * não navegam. Acessada pelo botão de sino da [UpperNavBar].
 *
 * @param today notificações de hoje.
 * @param previous notificações anteriores a hoje; é onde as páginas novas entram.
 * @param isLoadingMore quando `true`, exibe o indicador de carregamento no fim da lista e
 *   suspende novos pedidos de página.
 * @param hasMore quando `false` (a última página já veio), [onLoadMore] não é mais chamado.
 * @param onLoadMore pede a próxima página de notificações.
 */
@Composable
fun AlertCentralScreen(
    today: List<NotificationItem> = PlaceholderToday,
    previous: List<NotificationItem> = PlaceholderPrevious,
    isLoadingMore: Boolean = false,
    hasMore: Boolean = false,
    onLoadMore: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    val nearEnd by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            lastVisible >= info.totalItemsCount - 1 - LoadMoreThreshold
        }
    }
    // Também roda de novo quando uma página termina de carregar e a lista ainda não encheu a tela.
    LaunchedEffect(nearEnd, isLoadingMore, hasMore) {
        if (nearEnd && hasMore && !isLoadingMore) onLoadMore()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Central de Alertas",
                goBack = true,
                showActions = false,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.small),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = Spacing.medium,
                vertical = Spacing.small,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            item(key = "settings") {
                ZeraButton(
                    text = "Configurar Notificações",
                    onClick = { /* TODO: navegar para as configurações de notificação */ },
                    fillMaxWidth = true,
                    icon = ZeraIcon.ProceedArrow,
                )
            }

            if (today.isNotEmpty()) {
                item(key = "header-today") {
                    TitleText(
                        text = "Hoje",
                        bold = true,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = Spacing.small),
                    )
                }
                notificationItems(
                    notifications = today,
                    onItemClick = { /* TODO: rotear pelo kind do alerta */ },
                )
            }

            if (previous.isNotEmpty()) {
                item(key = "header-previous") {
                    TitleText(
                        text = "Anteriores",
                        bold = true,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = Spacing.small),
                    )
                }
                notificationItems(
                    notifications = previous,
                    onItemClick = { /* TODO: rotear pelo kind do alerta */ },
                )
            }

            if (today.isEmpty() && previous.isEmpty() && !isLoadingMore) {
                item(key = "empty") {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(Spacing.medium),
                        contentAlignment = Alignment.Center,
                    ) {
                        BodyText(text = "Nenhuma notificação")
                    }
                }
            }

            if (isLoadingMore) {
                item(key = "loading-more") {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(Spacing.medium),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
@Preview(heightDp = 900)
private fun AlertCentralScreenPreview() {
    ZeraTheme {
        AlertCentralScreen()
    }
}

@Composable
@Preview(heightDp = 900)
private fun AlertCentralScreenLoadingMorePreview() {
    ZeraTheme {
        AlertCentralScreen(isLoadingMore = true, hasMore = true)
    }
}

@Composable
@Preview(heightDp = 900)
private fun AlertCentralScreenEmptyPreview() {
    ZeraTheme {
        AlertCentralScreen(today = emptyList(), previous = emptyList())
    }
}
