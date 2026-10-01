package com.zera.android.viewmodel.shared

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.entity.user.ProfileUser
import com.zera.android.model.entity.user.UserRole
import com.zera.android.model.usecase.user.GetProfile
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

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

class ProfileViewModel(
    private val getProfile: GetProfile = GetProfile(),
) : ZeraViewModel() {
    private val _state = mutableStateOf(ProfileState(isLoading = true))
    val state = _state

    init {
        loadProfile()
    }

    private fun loadProfile() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val profile = getProfile.execute()
                _state.value = stateFrom(profile).copy(isLoading = false, errorMessage = null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Não foi possível carregar o perfil.",
                )
            }
        }
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

    companion object {
        internal fun stateFrom(profile: ProfileUser): ProfileState {
            val roleLabel = roleLabel(profile.role)
            return ProfileState(
                displayName = profile.name,
                fullName = profile.name,
                role = roleLabel,
                company = "",
                initials = initialsFrom(profile.name),
                photoUrl = profile.imageUrl,
                email = profile.email,
                phone = "",
                position = roleLabel,
            )
        }

        internal fun roleLabel(role: String): String = when (role) {
            UserRole.MANAGER -> "Gestor"
            UserRole.EMPLOYEE -> "Operador"
            else -> role
        }

        internal fun initialsFrom(name: String): String {
            val words = name.trim().split(" ").filter { it.isNotBlank() }
            return listOfNotNull(words.firstOrNull(), words.lastOrNull())
                .joinToString(separator = "") { it.first().uppercase() }
        }

        internal fun roleCaption(role: String, company: String): String {
            val trimmedCompany = company.trim()
            return if (trimmedCompany.isEmpty()) role else "$role · $trimmedCompany"
        }
    }
}
