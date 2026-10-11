package com.zera.android.viewmodel.employee

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.DashboardHomeResponseDTO
import com.zera.android.model.entity.user.UserRole
import com.zera.android.model.usecase.auth.GetSelfUser
import com.zera.android.model.usecase.inventory.GetEmployeeHome
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.components.lists.ProductItem
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class EmployeeHomeState(
    val userName: String = "",
    val userRole: String = "",
    val stockItemCount: Int = 0,
    val stockOccupation: Float = 0f,
    val maintenanceCount: String = "",
    val discardCount: String = "",
    val pendingNotifications: List<NotificationItem> = emptyList(),
    val latestProducts: List<ProductItem> = emptyList(),
)

/**
 * ViewModel da home do operário.
 *
 * Nome, cargo e ocupação do estoque vêm da API; atalhos, pendências e últimos itens
 * ainda são mock até o back expor o restante do dashboard do operário.
 */
class EmployeeHomeViewModel : ZeraViewModel() {
    private val getSelfUser = GetSelfUser()
    private val getEmployeeHome = GetEmployeeHome()

    private val _state = mutableStateOf(
        EmployeeHomeState(
            maintenanceCount = "25",
            discardCount = "8",
            pendingNotifications = listOf(
                NotificationItem(
                    id = "incomplete-registrations",
                    label = "3 cadastros incompletos",
                    style = ZeraColorFamily.Red
                ),
            ),
            latestProducts = listOf(
                ProductItem(
                    id = "265964",
                    name = "Placa de vídeo",
                    statusText = "Em aprovação",
                ),
            ),
        )
    )
    val state = _state

    init {
        viewModelScope.launch { loadLoggedUser() }
        viewModelScope.launch { loadStockOccupation() }
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
            _state.value = _state.value.copy(
                stockItemCount = home.activeItems.toInt(),
                stockOccupation = occupationFrom(home),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // O card fica em zero se o dashboard falhar; o restante da home continua visível.
        }
    }

    fun onScanClick() {
        // TODO: navegar para o scanner quando a rota do operário existir
    }

    fun onAddModelClick() {
        // TODO: navegar para o cadastro de modelo do operário
    }

    fun onMaintenanceCardClick() {
        // TODO: navegar para manutenção
    }

    fun onDiscardCardClick() {
        // TODO: navegar para descartes
    }

    fun onPendingClick() {
        // TODO: navegar para cadastros incompletos
    }

    fun onSeeAllItemsClick() {
        // TODO: navegar para os itens do operário
    }

    companion object {
        internal fun occupationFrom(home: DashboardHomeResponseDTO): Float {
            val percent = home.occupancyPercent ?: return 0f
            return (percent / 100.0).toFloat().coerceIn(0f, 1f)
        }

        internal fun roleLabel(role: String): String = when (role) {
            UserRole.MANAGER -> "Gestor"
            UserRole.EMPLOYEE -> "Operário"
            else -> role
        }
    }
}
