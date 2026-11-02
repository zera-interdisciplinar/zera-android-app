package com.zera.android.viewmodel.main.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.remote.client.isConnectionError
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.model.dto.inventory.PagedItemsDTO
import com.zera.android.model.usecase.auth.GetSelfUser
import com.zera.android.model.usecase.inventory.ApproveItem
import com.zera.android.model.usecase.inventory.DeleteModel
import com.zera.android.model.usecase.inventory.GetModel
import com.zera.android.model.usecase.inventory.GetModelItems
import com.zera.android.model.usecase.inventory.RejectItem
import com.zera.android.model.usecase.inventory.UpdateModel
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.view.components.navigation.SideBarItem
import com.zera.android.viewmodel.isEmployeeRole
import com.zera.android.viewmodel.sideBarItemsFor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class ModelDetailsState(
    val modelId: String = "",
    val name: String = "",
    val manufacturer: String = "",
    val category: String = "",
    val material: String = "",
    val notes: String = "",
    val rejectionReason: String = "",
    val status: ItemStatus? = null,
    val canReview: Boolean = false,
    val canEdit: Boolean = false,
    val canDelete: Boolean = false,
    /** `true` enquanto o popup "Excluir esse modelo?" está aberto. */
    val showDeleteConfirmation: Boolean = false,
    val reviewHint: String = "",
    val sideBarItems: List<SideBarItem> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    /** Rascunho do bottom sheet "Editar modelo"; `null` = sheet fechado. */
    val editDraft: ModelEditDraft? = null,
)

data class ModelEditDraft(
    val name: String = "",
    val manufacturer: String = "",
    val nameError: String? = null,
    val manufacturerError: String? = null,
)

/** Quais campos do rascunho diferem do modelo atual. */
internal data class ModelEditChanges(val name: Boolean, val manufacturer: Boolean) {
    val any: Boolean get() = name || manufacturer
}

class ModelDetailsViewModel : ZeraViewModel() {
    private val getModel = GetModel()
    private val getModelItems = GetModelItems()
    private val getSelfUser = GetSelfUser()
    private val updateModel = UpdateModel()
    private val deleteModel = DeleteModel()
    private val approveItem = ApproveItem()
    private val rejectItem = RejectItem()

    private val _state = mutableStateOf(ModelDetailsState())
    val state = _state

    private var isEmployee = false
    private var pendingItemId: String? = null

    fun loadModel(modelId: String) {
        if (modelId.isBlank()) {
            // Sem id não há o que carregar; evita deixar o isLoading inicial preso em true.
            _state.value = _state.value.copy(isLoading = false)
            return
        }
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                runCatching { getSelfUser.execute() }
                    .onSuccess { isEmployee = isEmployeeRole(it.role) }
                applyLoadedModel(getModel.execute(modelId), isLoading = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = userFacingError(
                        e,
                        conflictMessage = "Este modelo não pode ser carregado agora.",
                        validationMessage = "Não foi possível carregar o modelo.",
                        fallback = "Não foi possível carregar o modelo.",
                    ),
                )
            }
        }
    }

    fun onErrorDismiss() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onEditClick() {
        val current = _state.value
        if (!current.canEdit) return
        _state.value = current.copy(
            editDraft = ModelEditDraft(name = current.name, manufacturer = current.manufacturer),
            errorMessage = null,
        )
    }

    fun onEditDismiss() {
        if (_state.value.isSubmitting) return
        _state.value = _state.value.copy(editDraft = null)
    }

    fun onEditNameChange(value: String) {
        updateDraft { it.copy(name = value, nameError = null) }
    }

    fun onEditManufacturerChange(value: String) {
        updateDraft { it.copy(manufacturer = value, manufacturerError = null) }
    }

    /**
     * Valida o rascunho e grava só o que mudou. Cada campo é um endpoint: se um falhar, o anterior
     * já fica salvo no state e o sheet continua aberto, e um novo "Salvar" reenvia apenas o que falta.
     */
    fun onEditSave() {
        val current = _state.value
        val draft = current.editDraft ?: return
        if (current.isSubmitting) return

        val changes = editChangesFrom(current, draft)
        val validated = draft.copy(
            nameError = if (changes.name) nameValidationError(draft.name) else null,
            manufacturerError = if (changes.manufacturer) manufacturerValidationError(draft.manufacturer) else null,
        )
        if (validated.nameError != null || validated.manufacturerError != null) {
            _state.value = current.copy(editDraft = validated)
            return
        }
        if (!changes.any) {
            _state.value = current.copy(editDraft = null)
            return
        }

        _state.value = current.copy(isSubmitting = true, errorMessage = null, editDraft = validated)
        viewModelScope.launch {
            try {
                if (changes.name) {
                    val updated = updateModel.rename(current.modelId, draft.name.trim())
                    _state.value = _state.value.copy(name = updated.name.orEmpty())
                }
                if (changes.manufacturer) {
                    val updated = updateModel.manufacturer(current.modelId, draft.manufacturer.trim())
                    _state.value = _state.value.copy(manufacturer = updated.manufacturer.orEmpty())
                }
                _state.value = _state.value.copy(isSubmitting = false, editDraft = null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessage = userFacingError(
                        e,
                        conflictMessage = "O status atual do modelo não permite esta ação.",
                        validationMessage = "Revise os dados e tente de novo.",
                        fallback = "Não foi possível atualizar o modelo.",
                    ),
                )
            }
        }
    }

    fun onDeleteClick() {
        if (!_state.value.canDelete) return
        _state.value = _state.value.copy(showDeleteConfirmation = true)
    }

    fun onDeleteDismiss() {
        _state.value = _state.value.copy(showDeleteConfirmation = false)
    }

    /** A lista de modelos recarrega ao voltar a ficar visível. */
    fun onDeleteConfirm() {
        val modelId = _state.value.modelId
        if (modelId.isBlank() || _state.value.isSubmitting || !_state.value.canDelete) return
        _state.value = _state.value.copy(
            showDeleteConfirmation = false,
            isSubmitting = true,
            errorMessage = null,
        )
        viewModelScope.launch {
            try {
                deleteModel.execute(modelId)
                ZeraNavigator.goBack()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessage = userFacingError(
                        e,
                        conflictMessage = "Este modelo tem itens vinculados e não pode ser excluído.",
                        validationMessage = "Não foi possível excluir o modelo.",
                        fallback = "Não foi possível excluir o modelo.",
                    ),
                )
            }
        }
    }

    private fun updateDraft(transform: (ModelEditDraft) -> ModelEditDraft) {
        val draft = _state.value.editDraft ?: return
        _state.value = _state.value.copy(editDraft = transform(draft))
    }

    fun onApproveClick() {
        if (!_state.value.canReview || _state.value.isSubmitting) return
        mutateViaItem { itemId -> approveItem.execute(itemId) }
    }

    fun onRejectConfirm(reason: String) {
        val error = reasonValidationError(reason)
        if (error != null) {
            _state.value = _state.value.copy(errorMessage = error)
            return
        }
        if (!_state.value.canReview || _state.value.isSubmitting) return
        mutateViaItem { itemId -> rejectItem.execute(itemId, reason.trim()) }
    }

    private suspend fun applyLoadedModel(model: ModelResponseDTO, isLoading: Boolean) {
        val items = runCatching { getModelItems.execute(model.id.orEmpty()) }
            .getOrDefault(PagedItemsDTO())
        pendingItemId = reviewableItemId(items.content)
        _state.value = stateFrom(model, isEmployee, pendingItemId)
            .copy(isLoading = isLoading, isSubmitting = false, errorMessage = null)
    }

    private suspend fun resolvePendingItemId(modelId: String): String? {
        if (!pendingItemId.isNullOrBlank()) return pendingItemId
        val items = getModelItems.execute(modelId)
        pendingItemId = reviewableItemId(items.content)
        return pendingItemId
    }

    private fun mutateViaItem(block: suspend (String) -> ItemResponseDTO) {
        val modelId = _state.value.modelId
        if (modelId.isBlank() || _state.value.isSubmitting) return
        _state.value = _state.value.copy(isSubmitting = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val itemId = resolvePendingItemId(modelId)
                if (itemId.isNullOrBlank()) {
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        errorMessage = NO_PENDING_ITEM_HINT,
                    )
                    return@launch
                }
                block(itemId)
                applyLoadedModel(getModel.execute(modelId), isLoading = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessage = userFacingError(
                        e,
                        conflictMessage = "O status atual não permite esta ação.",
                        validationMessage = "Revise os dados e tente de novo.",
                        fallback = "Não foi possível atualizar o modelo.",
                    ),
                )
            }
        }
    }

    companion object {
        internal const val NAME_MAX_LENGTH = 120
        internal const val REASON_MAX_LENGTH = 500
        internal const val NO_PENDING_ITEM_HINT =
            "A aprovação do modelo acontece ao aprovar o item vinculado. Não há item pendente neste modelo."

        internal fun canReview(
            isEmployee: Boolean,
            status: ItemStatus?,
            @Suppress("UNUSED_PARAMETER") pendingItemId: String? = null,
        ): Boolean = !isEmployee && status == ItemStatus.PendingApproval

        internal fun canEdit(isEmployee: Boolean): Boolean = !isEmployee

        internal fun canDelete(isEmployee: Boolean): Boolean = !isEmployee

        internal fun editChangesFrom(state: ModelDetailsState, draft: ModelEditDraft) = ModelEditChanges(
            name = draft.name.trim() != state.name.trim(),
            manufacturer = draft.manufacturer.trim() != state.manufacturer.trim(),
        )

        internal fun reviewHint(
            @Suppress("UNUSED_PARAMETER") isEmployee: Boolean,
            @Suppress("UNUSED_PARAMETER") status: ItemStatus?,
            @Suppress("UNUSED_PARAMETER") pendingItemId: String?,
        ): String = ""

        internal fun reviewableItemId(items: List<ItemResponseDTO>): String? {
            val reviewable = setOf(ItemStatus.PendingApproval, ItemStatus.AwaitingEvaluation)
            return items.firstOrNull { item ->
                ItemStatus.fromBackend(item.status) in reviewable
            }?.id
        }

        internal fun nameValidationError(value: String): String? {
            val trimmed = value.trim()
            return when {
                trimmed.isEmpty() -> "Informe o nome do modelo."
                trimmed.length > NAME_MAX_LENGTH -> "O nome deve ter no máximo $NAME_MAX_LENGTH caracteres."
                else -> null
            }
        }

        internal fun manufacturerValidationError(value: String): String? {
            val trimmed = value.trim()
            return if (trimmed.isEmpty()) "Informe a marca." else null
        }

        internal fun reasonValidationError(value: String): String? {
            val trimmed = value.trim()
            return when {
                trimmed.isEmpty() -> "Informe o motivo da recusa."
                trimmed.length > REASON_MAX_LENGTH -> "O motivo deve ter no máximo $REASON_MAX_LENGTH caracteres."
                else -> null
            }
        }

        internal fun materialsLabel(names: List<String>): String = when {
            names.isEmpty() -> ""
            names.size == 1 -> names.first()
            names.size == 2 -> "${names[0]} e ${names[1]}"
            else -> names.dropLast(1).joinToString(", ") + " e " + names.last()
        }

        /** Descrição do popup para a falha de uma ação no modelo; `null` quando é falta de conexão. */
        internal fun userFacingError(
            error: Exception,
            conflictMessage: String,
            validationMessage: String,
            fallback: String,
        ): String? {
            if (error.isConnectionError()) return null
            if (error is HttpException) {
                return when (error.code()) {
                    400 -> validationMessage
                    403 -> "Você não tem permissão para esta ação."
                    404 -> "Modelo ou item não encontrado."
                    409 -> conflictMessage
                    else -> fallback
                }
            }
            return fallback
        }

        internal fun stateFrom(
            model: ModelResponseDTO,
            isEmployee: Boolean,
            pendingItemId: String? = null,
        ): ModelDetailsState {
            val status = ItemStatus.fromBackend(model.approvalStatus)
            return ModelDetailsState(
                modelId = model.id.orEmpty(),
                name = model.name.orEmpty(),
                manufacturer = model.manufacturer.orEmpty(),
                category = model.category?.name.orEmpty(),
                material = materialsLabel(
                    model.materials.map { it.name.ifBlank { it.code.orEmpty() } }.filter { it.isNotBlank() },
                ),
                notes = model.notes.orEmpty(),
                rejectionReason = model.rejectionReason.orEmpty(),
                status = status,
                canReview = canReview(isEmployee, status, pendingItemId),
                canEdit = canEdit(isEmployee),
                canDelete = canDelete(isEmployee),
                reviewHint = reviewHint(isEmployee, status, pendingItemId),
                sideBarItems = sideBarItemsFor(isEmployee),
            )
        }
    }
}
