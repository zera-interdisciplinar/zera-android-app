package com.zera.android.viewmodel.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.entity.user.ProfileUser
import com.zera.android.model.entity.user.UserRole
import com.zera.android.model.usecase.auth.SignOut
import com.zera.android.model.usecase.telephone.GetTelephone
import com.zera.android.model.usecase.user.GetProfile
import com.zera.android.model.usecase.user.RenameUser
import com.zera.android.model.usecase.user.UpdateUserEmail
import com.zera.android.model.usecase.user.UpdateUserImage
import com.zera.android.model.usecase.user.UploadAvatar
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.appMessageOrFallback
import com.zera.android.viewmodel.errorDescription
import com.zera.android.viewmodel.loadErrorDescription
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val isLoggingOut: Boolean = false,
    val errorMessage: String? = null,
    val snackbarMessage: String? = null,
    /** Rascunho do bottom sheet "Editar perfil"; `null` = sheet fechado. */
    val editDraft: ProfileEditDraft? = null,
)

data class ProfileEditDraft(
    val fullName: String = "",
    val email: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
)

/** Quais campos do rascunho diferem do perfil atual e o usuário pode alterar. */
internal data class ProfileEditChanges(val name: Boolean, val email: Boolean) {
    val any: Boolean get() = name || email
}

class ProfileViewModel(
    private val getProfile: GetProfile = GetProfile(),
    private val getTelephone: GetTelephone = GetTelephone(),
    private val renameUser: RenameUser = RenameUser(),
    private val updateUserEmail: UpdateUserEmail = UpdateUserEmail(),
    private val uploadAvatar: UploadAvatar = UploadAvatar(),
    private val updateUserImage: UpdateUserImage = UpdateUserImage(),
) : ZeraViewModel() {
    private val _state = mutableStateOf(ProfileState(isLoading = true))
    val state = _state

    private var apiRole: String = ""

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
                    loaded = profile.copy(
                        telephoneId = telephone?.telephoneId,
                        phone = telephone?.number.orEmpty(),
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _state.value = stateFrom(profile, apiRole).copy(
                        isLoading = false,
                        errorMessage = errorDescription(e, loadErrorDescription("o telefone")),
                    )
                    return@launch
                }
                _state.value = stateFrom(loaded, apiRole).copy(isLoading = false, errorMessage = null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = errorDescription(e, loadErrorDescription("o perfil")),
                )
            }
        }
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onSettingsClick() {
        ZeraNavigator.push(Route.Configurations, ScreenAnimation.SlideHorizontal)
    }

    fun onLogoutClick() {
        _state.value = _state.value.copy(showLogoutDialog = true)
    }

    fun onLogoutDismiss() {
        _state.value = _state.value.copy(showLogoutDialog = false)
    }

    fun onLogoutConfirm() {
        if (_state.value.isLoggingOut) return
        _state.value = _state.value.copy(showLogoutDialog = false, isLoggingOut = true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) { SignOut().execute() }
            ZeraNavigator.pushAndPopAll(Route.Welcome)
        }
    }

    fun onErrorDismiss() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun onSnackbarShown() {
        _state.value = _state.value.copy(snackbarMessage = null)
    }

    fun onPhotoPicked(bytes: ByteArray, mimeType: String) {
        if (_state.value.isSaving) return
        val error = photoValidationError(bytes, mimeType)
        if (error != null) {
            _state.value = _state.value.copy(errorMessage = error)
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
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    isUploadingPhoto = false,
                    errorMessage = errorDescription(e, photoWriteError(e)),
                )
            }
        }
    }

    fun onEditClick() {
        val current = _state.value
        _state.value = current.copy(
            editDraft = ProfileEditDraft(
                fullName = current.fullName,
                email = current.email,
            ),
        )
    }

    fun onEditDismiss() {
        if (_state.value.isSaving) return
        _state.value = _state.value.copy(editDraft = null)
    }

    fun onEditNameChange(value: String) {
        updateDraft { it.copy(fullName = value, nameError = null) }
    }

    fun onEditEmailChange(value: String) {
        updateDraft { it.copy(email = value, emailError = null) }
    }

    /**
     * Valida o rascunho e grava só o que mudou (o nome só para gestor). Cada campo é um
     * endpoint: se um falhar, os anteriores já ficam salvos no state e o sheet continua aberto
     * com o popup de erro por cima, e um novo "Salvar" reenvia apenas o que falta.
     */
    fun onEditSave() {
        val current = _state.value
        val draft = current.editDraft ?: return
        if (current.isSaving) return

        val changes = editChangesFrom(current, draft)
        val validated = draft.copy(
            nameError = if (changes.name) nameValidationError(draft.fullName) else null,
            emailError = if (changes.email) emailValidationError(draft.email) else null,
        )
        if (validated.nameError != null || validated.emailError != null) {
            _state.value = current.copy(editDraft = validated)
            return
        }
        if (!changes.any) {
            _state.value = current.copy(editDraft = null)
            return
        }

        _state.value = current.copy(isSaving = true, errorMessage = null, editDraft = validated)
        viewModelScope.launch {
            var writingEmail = false
            try {
                if (changes.name) {
                    val name = draft.fullName.trim()
                    renameUser.execute(name)
                    _state.value = _state.value.copy(
                        displayName = name,
                        fullName = name,
                        initials = initialsFrom(name),
                    )
                }
                if (changes.email) {
                    val email = draft.email.trim()
                    writingEmail = true
                    updateUserEmail.execute(email)
                    writingEmail = false
                    _state.value = _state.value.copy(email = email)
                }
                _state.value = _state.value.copy(
                    isSaving = false,
                    editDraft = null,
                    snackbarMessage = "Perfil atualizado.",
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = if (writingEmail) emailWriteError(e) else profileWriteError(e)
                _state.value = _state.value.copy(isSaving = false, errorMessage = message)
            }
        }
    }

    private fun updateDraft(transform: (ProfileEditDraft) -> ProfileEditDraft) {
        val draft = _state.value.editDraft ?: return
        _state.value = _state.value.copy(editDraft = transform(draft))
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
            )
        }

        internal fun editChangesFrom(state: ProfileState, draft: ProfileEditDraft) = ProfileEditChanges(
            name = state.canEditName && draft.fullName.trim() != state.fullName.trim(),
            email = state.canEditEmail && draft.email.trim() != state.email.trim(),
        )

        internal fun canEditName(role: String): Boolean = role == UserRole.MANAGER

        internal fun canEditEmail(): Boolean = true

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
                    "Sem permissão para enviar a foto."
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

        internal fun cacheBust(url: String): String {
            val separator = if (url.contains("?")) "&" else "?"
            return "$url${separator}t=${System.currentTimeMillis()}"
        }

        /** Descrição do popup para a falha ao salvar o perfil; `null` quando é falta de conexão. */
        internal fun profileWriteError(error: Exception): String? {
            if (error is HttpException) {
                return when (error.code()) {
                    400 -> "Não foi possível salvar. Verifique os dados."
                    403 -> "Sem permissão para alterar este campo."
                    404 -> "Registro não encontrado."
                    409 -> "Este usuário já possui telefone."
                    else -> "Não foi possível salvar. Tente novamente."
                }
            }
            return appMessageOrFallback(error, "Não foi possível salvar. Tente novamente.")
        }

        internal fun emailWriteError(error: Exception): String? {
            if (error is HttpException && error.code() == 400) {
                return "E-mail já em uso ou inválido."
            }
            return profileWriteError(error)
        }
    }
}
