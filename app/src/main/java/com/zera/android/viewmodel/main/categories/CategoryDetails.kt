package com.zera.android.viewmodel.main.categories

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.remote.client.isConnectionError
import com.zera.android.model.usecase.auth.GetSelfUser
import com.zera.android.model.usecase.inventory.GetCategory
import com.zera.android.model.usecase.inventory.UpdateCategory
import com.zera.android.view.components.navigation.SideBarItem
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.errorDescription
import com.zera.android.viewmodel.isEmployeeRole
import com.zera.android.viewmodel.loadErrorDescription
import com.zera.android.viewmodel.main.items.ItemDetailsViewModel
import com.zera.android.viewmodel.sideBarItemsFor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class CategoryDetailsState(
    val categoryId: String = "",
    val name: String = "",
    val description: String = "",
    val registeredAt: String = "",
    val canEdit: Boolean = false,
    val sideBarItems: List<SideBarItem> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    /** Rascunho do bottom sheet "Editar categoria"; `null` = sheet fechado. */
    val editDraft: CategoryEditDraft? = null,
)

data class CategoryEditDraft(
    val name: String = "",
    val description: String = "",
    val nameError: String? = null,
    val descriptionError: String? = null,
)

/** Quais campos do rascunho diferem da categoria atual. */
internal data class CategoryEditChanges(val name: Boolean, val description: Boolean) {
    val any: Boolean get() = name || description
}

class CategoryDetailsViewModel : ZeraViewModel() {
    private val getCategory = GetCategory()
    private val getSelfUser = GetSelfUser()
    private val updateCategory = UpdateCategory()

    private val _state = mutableStateOf(CategoryDetailsState())
    val state = _state

    private var isEmployee = false

    fun loadCategory(categoryId: String) {
        if (categoryId.isBlank()) {
            // Sem id não há o que carregar; evita deixar o isLoading inicial preso em true.
            _state.value = _state.value.copy(isLoading = false)
            return
        }
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                runCatching { getSelfUser.execute() }
                    .onSuccess { isEmployee = isEmployeeRole(it.role) }
                _state.value = stateFrom(getCategory.execute(categoryId), isEmployee).copy(isLoading = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = errorDescription(e, loadErrorDescription("a categoria")),
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
            editDraft = CategoryEditDraft(name = current.name, description = current.description),
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

    fun onEditDescriptionChange(value: String) {
        updateDraft { it.copy(description = value, descriptionError = null) }
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
            descriptionError = if (changes.description) descriptionValidationError(draft.description) else null,
        )
        if (validated.nameError != null || validated.descriptionError != null) {
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
                    val updated = updateCategory.rename(current.categoryId, draft.name.trim())
                    _state.value = _state.value.copy(name = updated.name)
                }
                if (changes.description) {
                    val updated = updateCategory.description(current.categoryId, draft.description.trim())
                    _state.value = _state.value.copy(description = updated.description.orEmpty())
                }
                _state.value = _state.value.copy(isSubmitting = false, editDraft = null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessage = writeError(e),
                )
            }
        }
    }

    private fun updateDraft(transform: (CategoryEditDraft) -> CategoryEditDraft) {
        val draft = _state.value.editDraft ?: return
        _state.value = _state.value.copy(editDraft = transform(draft))
    }

    companion object {
        // Escrita em categoria é só do gestor (ms-inventory).
        internal fun canEdit(isEmployee: Boolean): Boolean = !isEmployee

        internal fun editChangesFrom(state: CategoryDetailsState, draft: CategoryEditDraft) = CategoryEditChanges(
            name = draft.name.trim() != state.name.trim(),
            description = draft.description.trim() != state.description.trim(),
        )

        internal fun nameValidationError(value: String): String? =
            if (value.isBlank()) "Informe o nome da categoria." else null

        internal fun descriptionValidationError(value: String): String? =
            if (value.isBlank()) "Informe a descrição da categoria." else null

        /** Descrição do popup para a falha ao editar; `null` quando é falta de conexão. */
        internal fun writeError(error: Exception): String? {
            if (error.isConnectionError()) return null
            if (error is HttpException) {
                return when (error.code()) {
                    400 -> "Revise os dados e tente de novo."
                    403 -> "Você não tem permissão para esta ação."
                    404 -> "Categoria não encontrada."
                    else -> "Não foi possível atualizar a categoria."
                }
            }
            return "Não foi possível atualizar a categoria."
        }

        internal fun stateFrom(category: CategoryResponseDTO, isEmployee: Boolean): CategoryDetailsState =
            CategoryDetailsState(
                categoryId = category.id,
                name = category.name,
                description = category.description.orEmpty(),
                registeredAt = ItemDetailsViewModel.formatRegisteredAt(category.createdAt),
                canEdit = canEdit(isEmployee),
                sideBarItems = sideBarItemsFor(isEmployee),
            )
    }
}
