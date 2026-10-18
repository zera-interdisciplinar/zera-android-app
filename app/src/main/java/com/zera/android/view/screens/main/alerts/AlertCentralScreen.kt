package com.zera.android.view.screens.main.alerts

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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.lists.notificationItems
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette
import com.zera.android.viewmodel.main.alerts.AlertCentralViewModel

/** Quantos itens antes do fim da lista a próxima página começa a ser pedida. */
private const val LoadMoreThreshold = 3

/**
 * Central de Alertas: notificações do usuário (gestor ou operário). Acessada pelo botão
 * de sino da [UpperNavBar].
 *
 * Tudo vive numa única `LazyColumn` para que os alertas sejam carregados página a página:
 * quando o usuário chega perto do fim da lista, a próxima página é pedida ao ViewModel.
 */
@Composable
fun AlertCentralScreen(
    viewModel: AlertCentralViewModel = viewModel(),
) {
    val state by viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    val errorPalette = ZeraColorFamily.Red.palette()

    LaunchedEffect(state.snackbarMessage) {
        val message = state.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.onSnackbarShown()
    }

    val listState = rememberLazyListState()
    val nearEnd by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            lastVisible >= info.totalItemsCount - 1 - LoadMoreThreshold
        }
    }
    // Também roda de novo quando uma página termina de carregar e a lista ainda não encheu a tela.
    LaunchedEffect(nearEnd, state.isLoadingMore, state.hasMore) {
        if (nearEnd && state.hasMore && !state.isLoadingMore) viewModel.onLoadMore()
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = errorPalette.base,
                    contentColor = errorPalette.onBase,
                )
            }
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
            // TODO: refinar a necessidade do botão "Configurar Notificações" com o time
//            item(key = "settings") {
//                ZeraButton(
//                    text = "Configurar Notificações",
//                    onClick = { /* TODO: navegar para as configurações de notificação */ },
//                    fillMaxWidth = true,
//                    icon = ZeraIcon.ProceedArrow,
//                    modifier = Modifier.padding(bottom = Spacing.small),
//                )
//            }

            notificationItems(
                notifications = state.notifications,
                onItemClick = viewModel::onAlertClick,
            )

            if (state.notifications.isEmpty() && !state.isLoadingMore) {
                item(key = "empty") {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(Spacing.medium),
                        contentAlignment = Alignment.Center,
                    ) {
                        BodyText(text = "Nenhuma notificação")
                    }
                }
            }

            if (state.isLoadingMore) {
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
