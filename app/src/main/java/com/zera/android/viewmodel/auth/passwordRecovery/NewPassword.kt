package com.zera.android.viewmodel.auth.passwordRecovery

import androidx.compose.runtime.mutableStateOf
import com.zera.android.viewmodel.ZeraViewModel

data class NewPasswordState(
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

class NewPasswordViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(NewPasswordState())
    val state = _state

    fun onPasswordChange(newPassword: String) {
        _state.value = _state.value.copy(password = newPassword, passwordError = null)
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _state.value = _state.value.copy(confirmPassword = newConfirmPassword, confirmPasswordError = null)
    }

    fun resetPassword() {
        val current = _state.value
        val passwordError = if (isStrongPassword(current.password)) null else PASSWORD_RULES_MESSAGE
        val confirmPasswordError = if (current.confirmPassword == current.password) null else "As senhas não coincidem."

        if (passwordError != null || confirmPasswordError != null) {
            _state.value = current.copy(
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
            return
        }

        // TODO: chamar use case para redefinir a senha e navegar para SignInScreen
    }

    private fun isStrongPassword(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGTH &&
            password.any(Char::isUpperCase) &&
            password.any(Char::isLowerCase) &&
            password.any(Char::isDigit) &&
            password.any { !it.isLetterOrDigit() }
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private const val PASSWORD_RULES_MESSAGE =
            "Erro: a senha deve ter mais de 8 caracteres, tendo pelo menos um caracter especial, número, letra maiúscula e minúscula"
    }
}
