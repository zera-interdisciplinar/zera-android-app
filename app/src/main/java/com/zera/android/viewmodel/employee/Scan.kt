package com.zera.android.viewmodel.employee

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class ScanState(
    val hasCameraPermission: Boolean = false,
)

/**
 * ViewModel da tela de escanear item do operário.
 *
 * Guarda se o app tem a permissão de câmera. A checagem e o pedido da permissão acontecem
 * na tela (precisam de Context/Activity); o resultado chega aqui por [onCameraPermissionChanged].
 */
class ScanViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(ScanState())
    val state = _state

    fun onCameraPermissionChanged(granted: Boolean) {
        _state.value = _state.value.copy(hasCameraPermission = granted)
    }

    fun onRegisterWithoutLabelClick() {
        ZeraNavigator.push(Route.ManualRegister())
    }

    fun onRegisterModelClick() {
        // TODO: navegar para o cadastro de modelo do operário
    }
}
