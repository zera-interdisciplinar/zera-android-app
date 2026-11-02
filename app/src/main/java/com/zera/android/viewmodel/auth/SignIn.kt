package com.zera.android.viewmodel.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.user.SelfUserResponseDTO
import com.zera.android.model.remote.client.isConnectionError
import com.zera.android.model.usecase.auth.SingIn
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.navigation.consumePendingAlertDeepLink
import com.zera.android.view.navigation.consumePendingItemDeepLink
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.homeRouteForRole
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class SingInState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SingInViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(SingInState())
    val state = _state

    val useCase = SingIn()

    fun onEmailChange(newEmail: String) {
        _state.value = _state.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _state.value = _state.value.copy(password = newPassword)
    }

    fun onForgotPasswordClick() {
        ZeraNavigator.push(Route.PasswordRecoveryEmail)
    }

    fun signIn() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        if (state.value.email.isBlank() || state.value.password.isBlank()) {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = "Informe e-mail e senha."
            )
            return
        }

        viewModelScope.launch {
            try {
                val selfUser: SelfUserResponseDTO = useCase.execute(state.value.email, state.value.password)
                _state.value = _state.value.copy(isLoading = false)

                val destination = homeRouteForRole(selfUser.role)
                if (destination == null) {
                    useCase.clearSession()
                    _state.value = _state.value.copy(
                        errorMessage = "Perfil inválido. Tente entrar novamente.",
                    )
                    return@launch
                }
                ZeraNavigator.pushAndPopAll(destination)
                consumePendingAlertDeepLink()
                consumePendingItemDeepLink()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = signInError(e))
            }
        }
    }

    companion object {
        /** Mensagem inline do login; `null` quando é falta de conexão (a tela cheia já aparece). */
        internal fun signInError(error: Exception): String? {
            if (error.isConnectionError()) return null
            if (error is HttpException) {
                return when (error.code()) {
                    400, 401, 403, 404 -> "E-mail ou senha incorretos."
                    else -> "Não foi possível entrar. Tente novamente."
                }
            }
            return "Não foi possível entrar. Tente novamente."
        }
    }
}
