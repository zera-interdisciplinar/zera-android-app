package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel

data class RecyclingState(
    val canUseCurrentLocation: Boolean = false,
    val address: String = "",
    val selectedRecyclingPointId: String? = null,
) {
    val canSelectItemsForDisposal: Boolean
        get() = selectedRecyclingPointId != null
}

class RecyclingViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(RecyclingState())
    val state = _state

    fun onLocationAvailabilityChanged(canUseCurrentLocation: Boolean) {
        _state.value = _state.value.copy(canUseCurrentLocation = canUseCurrentLocation)
    }

    fun onAddressChange(value: String) {
        _state.value = _state.value.copy(address = value)
    }

    /** Chamado quando o usuário seleciona uma recicladora no mapa. */
    fun onRecyclingPointSelected(pointId: String) {
        _state.value = _state.value.copy(selectedRecyclingPointId = pointId)
    }

    fun onSelectItemsForDisposalClick() {
        // TODO: levar a recicladora escolhida (selectedRecyclingPointId) para a seleção de itens
        val placeId = _state.value.selectedRecyclingPointId ?: return
        ZeraNavigator.push(
            Route.ItensSelection(
                placeId = placeId,
                placeName = "",
                placeAddress = "",
                distanceMeters = 0,
            ),
            ScreenAnimation.SlideHorizontal,
        )
    }
}
