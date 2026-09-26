package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class ItemDetailsState(
    val itemId: String = "",
    val itemName: String = "",
    val itemSubtitle: String = "",
    val status: ItemStatus = ItemStatus.PendingApproval,
    val category: String = "",
    val material: String = "",
    val condition: String = "",
    val registeredBy: String = "",
    val registeredAt: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class ItemDetailsViewModel : ZeraViewModel() {
    // TODO: substituir os dados de exemplo pela chamada ao back a partir do ID do item
    // (ver loadItem), recebido por parâmetro de navegação assim que essa tela for wireada
    private val _state = mutableStateOf(
        ItemDetailsState(
            itemId = "265964",
            itemName = "Placa de vídeo",
            itemSubtitle = "Notebook Mac",
            status = ItemStatus.PendingApproval,
            category = "Eletrônico",
            material = "Metal e Plástico",
            condition = "Quebrado",
            registeredBy = "Gustavo Macal",
            registeredAt = "17 de Ago 2026 - 16h50",
        )
    )
    val state = _state

    init {
        loadItem()
    }

    private fun loadItem() {
        // TODO: buscar os detalhes do item no back e atualizar o _state (isLoading / errorMessage inclusos)
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onEditClick() {
        // TODO: fluxo de edição do item ainda não definido (regra de negócio pendente)
    }

    fun onApproveClick() {
        // TODO: caso de uso de aprovação do item ainda não definido (regra de negócio pendente)
    }
}
