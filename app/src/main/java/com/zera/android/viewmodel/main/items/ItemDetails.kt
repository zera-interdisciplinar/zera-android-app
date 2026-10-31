package com.zera.android.viewmodel.main.items

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.UpdateItemRequestDTO
import com.zera.android.model.usecase.auth.GetSelfUser
import com.zera.android.model.usecase.inventory.ApproveItem
import com.zera.android.model.usecase.inventory.GetItemDetails
import com.zera.android.model.usecase.inventory.RejectItem
import com.zera.android.model.usecase.inventory.StartItemMaintenance
import com.zera.android.model.usecase.inventory.UpdateItem
import com.zera.android.model.usecase.inventory.UploadItemPhoto
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.media.PhotoCapture
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.isEmployeeRole
import com.zera.android.viewmodel.profile.ProfileViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import retrofit2.HttpException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

data class ItemDetailsState(
    val itemId: String = "",
    val itemName: String = "",
    val itemSubtitle: String = "",
    val status: ItemStatus? = null,
    val category: String = "",
    val material: String = "",
    val condition: String = "",
    val conditionCode: String? = null,
    val usageIntensity: Int? = null,
    val hasDamage: Boolean = false,
    val damageCodes: List<String> = emptyList(),
    val serialNumber: String = "",
    val acquiredAt: String = "",
    val notes: String = "",
    val registeredBy: String = "",
    val registeredAt: String = "",
    val photoUrl: String? = null,
    /** `true` enquanto o popup com a foto ampliada está aberto. */
    val showPhotoViewer: Boolean = false,
    /** `true` enquanto a foto recém-tirada está sendo enviada ao Storage. */
    val isUploadingPhoto: Boolean = false,
    val canReview: Boolean = false,
    val canSendToMaintenance: Boolean = false,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    /** Rascunho do bottom sheet "Editar o item"; `null` = sheet fechado. */
    val editDraft: ItemEditDraft? = null,
)

/** Campos do bottom sheet de edição, com os rótulos de UI (condição e tipo de dano). */
data class ItemEditDraft(
    val name: String = "",
    val condition: String = "",
    val usageIntensity: Int = ItemDetailsViewModel.DEFAULT_USAGE_INTENSITY,
    val hasDamage: Boolean = false,
    val damageType: String = "",
    val notes: String = "",
    val nameError: String? = null,
    val damageTypeError: String? = null,
    val notesError: String? = null,
)

class ItemDetailsViewModel : ZeraViewModel() {
    private val getItemDetails = GetItemDetails()
    private val getSelfUser = GetSelfUser()
    private val approveItem = ApproveItem()
    private val rejectItem = RejectItem()
    private val updateItem = UpdateItem()
    private val startItemMaintenance = StartItemMaintenance()
    private val uploadItemPhoto = UploadItemPhoto()

    private val _state = mutableStateOf(ItemDetailsState())
    val state = _state
    private var isEmployee = false

    fun loadItem(itemId: String) {
        if (itemId.isBlank()) {
            // Sem id não há o que carregar; evita deixar o isLoading inicial preso em true.
            _state.value = _state.value.copy(isLoading = false)
            return
        }
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                runCatching { getSelfUser.execute() }
                    .onSuccess { isEmployee = isEmployeeRole(it.role) }
                val item = getItemDetails.execute(itemId)
                _state.value = stateFrom(item, isEmployee).copy(isLoading = false, errorMessage = null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onCameraPermissionDenied() {
        _state.value = _state.value.copy(errorMessage = "Permita o uso da câmera para tirar a foto.")
    }

    /** Recebe a foto já tirada e reduzida (`null` se não deu para processá-la) e a envia ao Storage. */
    fun onPhotoTaken(bytes: ByteArray?) {
        val current = _state.value
        val itemId = current.itemId
        if (itemId.isBlank() || current.isUploadingPhoto) return
        if (bytes == null || bytes.isEmpty()) {
            _state.value = current.copy(errorMessage = "Não foi possível processar a foto.")
            return
        }
        _state.value = current.copy(isUploadingPhoto = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val photoUrl = uploadItemPhoto.execute(itemId, bytes, PhotoCapture.MimeType)
                // TODO: gravar o photoUrl no item — o contrato ainda não tem endpoint/campo para isso
                //  (UpdateItemRequestDTO não tem foto). Até lá a URL só vale nesta sessão da tela.
                _state.value = _state.value.copy(
                    photoUrl = ProfileViewModel.cacheBust(photoUrl),
                    isUploadingPhoto = false,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isUploadingPhoto = false,
                    errorMessage = ProfileViewModel.photoWriteError(e),
                )
            }
        }
    }

    fun onPhotoInspect() {
        if (_state.value.photoUrl == null) return
        _state.value = _state.value.copy(showPhotoViewer = true)
    }

    fun onPhotoViewerDismiss() {
        _state.value = _state.value.copy(showPhotoViewer = false)
    }

    // TODO: upload da nova foto — o contrato ainda não tem endpoint para gravar a foto do item.
    fun onChangePhotoClick() = Unit

    // TODO: remoção da foto — o contrato ainda não tem endpoint para remover a foto do item.
    fun onRemovePhotoClick() = Unit

    fun onEditClick() {
        _state.value = _state.value.copy(editDraft = draftFrom(_state.value), errorMessage = null)
    }

    fun onEditDismiss() {
        _state.value = _state.value.copy(editDraft = null)
    }

    fun onEditNameChange(value: String) {
        updateDraft { it.copy(name = value, nameError = null) }
    }

    fun onEditConditionChange(value: String) {
        updateDraft { it.copy(condition = value) }
    }

    fun onEditUsageIntensityChange(value: Int) {
        updateDraft { it.copy(usageIntensity = value) }
    }

    fun onEditHasDamageChange(selected: Boolean) {
        val hasDamage = !selected
        updateDraft {
            it.copy(
                hasDamage = hasDamage,
                damageType = if (hasDamage) it.damageType else "",
                damageTypeError = null,
            )
        }
    }

    fun onEditDamageTypeChange(value: String) {
        updateDraft { it.copy(damageType = value, damageTypeError = null) }
    }

    fun onEditNotesChange(value: String) {
        updateDraft { it.copy(notes = value, notesError = null) }
    }

    /** Valida o rascunho e manda só o que mudou; o sheet fecha quando o PATCH volta com sucesso. */
    fun onEditSave() {
        val current = _state.value
        val draft = current.editDraft ?: return
        val validated = draft.copy(
            nameError = nameValidationError(draft.name),
            damageTypeError = if (draft.hasDamage && damageCodeFrom(draft.damageType) == null) {
                "Selecione o tipo de dano."
            } else {
                null
            },
            notesError = notesValidationError(draft.notes),
        )
        if (validated.nameError != null || validated.damageTypeError != null || validated.notesError != null) {
            _state.value = current.copy(editDraft = validated)
            return
        }
        val request = editRequestFrom(draftFrom(current), validated)
        if (request == null) {
            onEditDismiss()
            return
        }
        patchItem(request)
    }

    private fun updateDraft(transform: (ItemEditDraft) -> ItemEditDraft) {
        val draft = _state.value.editDraft ?: return
        _state.value = _state.value.copy(editDraft = transform(draft))
    }

    fun onApproveClick() {
        val itemId = _state.value.itemId
        if (itemId.isBlank() || _state.value.isSubmitting || !_state.value.canReview) return
        _state.value = _state.value.copy(isSubmitting = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val item = approveItem.execute(itemId)
                val next = stateFrom(item)
                _state.value = next.copy(isSubmitting = false, errorMessage = null)
                ZeraNavigator.pushAndPop(
                    Route.ItemApproved(
                        itemId = next.itemId,
                        itemName = next.itemName,
                        itemSubtitle = next.itemSubtitle,
                    ),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessage = userFacingError(
                        error = e,
                        conflictMessage = "Este item não pode ser aprovado no status atual.",
                        validationMessage = "Não foi possível aprovar o item.",
                        fallback = "Não foi possível aprovar o item.",
                    ),
                )
            }
        }
    }

    fun onRejectConfirm(reason: String) {
        val itemId = _state.value.itemId
        val trimmed = reason.trim()
        if (itemId.isBlank() || _state.value.isSubmitting || !_state.value.canReview) return
        val reasonError = reasonValidationError(trimmed)
        if (reasonError != null) {
            _state.value = _state.value.copy(errorMessage = reasonError)
            return
        }
        _state.value = _state.value.copy(isSubmitting = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val item = rejectItem.execute(itemId, trimmed)
                _state.value = stateFrom(item, isEmployee).copy(isSubmitting = false, errorMessage = null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessage = userFacingError(
                        error = e,
                        conflictMessage = "Este item não pode ser recusado no status atual.",
                        validationMessage = "Informe um motivo de até 500 caracteres.",
                        fallback = "Não foi possível recusar o item.",
                    ),
                )
            }
        }
    }

    fun onSendToMaintenanceClick() {
        val itemId = _state.value.itemId
        if (itemId.isBlank() || _state.value.isSubmitting || !_state.value.canSendToMaintenance) return
        _state.value = _state.value.copy(isSubmitting = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val item = startItemMaintenance.execute(itemId)
                _state.value = stateFrom(item, isEmployee).copy(isSubmitting = false, errorMessage = null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessage = userFacingError(
                        error = e,
                        conflictMessage = "Este item não pode ir para manutenção no status atual.",
                        validationMessage = "Não foi possível colocar o item em manutenção.",
                        fallback = "Não foi possível colocar o item em manutenção.",
                    ),
                )
            }
        }
    }

    private fun patchItem(request: UpdateItemRequestDTO) {
        val itemId = _state.value.itemId
        if (itemId.isBlank() || _state.value.isSubmitting) return
        _state.value = _state.value.copy(isSubmitting = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val item = updateItem.execute(itemId, request)
                _state.value = stateFrom(item, isEmployee).copy(isSubmitting = false, errorMessage = null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessage = userFacingError(
                        error = e,
                        conflictMessage = "Não foi possível salvar as alterações neste item.",
                        validationMessage = "Os dados enviados são inválidos.",
                        fallback = "Não foi possível editar o item.",
                    ),
                )
            }
        }
    }

    companion object {
        private val ptBr = Locale.forLanguageTag("pt-BR")
        internal const val NAME_MAX_LENGTH = 120
        internal const val NOTES_MAX_LENGTH = 500
        internal const val REASON_MAX_LENGTH = 500
        internal const val DEFAULT_USAGE_INTENSITY = 1
        internal val conditionOptions = listOf("Novo", "Usado", "Semidanificado", "Danificado")
        internal val damageOptions = listOf("Tela quebrada", "Peça faltando", "Não liga", "Enferrujado", "Outro")

        internal fun damageCodeFrom(label: String): String? = when (label) {
            "Tela quebrada" -> "BROKEN_SCREEN"
            "Peça faltando" -> "MISSING_PART"
            "Não liga" -> "DOES_NOT_POWER_ON"
            "Enferrujado" -> "OXIDATION"
            "Outro" -> "OTHER"
            else -> null
        }

        internal fun damageLabel(code: String?): String = when (code) {
            "BROKEN_SCREEN" -> "Tela quebrada"
            "MISSING_PART" -> "Peça faltando"
            "DOES_NOT_POWER_ON" -> "Não liga"
            "OXIDATION" -> "Enferrujado"
            "OTHER" -> "Outro"
            else -> ""
        }

        // O dropdown escolhe um só tipo de dano: exibe o primeiro da lista do backend.
        internal fun draftFrom(state: ItemDetailsState): ItemEditDraft = ItemEditDraft(
            name = state.itemName,
            condition = state.condition,
            usageIntensity = state.usageIntensity ?: DEFAULT_USAGE_INTENSITY,
            hasDamage = state.hasDamage,
            damageType = damageLabel(state.damageCodes.firstOrNull()),
            notes = state.notes,
        )

        /**
         * Monta o PATCH só com os campos que mudaram em relação ao rascunho [initial];
         * `null` quando nada mudou. Danos só vão se "possui danos?" ou o tipo mudou,
         * para não sobrescrever uma lista de vários danos que o usuário nem tocou.
         */
        internal fun editRequestFrom(initial: ItemEditDraft, draft: ItemEditDraft): UpdateItemRequestDTO? {
            val damagesChanged = draft.hasDamage != initial.hasDamage || draft.damageType != initial.damageType
            val request = UpdateItemRequestDTO(
                name = draft.name.trim().takeIf { it != initial.name.trim() },
                condition = conditionCodeFrom(draft.condition).takeIf { draft.condition != initial.condition },
                usageIntensity = draft.usageIntensity.takeIf { it != initial.usageIntensity },
                hasDamages = draft.hasDamage.takeIf { damagesChanged },
                damages = if (!damagesChanged) {
                    null
                } else if (draft.hasDamage) {
                    listOfNotNull(damageCodeFrom(draft.damageType))
                } else {
                    emptyList()
                },
                notes = changedOptionalText(initial.notes, draft.notes),
            )
            return request.takeIf { it != UpdateItemRequestDTO() }
        }

        internal fun statusFrom(value: String?): ItemStatus? = ItemStatus.fromBackend(value)

        internal fun conditionLabel(value: String?): String = when (value) {
            "NEW" -> "Novo"
            "USED" -> "Usado"
            "SEMI_DAMAGED" -> "Semidanificado"
            "DAMAGED" -> "Danificado"
            else -> value.orEmpty()
        }

        internal fun canReview(status: ItemStatus?, isEmployee: Boolean = false): Boolean =
            !isEmployee &&
                (status == ItemStatus.PendingApproval || status == ItemStatus.AwaitingEvaluation)

        // Sem status (item não carregado) o botão não aparece.
        internal fun canSendToMaintenance(status: ItemStatus?): Boolean =
            status != null && status != ItemStatus.InMaintenance

        internal fun notesValidationError(value: String): String? {
            val trimmed = value.trim()
            return if (trimmed.length > NOTES_MAX_LENGTH) {
                "As observações devem ter no máximo $NOTES_MAX_LENGTH caracteres."
            } else {
                null
            }
        }

        internal fun changedOptionalText(current: String, next: String): String? {
            val trimmed = next.trim()
            return trimmed.takeIf { it != current.trim() }
        }

        internal fun nameValidationError(value: String): String? {
            val trimmed = value.trim()
            return when {
                trimmed.isEmpty() -> "Informe o nome do item."
                trimmed.length > NAME_MAX_LENGTH -> "O nome deve ter no máximo $NAME_MAX_LENGTH caracteres."
                else -> null
            }
        }

        internal fun reasonValidationError(value: String): String? {
            val trimmed = value.trim()
            return when {
                trimmed.isEmpty() -> "Informe o motivo da recusa."
                trimmed.length > REASON_MAX_LENGTH -> "O motivo deve ter no máximo $REASON_MAX_LENGTH caracteres."
                else -> null
            }
        }

        internal fun conditionCodeFrom(value: String?): String? {
            val normalized = value?.trim()?.uppercase(Locale.ROOT) ?: return null
            return when (normalized) {
                "NEW", "NOVO" -> "NEW"
                "USED", "USADO" -> "USED"
                "SEMI_DAMAGED", "SEMIDANIFICADO" -> "SEMI_DAMAGED"
                "DAMAGED", "DANIFICADO" -> "DAMAGED"
                else -> null
            }
        }

        internal fun materialsLabel(names: List<String>): String = when {
            names.isEmpty() -> ""
            names.size == 1 -> names.first()
            names.size == 2 -> "${names[0]} e ${names[1]}"
            else -> names.dropLast(1).joinToString(", ") + " e " + names.last()
        }

        internal fun registeredByFrom(item: ItemResponseDTO): String {
            item.createdByName?.trim()?.takeIf { it.isNotEmpty() }?.let { return it }
            val createdBy = item.createdBy
            if (createdBy is JsonObject) {
                return sequenceOf("name", "fullName", "displayName", "userName")
                    .mapNotNull { key ->
                        val value = createdBy[key] as? JsonPrimitive ?: return@mapNotNull null
                        value.contentOrNull?.trim()?.takeIf { it.isNotEmpty() }
                    }
                    .firstOrNull()
                    .orEmpty()
            }
            return ""
        }

        private fun monthLabel(month: Month): String = month
            .getDisplayName(TextStyle.SHORT, ptBr)
            .replace(".", "")
            .replaceFirstChar { it.uppercase() }
            .take(3)

        internal fun formatRegisteredAt(iso: String?): String {
            if (iso.isNullOrBlank()) return ""
            return try {
                val dateTime = LocalDateTime.parse(iso)
                val minute = dateTime.minute.toString().padStart(2, '0')
                "${dateTime.dayOfMonth} de ${monthLabel(dateTime.month)} ${dateTime.year} - ${dateTime.hour}h$minute"
            } catch (_: Exception) {
                iso
            }
        }

        // `acquiredAt` é só data (`yyyy-MM-dd`), sem hora.
        internal fun formatAcquiredAt(iso: String?): String {
            if (iso.isNullOrBlank()) return ""
            return try {
                val date = LocalDate.parse(iso)
                "${date.dayOfMonth} de ${monthLabel(date.month)} ${date.year}"
            } catch (_: Exception) {
                iso
            }
        }

        internal fun stateFrom(
            item: ItemResponseDTO,
            isEmployee: Boolean = false,
        ): ItemDetailsState = ItemDetailsState(
            // Um state montado a partir de um item já carregado nunca está em loading.
            isLoading = false,
            itemId = item.id,
            itemName = item.name,
            itemSubtitle = item.model?.name.orEmpty(),
            status = statusFrom(item.status),
            category = item.model?.category?.name.orEmpty(),
            material = materialsLabel(item.model?.materials.orEmpty().map { it.name }),
            condition = conditionLabel(item.condition),
            conditionCode = item.condition,
            usageIntensity = item.usageIntensity,
            hasDamage = item.hasDamages ?: item.damages.isNotEmpty(),
            damageCodes = item.damages,
            serialNumber = item.serialNumber.orEmpty(),
            acquiredAt = formatAcquiredAt(item.acquiredAt),
            notes = item.notes.orEmpty(),
            registeredBy = registeredByFrom(item),
            registeredAt = formatRegisteredAt(item.createdAt),
            // TODO: alinhar com o back-end o envio do photoUrl do item (hoje o ItemResponse pode vir sem foto).
            photoUrl = item.photoUrl?.takeIf { it.isNotBlank() },
            canReview = canReview(statusFrom(item.status), isEmployee),
            canSendToMaintenance = canSendToMaintenance(statusFrom(item.status)),
        )

        internal fun userFacingError(
            error: Exception,
            conflictMessage: String,
            validationMessage: String,
            fallback: String,
        ): String {
            if (error is HttpException) {
                return when (error.code()) {
                    400 -> validationMessage
                    403 -> "Você não tem permissão para esta ação."
                    404 -> "Item não encontrado."
                    409 -> conflictMessage
                    else -> error.message?.takeIf { it.isNotBlank() } ?: fallback
                }
            }
            return error.message?.takeIf { it.isNotBlank() } ?: fallback
        }
    }
}
