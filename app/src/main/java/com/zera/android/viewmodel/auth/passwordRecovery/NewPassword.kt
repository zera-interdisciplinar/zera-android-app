package com.zera.android.viewmodel.auth.passwordRecovery

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.usecase.auth.ConfirmPasswordReset
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class NewPasswordState(
    val token: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val errorMessage: String? = null
)

class NewPasswordViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(NewPasswordState())
    val state = _state

    private val confirmPasswordReset = ConfirmPasswordReset()

    /** Token lido do deep link `zera://reset-password?token=...` do e-mail. */
    fun onTokenReceived(token: String) {
        _state.value = _state.value.copy(token = token, errorMessage = null)
    }

    fun onPasswordChange(newPassword: String) {
        _state.value = _state.value.copy(password = newPassword, passwordError = null, errorMessage = null)
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _state.value = _state.value.copy(
            confirmPassword = newConfirmPassword,
            confirmPasswordError = null,
            errorMessage = null
        )
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

        if (current.token.isBlank()) {
            _state.value = current.copy(errorMessage = INVALID_LINK_MESSAGE)
            return
        }

        _state.value = current.copy(isLoading = true, errorMessage = null)
        ZeraNavigator.navigationStatus(false)
        viewModelScope.launch {
            try {
                confirmPasswordReset.execute(current.token, current.password)
                SqliteManager.clearSession()
                _state.value = _state.value.copy(isLoading = false)
                ZeraNavigator.navigationStatus(true)
                ZeraNavigator.pushAndPopAll(Route.SignIn)
            } catch (e: CancellationException) {
                ZeraNavigator.navigationStatus(true)
                _state.value = _state.value.copy(isLoading = false)
                throw e
            } catch (e: Exception) {
                ZeraNavigator.navigationStatus(true)
                _state.value = _state.value.copy(isLoading = false, errorMessage = userFacingError(e))
            }
        }
    }

    override fun onCleared() {
        ZeraNavigator.navigationStatus(true)
        super.onCleared()
    }

    private fun userFacingError(error: Exception): String {
        // 400 não distingue token desconhecido, usado ou expirado.
        if (error is HttpException && error.code() == 400) return INVALID_LINK_MESSAGE
        return "Não foi possível redefinir a senha. Tente novamente."
    }

    private fun isStrongPassword(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGTH &&
            password.any(Char::isUpperCase) &&
            password.any(Char::isLowerCase) &&
            password.any(Char::isDigit) &&
            password.any { !it.isLetterOrDigit() }
    }

    companion object {
        private const val INVALID_LINK_MESSAGE =
            "Link de recuperação inválido ou expirado. Solicite um novo."
        private const val MIN_PASSWORD_LENGTH = 8
        private const val PASSWORD_RULES_MESSAGE =
            "A senha deve ter 8 caracteres ou mais, incluindo letra maiúscula, letra minúscula, número e caractere especial."
    }
}
