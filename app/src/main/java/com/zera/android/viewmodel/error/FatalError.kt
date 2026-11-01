package com.zera.android.viewmodel.error

import androidx.compose.runtime.mutableStateOf
import com.zera.android.viewmodel.ZeraViewModel

data class FatalErrorState(
    val shouldExitApp: Boolean = false,
)

class FatalErrorViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(FatalErrorState())
    val state = _state

    /** Sinaliza para a tela fechar a Activity, já que o ViewModel não conhece a View. */
    fun onConfirmClick() {
        _state.value = _state.value.copy(shouldExitApp = true)
    }
}
