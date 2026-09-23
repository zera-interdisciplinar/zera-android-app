package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.components.lists.ProductItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.icons.ZeraIcon

data class ManagerHomeState(
    val userName: String = "",
    val userRole: String = "",
    val stockItemCount: Int = 0,
    val stockOccupation: Float = 0f,
    val totalItems: String = "",
    val totalEmployees: String = "",
    val notifications: List<NotificationItem> = emptyList(),
    val latestProducts: List<ProductItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ManagerHomeViewModel : ViewModel() {
    // TODO: substituir os dados de exemplo pela chamada ao back (ver loadDashboard)
    private val _state = mutableStateOf(
        ManagerHomeState(
            userName = "Natalia Flores",
            userRole = "Gestor",
            stockItemCount = 300,
            stockOccupation = 0.6f,
            totalItems = "1.230",
            totalEmployees = "43",
            notifications = listOf(
                NotificationItem(
                    id = "1",
                    label = "5 produtos sem classificação",
                    text = "Aguardando revisão do gestor",
                    style = ZeraColorFamily.Yellow,
                ),
                NotificationItem(
                    id = "2",
                    label = "Material reciclável em rota incorreta",
                    text = "Verifique a ocorrência registrada",
                    style = ZeraColorFamily.Red,
                ),
            ),
            latestProducts = listOf(
                ProductItem(id = "265964", name = "Placa de vídeo"),
                ProductItem(id = "118203", name = "Teclado mecânico", icon = ZeraIcon.Box),
            ),
        )
    )
    val state = _state

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        // TODO: buscar o resumo do gestor no back e atualizar o _state (isLoading / errorMessage inclusos)
    }

    fun onItemCardClick() {
        ZeraNavigator.push(Route.Itens)
    }

    fun onEmployeesCardClick() {
        ZeraNavigator.push(Route.Employees)
    }
}
