package com.zera.android.viewmodel.auth.passwordRecovery

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class PasswordRecoveryTokenState(
    val token: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PasswordRecoveryTokenViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(PasswordRecoveryTokenState())
    val state = _state

    fun onTokenChange(newToken: String) {
        _state.value = _state.value.copy(token = newToken, errorMessage = null)
    }

    fun changeEmail() {
        ZeraNavigator.goBack()
    }

    fun verifyToken() {
        val current = _state.value
        if (current.token.length != TOKEN_SIZE) {
            _state.value = current.copy(errorMessage = "O código de acesso deve ter $TOKEN_SIZE dígitos.")
            return
        }

        // TODO: validar o código com o use case e navegar para NewPasswordScreen
    }

    companion object {
        const val TOKEN_SIZE = 6
    }
}
