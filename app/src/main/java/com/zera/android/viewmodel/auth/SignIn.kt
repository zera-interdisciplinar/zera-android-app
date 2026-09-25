package com.zera.android.viewmodel.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.entity.user.SelfUserResponseDTO
import com.zera.android.model.usecase.auth.SingIn
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.homeRouteForRole
import kotlinx.coroutines.launch

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

    fun signIn() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        if (state.value.email.isBlank() || state.value.password.isBlank()) {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = "Email and password cannot be empty"
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
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}
