package com.zera.android.viewmodel.auth.passwordRecovery

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.remote.client.isConnectionError
import com.zera.android.model.usecase.auth.RequestPasswordReset
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class PasswordRecoveryEmailState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class PasswordRecoveryEmailViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(PasswordRecoveryEmailState())
    val state = _state

    private val requestPasswordReset = RequestPasswordReset()

    fun onEmailChange(newEmail: String) {
        _state.value = _state.value.copy(email = newEmail, errorMessage = null, successMessage = null)
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

        _state.value = current.copy(isLoading = true, errorMessage = null, successMessage = null)
        viewModelScope.launch {
            try {
                requestPasswordReset.execute(current.email)
                // O backend responde 202 exista a conta ou não, então a mensagem não confirma o e-mail.
                _state.value = _state.value.copy(isLoading = false, successMessage = SUCCESS_MESSAGE)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = userFacingError(e))
            }
        }
    }

    /** Mensagem inline; `null` quando é falta de conexão (a tela cheia já aparece). */
    private fun userFacingError(error: Exception): String? {
        if (error.isConnectionError()) return null
        if (error is HttpException && error.code() == 400) return "Informe um e-mail válido."
        return "Não foi possível enviar o e-mail. Tente novamente."
    }

    companion object {
        private const val SUCCESS_MESSAGE =
            "Se existir uma conta com este e-mail, enviamos um link para redefinir a senha."
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
