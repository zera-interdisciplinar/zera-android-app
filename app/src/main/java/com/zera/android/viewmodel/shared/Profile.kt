package com.zera.android.viewmodel.shared

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.entity.user.ProfileUser
import com.zera.android.model.entity.user.UserRole
import com.zera.android.model.usecase.telephone.GetTelephone
import com.zera.android.model.usecase.telephone.SaveTelephone
import com.zera.android.model.usecase.user.GetProfile
import com.zera.android.model.usecase.user.RenameUser
import com.zera.android.model.usecase.user.UpdateUserEmail
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
    val canEditName: Boolean = false,
    val canEditEmail: Boolean = true,
    val canEditPhone: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

class ProfileViewModel(
    private val getProfile: GetProfile = GetProfile(),
    private val getTelephone: GetTelephone = GetTelephone(),
    private val renameUser: RenameUser = RenameUser(),
    private val updateUserEmail: UpdateUserEmail = UpdateUserEmail(),
    private val saveTelephone: SaveTelephone = SaveTelephone(),
) : ZeraViewModel() {
    private val _state = mutableStateOf(ProfileState(isLoading = true))
    val state = _state

    private var apiRole: String = ""
    private var telephoneId: String? = null

    init {
        loadProfile()
    }

    private fun loadProfile() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val profile = getProfile.execute()
                apiRole = profile.role
                var loaded = profile
                try {
                    val telephone = getTelephone.execute(profile.userId)
                    telephoneId = telephone?.telephoneId
                    loaded = profile.copy(
                        telephoneId = telephone?.telephoneId,
                        phone = telephone?.number.orEmpty(),
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    telephoneId = null
                    _state.value = stateFrom(profile, apiRole).copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Não foi possível carregar o telefone.",
                    )
                    return@launch
                }
                _state.value = stateFrom(loaded, apiRole).copy(isLoading = false, errorMessage = null)
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

    fun onNameConfirm(name: String) {
        if (!canEditName(apiRole) || _state.value.isSaving) return
        val error = nameValidationError(name)
        if (error != null) {
            _state.value = _state.value.copy(errorMessage = error)
            return
        }
        val trimmed = name.trim()
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, errorMessage = null)
            try {
                renameUser.execute(trimmed)
                _state.value = _state.value.copy(
                    displayName = trimmed,
                    fullName = trimmed,
                    initials = initialsFrom(trimmed),
                    isSaving = false,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSaving = false, errorMessage = profileWriteError(e))
            }
        }
    }

    fun onEmailConfirm(email: String) {
        if (!canEditEmail() || _state.value.isSaving) return
        val error = emailValidationError(email)
        if (error != null) {
            _state.value = _state.value.copy(errorMessage = error)
            return
        }
        val trimmed = email.trim()
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, errorMessage = null)
            try {
                updateUserEmail.execute(trimmed)
                _state.value = _state.value.copy(email = trimmed, isSaving = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSaving = false, errorMessage = emailWriteError(e))
            }
        }
    }

    fun onPhoneConfirm(phone: String) {
        if (!canEditPhone(apiRole) || _state.value.isSaving) return
        val error = phoneValidationError(phone)
        if (error != null) {
            _state.value = _state.value.copy(errorMessage = error)
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, errorMessage = null)
            try {
                val saved = saveTelephone.execute(telephoneId, phone)
                telephoneId = saved.telephoneId
                _state.value = _state.value.copy(
                    phone = saved.number,
                    isSaving = false,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSaving = false, errorMessage = profileWriteError(e))
            }
        }
    }

    companion object {
        internal fun stateFrom(profile: ProfileUser, role: String = profile.role): ProfileState {
            val roleLabel = roleLabel(role)
            return ProfileState(
                displayName = profile.name,
                fullName = profile.name,
                role = roleLabel,
                company = "",
                initials = initialsFrom(profile.name),
                photoUrl = profile.imageUrl,
                email = profile.email,
                phone = profile.phone,
                position = roleLabel,
                canEditName = canEditName(role),
                canEditEmail = canEditEmail(),
                canEditPhone = canEditPhone(role),
            )
        }

        internal fun canEditName(role: String): Boolean = role == UserRole.MANAGER

        internal fun canEditEmail(): Boolean = true

        internal fun canEditPhone(role: String): Boolean = role == UserRole.MANAGER

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

        internal fun nameValidationError(name: String): String? =
            if (name.trim().isEmpty()) "Nome inválido" else null

        internal fun emailValidationError(email: String): String? =
            if (email.contains("@")) null else "E-mail inválido"

        internal fun phoneValidationError(phone: String): String? {
            val digits = phone.count(Char::isDigit)
            return if (digits in 10..11) null else "Telefone inválido"
        }

        internal fun saveTelephoneUsesCreate(telephoneId: String?): Boolean = telephoneId.isNullOrBlank()

        internal fun profileWriteError(error: Exception): String {
            if (error is HttpException) {
                return when (error.code()) {
                    400 -> "Não foi possível salvar. Verifique os dados."
                    403 -> "Sem permissão para alterar este campo."
                    404 -> "Registro não encontrado."
                    409 -> "Este usuário já possui telefone."
                    else -> "Não foi possível salvar. Tente novamente."
                }
            }
            return error.message?.takeIf { it.isNotBlank() }
                ?: "Não foi possível salvar. Tente novamente."
        }

        internal fun emailWriteError(error: Exception): String {
            if (error is HttpException && error.code() == 400) {
                return "E-mail já em uso ou inválido."
            }
            return profileWriteError(error)
        }
    }
}
