package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.UpdateItemRequestDTO
import com.zera.android.model.usecase.inventory.ApproveItem
import com.zera.android.model.usecase.inventory.GetItemDetails
import com.zera.android.model.usecase.inventory.RejectItem
import com.zera.android.model.usecase.inventory.UpdateItem
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDateTime
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
    val serialNumber: String = "",
    val notes: String = "",
    val registeredBy: String = "",
    val registeredAt: String = "",
    val canReview: Boolean = false,
    val isLoading: Boolean = false,
    val isMutating: Boolean = false,
    val errorMessage: String? = null,
)

class ItemDetailsViewModel : ZeraViewModel() {
    private val getItemDetails = GetItemDetails()
    private val approveItem = ApproveItem()
    private val rejectItem = RejectItem()
    private val updateItem = UpdateItem()

    private val _state = mutableStateOf(ItemDetailsState())
    val state = _state

    fun loadItem(itemId: String) {
        if (itemId.isBlank()) return
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val item = getItemDetails.execute(itemId)
                _state.value = stateFrom(item).copy(isLoading = false, errorMessage = null)
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

    fun onNameConfirm(name: String) {
        val error = nameValidationError(name)
        if (error != null) {
            _state.value = _state.value.copy(errorMessage = error)
            return
        }
        patchItem(UpdateItemRequestDTO(name = name.trim()))
    }

    fun onConditionConfirm(value: String) {
        val code = conditionCodeFrom(value)
        if (code == null) {
            _state.value = _state.value.copy(
                errorMessage = "Condição inválida. Use Novo, Usado, Semidanificado ou Danificado.",
            )
            return
        }
        patchItem(UpdateItemRequestDTO(condition = code))
    }

    fun onSerialConfirm(value: String) {
        val error = serialValidationError(value)
        if (error != null) {
            _state.value = _state.value.copy(errorMessage = error)
            return
        }
        patchItem(UpdateItemRequestDTO(serialNumber = value.trim()))
    }

    fun onNotesConfirm(value: String) {
        val error = notesValidationError(value)
        if (error != null) {
            _state.value = _state.value.copy(errorMessage = error)
            return
        }
        patchItem(UpdateItemRequestDTO(notes = value.trim()))
    }

    fun onEditConfirm(
        name: String,
        conditionLabel: String,
        serialNumber: String,
        notes: String,
    ) {
        val nameError = nameValidationError(name)
        if (nameError != null) {
            _state.value = _state.value.copy(errorMessage = nameError)
            return
        }
        val conditionCode = conditionCodeFrom(conditionLabel)
        if (conditionCode == null) {
            _state.value = _state.value.copy(
                errorMessage = "Condição inválida. Use Novo, Usado, Semidanificado ou Danificado.",
            )
            return
        }
        val serialError = serialValidationError(serialNumber)
        if (serialError != null) {
            _state.value = _state.value.copy(errorMessage = serialError)
            return
        }
        val notesError = notesValidationError(notes)
        if (notesError != null) {
            _state.value = _state.value.copy(errorMessage = notesError)
            return
        }
        val current = _state.value
        val request = UpdateItemRequestDTO(
            name = name.trim().takeIf { it != current.itemName },
            condition = conditionCode.takeIf { it != current.conditionCode },
            serialNumber = changedOptionalText(current.serialNumber, serialNumber),
            notes = changedOptionalText(current.notes, notes),
        )
        if (
            request.name == null &&
            request.condition == null &&
            request.serialNumber == null &&
            request.notes == null
        ) {
            return
        }
        patchItem(request)
    }

    fun onApproveClick() {
        val itemId = _state.value.itemId
        if (itemId.isBlank() || _state.value.isMutating || !_state.value.canReview) return
        _state.value = _state.value.copy(isMutating = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val item = approveItem.execute(itemId)
                val next = stateFrom(item)
                _state.value = next.copy(isMutating = false, errorMessage = null)
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
                    isMutating = false,
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
        if (itemId.isBlank() || _state.value.isMutating || !_state.value.canReview) return
        val reasonError = reasonValidationError(trimmed)
        if (reasonError != null) {
            _state.value = _state.value.copy(errorMessage = reasonError)
            return
        }
        _state.value = _state.value.copy(isMutating = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val item = rejectItem.execute(itemId, trimmed)
                _state.value = stateFrom(item).copy(isMutating = false, errorMessage = null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isMutating = false,
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

    private fun patchItem(request: UpdateItemRequestDTO) {
        val itemId = _state.value.itemId
        if (itemId.isBlank() || _state.value.isMutating) return
        _state.value = _state.value.copy(isMutating = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val item = updateItem.execute(itemId, request)
                _state.value = stateFrom(item).copy(isMutating = false, errorMessage = null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isMutating = false,
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
        internal const val SERIAL_MAX_LENGTH = 120
        internal const val NOTES_MAX_LENGTH = 500
        internal const val REASON_MAX_LENGTH = 500
        internal val conditionOptions = listOf("Novo", "Usado", "Semidanificado", "Danificado")

        internal fun statusFrom(value: String?): ItemStatus? = ItemStatus.fromBackend(value)

        internal fun conditionLabel(value: String?): String = when (value) {
            "NEW" -> "Novo"
            "USED" -> "Usado"
            "SEMI_DAMAGED" -> "Semidanificado"
            "DAMAGED" -> "Danificado"
            else -> value.orEmpty()
        }

        internal fun canReview(status: ItemStatus?): Boolean =
            status == ItemStatus.PendingApproval || status == ItemStatus.AwaitingEvaluation

        internal fun serialValidationError(value: String): String? {
            val trimmed = value.trim()
            return if (trimmed.length > SERIAL_MAX_LENGTH) {
                "O número de série deve ter no máximo $SERIAL_MAX_LENGTH caracteres."
            } else {
                null
            }
        }

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

        internal fun formatRegisteredAt(iso: String?): String {
            if (iso.isNullOrBlank()) return ""
            return try {
                val dateTime = LocalDateTime.parse(iso)
                val month = dateTime.month
                    .getDisplayName(TextStyle.SHORT, ptBr)
                    .replace(".", "")
                    .replaceFirstChar { it.uppercase() }
                    .take(3)
                val minute = dateTime.minute.toString().padStart(2, '0')
                "${dateTime.dayOfMonth} de $month ${dateTime.year} - ${dateTime.hour}h$minute"
            } catch (_: Exception) {
                iso
            }
        }

        internal fun stateFrom(item: ItemResponseDTO): ItemDetailsState = ItemDetailsState(
            itemId = item.id,
            itemName = item.name,
            itemSubtitle = item.model?.name.orEmpty(),
            status = statusFrom(item.status),
            category = item.model?.category?.name.orEmpty(),
            material = materialsLabel(item.model?.materials.orEmpty().map { it.name }),
            condition = conditionLabel(item.condition),
            conditionCode = item.condition,
            serialNumber = item.serialNumber.orEmpty(),
            notes = item.notes.orEmpty(),
            registeredBy = item.createdByName.orEmpty(),
            registeredAt = formatRegisteredAt(item.createdAt),
            canReview = canReview(statusFrom(item.status)),
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
