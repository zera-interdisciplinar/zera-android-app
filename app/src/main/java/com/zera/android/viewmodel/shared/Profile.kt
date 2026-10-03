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
import com.zera.android.model.usecase.user.UpdateUserImage
import com.zera.android.model.usecase.user.UploadAvatar
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
    val isUploadingPhoto: Boolean = false,
    val errorMessage: String? = null,
    val snackbarMessage: String? = null,
    val snackbarIsError: Boolean = true,
)

class ProfileViewModel(
    private val getProfile: GetProfile = GetProfile(),
    private val getTelephone: GetTelephone = GetTelephone(),
    private val renameUser: RenameUser = RenameUser(),
    private val updateUserEmail: UpdateUserEmail = UpdateUserEmail(),
    private val saveTelephone: SaveTelephone = SaveTelephone(),
    private val uploadAvatar: UploadAvatar = UploadAvatar(),
    private val updateUserImage: UpdateUserImage = UpdateUserImage(),
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

    fun onSettingsClick() {
        // TODO: navegar para a tela de configurações (ainda não existe)
    }

    fun onSnackbarShown() {
        _state.value = _state.value.copy(snackbarMessage = null)
    }

    private fun showSnackbar(message: String, isError: Boolean, isSaving: Boolean? = null) {
        _state.value = _state.value.copy(
            isSaving = isSaving ?: _state.value.isSaving,
            snackbarMessage = message,
            snackbarIsError = isError,
        )
    }

    fun onPhotoPicked(bytes: ByteArray, mimeType: String) {
        if (_state.value.isSaving) return
        val error = photoValidationError(bytes, mimeType)
        if (error != null) {
            showSnackbar(error, isError = true)
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isSaving = true,
                isUploadingPhoto = true,
                errorMessage = null,
            )
            try {
                val imageUrl = uploadAvatar.execute(bytes, mimeType)
                updateUserImage.execute(imageUrl)
                _state.value = _state.value.copy(
                    photoUrl = cacheBust(imageUrl),
                    isSaving = false,
                    isUploadingPhoto = false,
                    snackbarMessage = "Foto atualizada.",
                    snackbarIsError = false,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    isUploadingPhoto = false,
                    snackbarMessage = photoWriteError(e),
                    snackbarIsError = true,
                )
            }
        }
    }

    fun onNameConfirm(name: String) {
        if (!canEditName(apiRole) || _state.value.isSaving) return
        val error = nameValidationError(name)
        if (error != null) {
            showSnackbar(error, isError = true)
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
                    snackbarMessage = "Nome atualizado.",
                    snackbarIsError = false,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showSnackbar(profileWriteError(e), isError = true, isSaving = false)
            }
        }
    }

    fun onEmailConfirm(email: String) {
        if (!canEditEmail() || _state.value.isSaving) return
        val error = emailValidationError(email)
        if (error != null) {
            showSnackbar(error, isError = true)
            return
        }
        val trimmed = email.trim()
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, errorMessage = null)
            try {
                updateUserEmail.execute(trimmed)
                _state.value = _state.value.copy(
                    email = trimmed,
                    isSaving = false,
                    snackbarMessage = "E-mail atualizado.",
                    snackbarIsError = false,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showSnackbar(emailWriteError(e), isError = true, isSaving = false)
            }
        }
    }

    fun onPhoneConfirm(phone: String) {
        if (!canEditPhone(apiRole) || _state.value.isSaving) return
        val error = phoneValidationError(phone)
        if (error != null) {
            showSnackbar(error, isError = true)
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
                    snackbarMessage = "Telefone atualizado.",
                    snackbarIsError = false,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showSnackbar(profileWriteError(e), isError = true, isSaving = false)
            }
        }
    }

    companion object {
        internal const val MaxPhotoBytes = 5 * 1024 * 1024
        private val AllowedPhotoMimes = setOf(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp",
        )

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

        internal fun photoValidationError(bytes: ByteArray, mimeType: String): String? {
            if (mimeType.lowercase() !in AllowedPhotoMimes) {
                return "Use uma foto JPEG, PNG ou WebP."
            }
            if (bytes.isEmpty() || bytes.size > MaxPhotoBytes) {
                return "A foto deve ter no máximo 5 MB."
            }
            return null
        }

        internal fun photoWriteError(error: Exception): String {
            val raw = error.message.orEmpty().lowercase()
            return when {
                "não configurado" in raw -> "O envio de foto ainda não está disponível."
                "invalidmimetype" in raw || "mime type" in raw ->
                    "Este tipo de arquivo não é permitido. Use JPEG, PNG ou WebP."
                "row-level security" in raw || "accessdenied" in raw ->
                    "Sem permissão para enviar a foto. Confira a política do Storage."
                "bucket" in raw -> "Não foi possível enviar a foto. Tente novamente."
                "jwt" in raw || "apikey" in raw || "unauthorized" in raw ->
                    "Não foi possível enviar a foto. Tente novamente."
                error is HttpException && error.code() == 400 ->
                    "Não foi possível salvar a foto no perfil."
                error is HttpException && error.code() in listOf(401, 403) ->
                    "Sem permissão para alterar a foto."
                else -> "Não foi possível enviar a foto. Tente novamente."
            }
        }

        internal fun saveTelephoneUsesCreate(telephoneId: String?): Boolean = telephoneId.isNullOrBlank()

        internal fun cacheBust(url: String): String {
            val separator = if (url.contains("?")) "&" else "?"
            return "$url${separator}t=${System.currentTimeMillis()}"
        }

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
