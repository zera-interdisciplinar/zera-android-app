package com.zera.android.viewmodel.employee

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.DashboardHomeResponseDTO
import com.zera.android.model.dto.inventory.WorkCenterResponseDTO
import com.zera.android.model.entity.user.UserRole
import com.zera.android.model.usecase.auth.GetSelfUser
import com.zera.android.model.usecase.inventory.GetEmployeeHome
import com.zera.android.model.usecase.inventory.GetEmployeePendencies
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.components.lists.ProductItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.manager.ItensViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class EmployeeHomeState(
    val userName: String = "",
    val userRole: String = "",
    val stockItemCount: Int = 0,
    val stockOccupation: Float = 0f,
    val maintenanceCount: String = "0",
    val discardCount: String = "0",
    val pendingNotifications: List<NotificationItem> = emptyList(),
    val latestProducts: List<ProductItem> = emptyList(),
)

/**
 * ViewModel da home do operário.
 *
 * Nome, cargo, ocupação, pendências, atalhos e últimos itens vêm da API.
 * Manutenção = `inMaintenance`; Descartes = `disposalsInWindow` (janela do backend).
 */
class EmployeeHomeViewModel : ZeraViewModel() {
    private val getSelfUser = GetSelfUser()
    private val getEmployeeHome = GetEmployeeHome()
    private val getEmployeePendencies = GetEmployeePendencies()

    private val _state = mutableStateOf(EmployeeHomeState())
    val state = _state

    init {
        viewModelScope.launch { loadLoggedUser() }
        viewModelScope.launch { loadStockOccupation() }
        viewModelScope.launch { loadPendencies() }
    }

    private suspend fun loadLoggedUser() {
        try {
            val user = getSelfUser.execute()
            _state.value = _state.value.copy(
                userName = user.name,
                userRole = roleLabel(user.role),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // A home ainda pode ser exibida sem o nome.
        }
    }

    private suspend fun loadStockOccupation() {
        try {
            val home = getEmployeeHome.execute()
            val (maintenance, discards) = shortcutCounts(home)
            _state.value = _state.value.copy(
                stockItemCount = home.activeItems.toInt(),
                stockOccupation = occupationFrom(home),
                maintenanceCount = maintenance,
                discardCount = discards,
                latestProducts = latestProductsFrom(home),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // O card fica em zero e a lista cai no empty state; o restante da home continua visível.
        }
    }

    private suspend fun loadPendencies() {
        try {
            val workCenter = getEmployeePendencies.execute()
            _state.value = _state.value.copy(
                pendingNotifications = pendenciesFrom(workCenter),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // A lista cai no empty state se o work-center falhar.
        }
    }

    fun onScanClick() {
        ZeraNavigator.push(Route.Scan)
    }

    fun onAddModelClick() {
        ZeraNavigator.push(addModelRoute())
    }

    fun onMaintenanceCardClick() {
        ZeraNavigator.push(maintenanceCardRoute())
    }

    fun onDiscardCardClick() {
        ZeraNavigator.push(discardCardRoute())
    }

    fun onPendingClick() {
        ZeraNavigator.push(Route.WorkingCentral)
    }

    fun onSeeAllItemsClick() {
        ZeraNavigator.push(Route.EmployeeItems())
    }

    companion object {
        internal fun addModelRoute() = Route.Models

        internal fun discardCardRoute() = Route.ScheduledDisposals

        internal fun maintenanceCardRoute() =
            Route.EmployeeItems(status = ItensViewModel.MAINTENANCE_STATUS_LABEL)

        internal fun latestProductsFrom(home: DashboardHomeResponseDTO): List<ProductItem> =
            home.recentItems.content.map { item ->
                ProductItem(id = item.id, name = item.name)
            }

        internal fun shortcutCounts(home: DashboardHomeResponseDTO): Pair<String, String> =
            home.inMaintenance.toString() to home.disposalsInWindow.toString()

        internal fun occupationFrom(home: DashboardHomeResponseDTO): Float {
            val percent = home.occupancyPercent ?: return 0f
            return (percent / 100.0).toFloat().coerceIn(0f, 1f)
        }

        internal fun roleLabel(role: String): String = when (role) {
            UserRole.MANAGER -> "Gestor"
            UserRole.EMPLOYEE -> "Operário"
            else -> role
        }

        internal fun pendenciesFrom(workCenter: WorkCenterResponseDTO): List<NotificationItem> {
            val cards = mutableListOf<NotificationItem>()
            if (workCenter.drafts.isNotEmpty()) {
                cards += NotificationItem(
                    id = "incomplete-registrations",
                    label = countLabel(
                        workCenter.drafts.size,
                        singular = "cadastro incompleto",
                        plural = "cadastros incompletos",
                    ),
                    style = ZeraColorFamily.Red,
                    resolvable = true,
                )
            }
            if (workCenter.rejected.isNotEmpty()) {
                cards += NotificationItem(
                    id = "rejected-registrations",
                    label = countLabel(
                        workCenter.rejected.size,
                        singular = "cadastro recusado",
                        plural = "cadastros recusados",
                    ),
                    style = ZeraColorFamily.Red,
                    resolvable = true,
                )
            }
            if (workCenter.damagedWithoutDestination.isNotEmpty()) {
                cards += NotificationItem(
                    id = "damaged-without-destination",
                    label = countLabel(
                        workCenter.damagedWithoutDestination.size,
                        singular = "item danificado sem destino",
                        plural = "itens danificados sem destino",
                    ),
                    style = ZeraColorFamily.Yellow,
                    resolvable = true,
                )
            }
            return cards
        }

        private fun countLabel(count: Int, singular: String, plural: String): String {
            val noun = if (count == 1) singular else plural
            return "$count $noun"
        }
    }
}
