package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator

data class ItemApprovedState(
    val itemId: String = "",
    val itemName: String = "",
    val itemSubtitle: String = "",
    val status: ItemStatus = ItemStatus.PendingApproval,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class ItemApprovedViewModel : ViewModel() {
    // TODO: substituir os dados de exemplo pelo item recebido por parâmetro de
    // navegação assim que essa tela for alcançada a partir de um fluxo real de aprovação
    private val _state = mutableStateOf(
        ItemApprovedState(
            itemId = "265964",
            itemName = "Placa de vídeo",
            itemSubtitle = "Notebook Mac",
            status = ItemStatus.PendingApproval,
        )
    )
    val state = _state

    fun onCloseClick() {
        ZeraNavigator.goBack()
    }

    fun onViewPendingItemsClick() {
        // TODO: navegar para a lista de itens filtrada por "Pendentes" (ainda não há rota para ItensScreen)
    }

    fun onBackToHomeClick() {
        ZeraNavigator.pushAndPopAll(Route.ManagerHome)
    }
}
