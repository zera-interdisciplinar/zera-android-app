package com.zera.android.viewmodel.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zera.android.model.entity.user.SelfUserResponseDTO
import com.zera.android.model.usecase.auth.SingIn
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import kotlinx.coroutines.launch


data class SingInState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SingInViewModel : ViewModel() {
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

        // A simple client-side validation for email and password
        if (state.value.email.isBlank() || state.value.password.isBlank()) {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = "Email and password cannot be empty"
            )
            return
        }

        // Call the authentication use case here
        viewModelScope.launch {
            try {
                var selfUser : SelfUserResponseDTO = useCase.execute(state.value.email, state.value.password)
                _state.value = _state.value.copy(isLoading = false)

                // in case of success navigate to the home screen according to the role of the user
                when (selfUser.role) {
                    "EMPLOYEE" -> ZeraNavigator.pushAndPop(Route.EmployeeHome)
                    "MANAGER" -> ZeraNavigator.pushAndPop(Route.ManagerHome)

                    // default to login screen
                    else -> {
                        _state.value = _state.value.copy(isLoading = false, errorMessage = "Invalid role")
                        ZeraNavigator.pushAndPop(Route.Login)
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}