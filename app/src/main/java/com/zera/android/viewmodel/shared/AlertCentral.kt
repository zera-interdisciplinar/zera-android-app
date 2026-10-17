package com.zera.android.viewmodel.shared

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.usecase.notification.GetAlerts
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.navigation.routeForAlertKind
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class AlertCentralState(
    val notifications: List<NotificationItem> = emptyList(),
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val snackbarMessage: String? = null,
)

class AlertCentralViewModel(
    private val getAlerts: GetAlerts = GetAlerts(),
) : ZeraViewModel() {
    private val _state = mutableStateOf(AlertCentralState())
    val state = _state

    private var nextPage = 0

    init {
        onLoadMore()
    }

    fun onLoadMore() {
        val current = _state.value
        if (current.isLoadingMore || !current.hasMore) return
        _state.value = current.copy(isLoadingMore = true)
        viewModelScope.launch {
            try {
                val alerts = getAlerts.execute(page = nextPage, size = PageSize)
                nextPage++
                _state.value = _state.value.copy(
                    notifications = appendPage(
                        _state.value.notifications,
                        AlertMapping.notificationsFrom(alerts),
                    ),
                    isLoadingMore = false,
                    hasMore = hasMoreAfter(alerts.size),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // TODO: trocar por snackbar com "Tentar novamente". Por ora a paginação para aqui,
                //  senão a tela pediria a mesma página de novo assim que o carregamento terminasse.
                _state.value = _state.value.copy(
                    isLoadingMore = false,
                    hasMore = false,
                    snackbarMessage = "Não foi possível carregar os alertas.",
                )
            }
        }
    }

    fun onAlertClick(notification: NotificationItem) {
        val route = routeForAlertKind(notification.kind) ?: return
        ZeraNavigator.push(route)
    }

    fun onSnackbarShown() {
        _state.value = _state.value.copy(snackbarMessage = null)
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
