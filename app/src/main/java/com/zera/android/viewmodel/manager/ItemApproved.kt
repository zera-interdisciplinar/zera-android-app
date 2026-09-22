package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.ZeraColorFamily

data class ItemApprovedState(
    val itemId: String = "",
    val itemName: String = "",
    val itemSubtitle: String = "",
    val statusText: String = "",
    val statusStyle: ZeraColorFamily = ZeraColorFamily.Yellow,
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
            statusText = "Em aprovação",
            statusStyle = ZeraColorFamily.Yellow,
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
