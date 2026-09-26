package com.zera.android.viewmodel.shared

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class ProfileState(
    val displayName: String = "",
    val fullName: String = "",
    val role: String = "",
    val company: String = "",
    val initials: String = "",
    val photoUrl: String? = null,
    val email: String = "",
    val phone: String = "",
    val position: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class ProfileViewModel : ZeraViewModel() {
    // TODO: substituir os dados de exemplo pela chamada ao back (ver loadProfile)
    private val _state = mutableStateOf(
        ProfileState(
            displayName = "Natalia Flores",
            fullName = "Natalia Cristina Flores",
            role = "Gestora",
            company = "Empresa Zera",
            initials = initialsFrom("Natalia Flores"),
            email = "natalia@zera.com.br",
            phone = "(11) 97520-6322",
            position = "Gestora",
        )
    )
    val state = _state

    init {
        loadProfile()
    }

    private fun loadProfile() {
        // TODO: buscar os dados do usuário logado no back (photoUrl incluída) e atualizar o
        // _state (isLoading / errorMessage inclusos)
    }

    private fun initialsFrom(name: String): String {
        val words = name.trim().split(" ").filter { it.isNotBlank() }
        return listOfNotNull(words.firstOrNull(), words.lastOrNull())
            .joinToString(separator = "") { it.first().uppercase() }
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onChangePhotoClick() {
        // TODO: fluxo de troca de foto ainda não definido (regra de negócio pendente)
    }

    fun onSettingsClick() {
        // TODO: navegar para a tela de configurações (ainda não existe)
    }

    fun onEditNameClick() {
        // TODO: fluxo de edição de nome ainda não definido (regra de negócio pendente)
    }

    fun onEditEmailClick() {
        // TODO: fluxo de edição de e-mail ainda não definido (regra de negócio pendente)
    }

    fun onEditPhoneClick() {
        // TODO: fluxo de edição de telefone ainda não definido (regra de negócio pendente)
    }
}
