package com.zera.android.viewmodel.employee

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.usecase.auth.GetSelfUser
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.components.lists.ProductItem
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.manager.ManagerHomeViewModel
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
 * Nome e cargo vêm do usuário logado; o restante dos dados ainda é fixo (mock) até o
 * back expor o dashboard do operário.
 */
class EmployeeHomeViewModel : ZeraViewModel() {
    private val getSelfUser = GetSelfUser()

    private val _state = mutableStateOf(
        EmployeeHomeState(
            stockItemCount = 300,
            stockOccupation = 0.71f,
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
    }

    private suspend fun loadLoggedUser() {
        try {
            val user = getSelfUser.execute()
            _state.value = _state.value.copy(
                userName = user.name,
                userRole = ManagerHomeViewModel.roleLabel(user.role),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // A home ainda pode ser exibida sem o nome.
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
}
