package com.zera.android.viewmodel.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.usecase.auth.InvitationUseCase
import com.zera.android.model.usecase.auth.SingIn
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class SignUpState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val token: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SignUpViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(SignUpState())
    val state = _state

    private val invitationUseCase = InvitationUseCase()
    private val signInUseCase = SingIn()

    fun onNameChange(newName: String) {
        _state.value = _state.value.copy(name = newName)
    }

    fun onEmailChange(newEmail: String) {
        _state.value = _state.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _state.value = _state.value.copy(password = newPassword)
    }

    fun onTokenChange(newToken: String) {
        _state.value = _state.value.copy(token = newToken)
    }

    fun signUp() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        val current = _state.value
        if (current.token.isBlank() || current.name.isBlank() || current.email.isBlank() || current.password.isBlank()) {
            _state.value = current.copy(
                isLoading = false,
                errorMessage = "Preencha nome, e-mail, senha e o código de convite."
            )
            return
        }
        if (current.token.length != 6) {
            _state.value = current.copy(
                isLoading = false,
                errorMessage = "O código de convite deve ter 6 dígitos."
            )
            return
        }
        if (!isValidEmail(current.email)) {
            _state.value = current.copy(
                isLoading = false,
                errorMessage = "Informe um e-mail válido."
            )
            return
        }

        viewModelScope.launch {
            try {
                invitationUseCase.execute(current.token, current.name, current.email, current.password)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = userFacingError(e, "Não foi possível cadastrar. Tente novamente.")
                )
                return@launch
            }

            try {
                val selfUser = signInUseCase.execute(current.email, current.password)
                _state.value = _state.value.copy(isLoading = false)
                when (selfUser.role) {
                    "MANAGER" -> ZeraNavigator.pushAndPopAll(Route.ManagerHome)
                    "EMPLOYEE" -> ZeraNavigator.pushAndPopAll(Route.EmployeeHome)
                    else -> {
                        _state.value = _state.value.copy(
                            errorMessage = "Cadastro feito, mas o perfil retornado não é válido. Entre pelo login."
                        )
                        ZeraNavigator.pushAndPop(Route.SignIn)
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Cadastro feito, mas o login automático falhou. Entre com o mesmo e-mail."
                )
                ZeraNavigator.pushAndPop(Route.SignIn)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return EMAIL_REGEX.matches(email.trim())
    }

    private fun userFacingError(error: Exception, fallback: String): String {
        if (error is HttpException) {
            return when (error.code()) {
                400, 409, 422 -> "Código inválido ou já utilizado. Confira o convite e tente de novo."
                401, 403 -> "Não foi possível cadastrar com este convite."
                else -> fallback
            }
        }
        return error.message?.takeIf { it.isNotBlank() } ?: fallback
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
