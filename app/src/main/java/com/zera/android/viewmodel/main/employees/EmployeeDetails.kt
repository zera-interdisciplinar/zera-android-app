package com.zera.android.viewmodel.main.employees

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.profile.ProfileViewModel

data class EmployeeDetailsState(
    val employeeId: String = "",
    val name: String = "",
    val initials: String = "",
    val photoUrl: String? = null,
    val email: String = "",
    val phone: String = "",
    val position: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class EmployeeDetailsViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(EmployeeDetailsState())
    val state = _state

    fun loadEmployee(employeeId: String) {
        if (employeeId.isBlank()) {
            // Sem id não há o que carregar; evita deixar o isLoading inicial preso em true.
            _state.value = _state.value.copy(isLoading = false)
            return
        }
        _state.value = _state.value.copy(employeeId = employeeId, isLoading = true, errorMessage = null)
        // TODO: buscar o operário (`GET /users/{id}` + `GET /telephone/user?userId=`) e preencher
        //  nome, e-mail, telefone, cargo e a foto (`imageUrl`) do avatar, com as iniciais via
        //  [initialsFrom] para quando não houver foto.
        _state.value = _state.value.copy(isLoading = false)
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onDeleteClick() {
        // TODO: confirmar e excluir o perfil do operário.
    }

    companion object {
        internal fun initialsFrom(name: String): String = ProfileViewModel.initialsFrom(name)
    }
}
