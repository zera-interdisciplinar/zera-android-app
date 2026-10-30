package com.zera.android.viewmodel.main.employees

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class EmployeeRegisterState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val unit: String = "",
    val unitOptions: List<String> = emptyList(),
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

class EmployeeRegisterViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(EmployeeRegisterState())
    val state = _state

    init {
        loadUnits()
    }

    private fun loadUnits() {
        // TODO: carregar as unidades da organização do gestor logado para o dropdown "Unidade".
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onNameChange(value: String) {
        _state.value = _state.value.copy(name = value, nameError = null)
    }

    fun onEmailChange(value: String) {
        _state.value = _state.value.copy(email = value, emailError = null)
    }

    fun onPhoneChange(value: String) {
        _state.value = _state.value.copy(phone = value, phoneError = null)
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
            phoneError = phoneValidationError(current.phone),
            errorMessage = null,
        )
        _state.value = validated
        if (validated.nameError != null || validated.emailError != null || validated.phoneError != null) return
        // TODO: criar o convite com nome, e-mail, telefone e unidade. Hoje `POST /invitations`
        //  só recebe `managerId` + `inviteeName` (ver `CreateInvitation`).
    }

    companion object {
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

        /** Telefone é opcional; quando preenchido, precisa ter 10 ou 11 dígitos. */
        internal fun phoneValidationError(value: String): String? {
            if (value.isBlank()) return null
            val digits = value.count(Char::isDigit)
            return if (digits in 10..11) null else "Telefone inválido."
        }
    }
}
