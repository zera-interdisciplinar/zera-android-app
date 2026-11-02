package com.zera.android.viewmodel.main.alerts

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.usecase.notification.DismissAlert
import com.zera.android.model.usecase.notification.DismissedAlerts
import com.zera.android.model.usecase.notification.GetAlerts
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.navigation.routeForAlertKind
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.errorDescription
import com.zera.android.viewmodel.loadErrorDescription
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class AlertCentralState(
    val notifications: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    /** A última carga falhou: a lista vazia não mostra "Nenhuma notificação". */
    val loadFailed: Boolean = false,
    val errorMessage: String? = null,
)

class AlertCentralViewModel(
    private val getAlerts: GetAlerts = GetAlerts(),
    private val dismissAlert: DismissAlert = DismissAlert(),
) : ZeraViewModel() {
    private val _state = mutableStateOf(AlertCentralState())
    val state = _state

    /** Tudo o que já foi carregado; a tela mostra estes alertas menos os dispensados. */
    private var loadedAlerts = emptyList<NotificationItem>()

    init {
        viewModelScope.launch {
            DismissedAlerts.ids.collect { dismissedIds ->
                _state.value = _state.value.copy(
                    notifications = AlertMapping.visibleAlerts(loadedAlerts, dismissedIds),
                )
                loadMoreIfEmptied()
            }
        }
        loadPage()
    }

    fun onLoadMore() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || !current.hasMore) return
        loadPage()
    }

    /** A primeira página usa [AlertCentralState.isLoading]; as seguintes, [AlertCentralState.isLoadingMore]. */
    private fun loadPage() {
        val current = _state.value
        _state.value = if (loadedAlerts.isEmpty()) {
            current.copy(isLoading = true)
        } else {
            current.copy(isLoadingMore = true)
        }
        val page = pageFor(current.notifications.size)
        viewModelScope.launch {
            try {
                val alerts = getAlerts.execute(status = "OPEN", page = page, size = PageSize)
                val previousCount = loadedAlerts.size
                loadedAlerts = appendPage(loadedAlerts, AlertMapping.notificationsFrom(alerts))
                _state.value = _state.value.copy(
                    notifications = AlertMapping.visibleAlerts(
                        loadedAlerts,
                        DismissedAlerts.ids.value,
                    ),
                    isLoading = false,
                    isLoadingMore = false,
                    hasMore = hasMoreAfter(alerts.size),
                    loadFailed = false,
                )
                // Só insiste se a página trouxe algo novo: repetir a mesma não muda o resultado.
                if (loadedAlerts.size > previousCount) loadMoreIfEmptied()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Mantém hasMore: o LoadMoreEffect não pede a página de novo sozinho, só quando o
                // usuário sobe e desce outra vez até o fim.
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    loadFailed = true,
                    errorMessage = errorDescription(e, loadErrorDescription("os alertas")),
                )
            }
        }
    }

    /** O `LoadMoreEffect` não dispara com a lista vazia, então o próprio ViewModel pede a página. */
    private fun loadMoreIfEmptied() {
        if (_state.value.notifications.isEmpty() && loadedAlerts.isNotEmpty()) onLoadMore()
    }

    fun onAlertDismiss(notification: NotificationItem) {
        viewModelScope.launch {
            try {
                dismissAlert.execute(notification.id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = errorDescription(e, AlertMapping.DismissErrorDescription),
                )
            }
        }
    }

    fun onAlertClick(notification: NotificationItem) {
        val route = routeForAlertKind(notification.kind) ?: return
        ZeraNavigator.push(route)
        viewModelScope.launch {
            try {
                dismissAlert.execute(notification.id)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // O usuário já seguiu para a ação: o alerta só volta para a lista.
            }
        }
    }

    fun onErrorDismiss() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    companion object {
        internal const val PageSize = 20

        /** Sem total na resposta: uma página incompleta significa que era a última. */
        internal fun hasMoreAfter(pageSize: Int): Boolean = pageSize >= PageSize

        /**
         * Fechar alertas desloca os seguintes para páginas anteriores. Calcular a página pelo que
         * ainda está visível evita pular itens; os repetidos são descartados em [appendPage].
         */
        internal fun pageFor(visibleCount: Int): Int = visibleCount / PageSize

        /**
         * Junta a página nova ao fim da lista. Alertas criados entre um pedido e outro deslocam
         * a paginação e podem repetir itens; como o `id` é a chave da `LazyColumn`, os repetidos
         * são descartados.
         */
        internal fun appendPage(
            current: List<NotificationItem>,
            page: List<NotificationItem>,
        ): List<NotificationItem> = (current + page).distinctBy { it.id }
    }
}
