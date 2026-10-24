package com.zera.android.viewmodel.auth.passwordRecovery

import androidx.compose.runtime.mutableStateOf
import com.zera.android.viewmodel.ZeraViewModel

data class PasswordRecoveryEmailState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PasswordRecoveryEmailViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(PasswordRecoveryEmailState())
    val state = _state

    fun onEmailChange(newEmail: String) {
        _state.value = _state.value.copy(email = newEmail, errorMessage = null)
    }

    fun sendEmail() {
        val current = _state.value
        if (current.email.isBlank()) {
            _state.value = current.copy(errorMessage = "Informe seu e-mail.")
            return
        }
        if (!EMAIL_REGEX.matches(current.email.trim())) {
            _state.value = current.copy(errorMessage = "Informe um e-mail válido.")
            return
        }

        // TODO: chamar use case de recuperação de senha e navegar para PasswordRecoveryTokenScreen
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
