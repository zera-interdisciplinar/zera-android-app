package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class ItemApprovedState(
    val itemId: String = "",
    val itemName: String = "",
    val itemSubtitle: String = "",
    val status: ItemStatus = ItemStatus.InStock,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class ItemApprovedViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(ItemApprovedState())
    val state = _state

    fun setItem(itemId: String, itemName: String, itemSubtitle: String) {
        _state.value = ItemApprovedState(
            itemId = itemId,
            itemName = itemName,
            itemSubtitle = itemSubtitle,
            status = ItemStatus.InStock,
        )
    }

    fun onCloseClick() {
        ZeraNavigator.goBack()
    }

    fun onViewPendingItemsClick() {
        ZeraNavigator.push(Route.Itens)
    }

    fun onBackToHomeClick() {
        ZeraNavigator.pushAndPopAll(Route.ManagerHome)
    }
}
