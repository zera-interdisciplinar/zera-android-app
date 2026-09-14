package com.zera.android.viewmodel.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class SignUpState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val token: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SignUpViewModel : ViewModel() {
    private val _state = mutableStateOf(SignUpState())
    val state = _state

    // TODO: instanciar o use case de cadastro quando o back estiver pronto (ver SingInViewModel.useCase)

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
        // TODO: chamar o use case de cadastro e navegar de acordo com o resultado (ver SingInViewModel.signIn)
    }
}
