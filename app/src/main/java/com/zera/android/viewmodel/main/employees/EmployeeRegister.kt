package com.zera.android.viewmodel.main.employees

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.remote.client.isConnectionError
import com.zera.android.model.usecase.team.CreateInvitation
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class EmployeeRegisterState(
    val name: String = "",
    val email: String = "",
    val unit: String = "",
    val unitOptions: List<String> = emptyList(),
    val nameError: String? = null,
    val emailError: String? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

class EmployeeRegisterViewModel(
    private val createInvitation: CreateInvitation = CreateInvitation(),
) : ZeraViewModel() {
    private val _state = mutableStateOf(EmployeeRegisterState())
    val state = _state

    init {
        loadUnits()
    }

    private fun loadUnits() {
        // TODO: carregar as unidades da organização do gestor logado para o dropdown "Unidade".
        //  O adm-core ainda não tem rota de listagem de unidades; o convite herda o `unitId` do gestor.
    }

    fun onBackClick() {
        if (_state.value.isSubmitting) return
        ZeraNavigator.goBack()
    }

    fun onErrorDismiss() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun onNameChange(value: String) {
        _state.value = _state.value.copy(name = value, nameError = null)
    }

    fun onEmailChange(value: String) {
        _state.value = _state.value.copy(email = value, emailError = null)
    }

    fun onUnitChange(value: String) {
        _state.value = _state.value.copy(unit = value)
    }

    fun onCreateInviteClick() {
        val current = _state.value
        if (current.isSubmitting) return
        val validated = current.copy(
            nameError = nameValidationError(current.name),
            emailError = emailValidationError(current.email),
            errorMessage = null,
        )
        if (validated.nameError != null || validated.emailError != null) {
            _state.value = validated
            return
        }
        _state.value = validated.copy(isSubmitting = true)
        viewModelScope.launch {
            try {
                // TODO: enviar e-mail e unidade quando `POST /invitations` aceitar esses
                //  campos. Hoje o contrato só recebe `managerId` + `inviteeName`.
                createInvitation.execute(validated.name)
                // A lista de Colaboradores recarrega os convites ao voltar a ficar visível.
                ZeraNavigator.goBack()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessage = createInviteError(e),
                )
            }
        }
    }

    companion object {
        /** Descrição do popup para a falha ao criar o convite; `null` quando é falta de conexão. */
        internal fun createInviteError(error: Exception): String? {
            if (error.isConnectionError()) return null
            if (error is HttpException) {
                return when (error.code()) {
                    400 -> "Nome inválido ou gestor não reconhecido."
                    404 -> "Gestor não encontrado."
                    401, 403 -> "Sem permissão para criar convite."
                    else -> "Não foi possível criar o convite. Tente novamente."
                }
            }
            return "Não foi possível criar o convite. Tente novamente."
        }

        internal fun nameValidationError(value: String): String? =
            if (value.isBlank()) "Informe o nome do operário." else null

        internal fun emailValidationError(value: String): String? {
            val trimmed = value.trim()
            return when {
                trimmed.isEmpty() -> "Informe o e-mail do operário."
                !trimmed.contains("@") -> "E-mail inválido."
                else -> null
            }
        }
    }
}
