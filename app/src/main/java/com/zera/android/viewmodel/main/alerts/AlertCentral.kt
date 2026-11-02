package com.zera.android.viewmodel.main.alerts

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
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
) : ZeraViewModel() {
    private val _state = mutableStateOf(AlertCentralState())
    val state = _state

    private var nextPage = 0

    init {
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
        _state.value = if (nextPage == 0) {
            current.copy(isLoading = true)
        } else {
            current.copy(isLoadingMore = true)
        }
        viewModelScope.launch {
            try {
                val alerts = getAlerts.execute(page = nextPage, size = PageSize)
                nextPage++
                _state.value = _state.value.copy(
                    notifications = appendPage(
                        _state.value.notifications,
                        AlertMapping.notificationsFrom(alerts),
                    ),
                    isLoading = false,
                    isLoadingMore = false,
                    hasMore = hasMoreAfter(alerts.size),
                    loadFailed = false,
                )
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

    fun onAlertClick(notification: NotificationItem) {
        val route = routeForAlertKind(notification.kind) ?: return
        ZeraNavigator.push(route)
    }

    fun onErrorDismiss() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    companion object {
        internal const val PageSize = 20

        /** Sem total na resposta: uma página incompleta significa que era a última. */
        internal fun hasMoreAfter(pageSize: Int): Boolean = pageSize >= PageSize

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
