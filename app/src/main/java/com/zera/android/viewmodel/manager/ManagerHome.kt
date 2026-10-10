package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.DashboardHomeResponseDTO
import com.zera.android.model.dto.notification.AlertResponseDTO
import com.zera.android.model.entity.user.UserRole
import com.zera.android.model.usecase.auth.GetSelfUser
import com.zera.android.model.usecase.inventory.GetManagerHome
import com.zera.android.model.usecase.notification.GetAlerts
import com.zera.android.model.usecase.team.CountActiveEmployees
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.components.lists.ProductItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

data class ManagerHomeState(
    val userName: String = "",
    val userRole: String = "",
    val stockItemCount: Int = 0,
    val stockOccupation: Float = 0f,
    val totalItems: String = "",
    val itemsChangeLabel: String = "",
    val itemsChangePositive: Boolean = true,
    val totalEmployees: String = "",
    val employeesDescription: String? = null,
    val notifications: List<NotificationItem> = emptyList(),
    val latestProducts: List<ProductItem> = emptyList(),
    val errorMessage: String? = null,
)

class ManagerHomeViewModel : ZeraViewModel() {
    private val getSelfUser = GetSelfUser()
    private val getManagerHome = GetManagerHome()
    private val getAlerts = GetAlerts()
    private val countActiveEmployees = CountActiveEmployees()

    private val _state = mutableStateOf(ManagerHomeState())
    val state = _state

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        _state.value = _state.value.copy(errorMessage = null)
        viewModelScope.launch { loadLoggedUser() }
        viewModelScope.launch { loadInventoryHome() }
        viewModelScope.launch { loadAlerts() }
        viewModelScope.launch { loadEmployeeCount() }
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
            // A home ainda pode ser preenchida sem o nome; o interceptor já tem o token/unidade. (okHttp já colocou os dados no header)
        }
    }

    private suspend fun loadInventoryHome() {
        try {
            val home = getManagerHome.execute()
            _state.value = _state.value.copy(
                stockItemCount = home.activeItems.toInt(),
                stockOccupation = occupationFrom(home),
                totalItems = formatCount(home.activeItems),
                itemsChangeLabel = formatChangePercent(home.activeItemsChangePercent).orEmpty(),
                itemsChangePositive = home.activeItemsChangePercent?.let { it >= 0.0 } ?: true,
                latestProducts = home.recentItems.content.map { item ->
                    ProductItem(id = item.id, name = item.name)
                },
                errorMessage = null,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(errorMessage = e.message)
        }
    }

    private suspend fun loadAlerts() {
        try {
            val alerts = getAlerts.execute(status = "OPEN")
            _state.value = _state.value.copy(notifications = alertsFrom(alerts))
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // A home ainda pode ser preenchida se a lista de alertas falhar.
        }
    }

    private suspend fun loadEmployeeCount() {
        try {
            val count = countActiveEmployees.execute()
            _state.value = _state.value.copy(totalEmployees = formatCount(count.toLong()))
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // O card de itens ainda pode aparecer se a contagem de time falhar.
        }
    }

    fun onItemCardClick() {
        ZeraNavigator.push(Route.Itens)
    }

    fun onEmployeesCardClick() {
        ZeraNavigator.push(Route.Employees)
    }

    fun onSeeAllItemsClick() {
        ZeraNavigator.push(Route.Itens)
    }

    companion object {
        private val ptBr = Locale.forLanguageTag("pt-BR")

        internal fun occupationFrom(home: DashboardHomeResponseDTO): Float {
            val percent = home.occupancyPercent ?: return 0f
            return (percent / 100.0).toFloat().coerceIn(0f, 1f)
        }

        internal fun formatCount(value: Long): String =
            NumberFormat.getIntegerInstance(ptBr).format(value)

        internal fun formatChangePercent(value: Double?): String? {
            if (value == null) return null
            val arrow = if (value >= 0) "↑" else "↓"
            return "$arrow ${formatDecimal(abs(value))}%"
        }

        internal fun alertsFrom(alerts: List<AlertResponseDTO>): List<NotificationItem> =
            alerts.map { alert ->
                NotificationItem(
                    id = alert.alertId,
                    label = alert.description,
                    style = colorFamilyForSeverity(alert.severity),
                )
            }

        internal fun colorFamilyForSeverity(severity: String): ZeraColorFamily = when (severity) {
            "HIGH" -> ZeraColorFamily.Red
            "MEDIUM" -> ZeraColorFamily.Yellow
            "LOW" -> ZeraColorFamily.Green
            else -> ZeraColorFamily.Yellow
        }

        internal fun roleLabel(role: String): String = when (role) {
            UserRole.MANAGER -> "Gestor"
            UserRole.EMPLOYEE -> "Operário"
            else -> role
        }

        private fun formatDecimal(value: Double): String {
            val format = NumberFormat.getNumberInstance(ptBr)
            format.minimumFractionDigits = 0
            format.maximumFractionDigits = 1
            return format.format(value)
        }
    }
}
