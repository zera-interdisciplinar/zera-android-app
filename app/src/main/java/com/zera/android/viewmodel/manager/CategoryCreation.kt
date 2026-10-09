package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.usecase.inventory.CreateCategory
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class CategoryCreationState(
    val name: String = "",
    val description: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

class CategoryCreationViewModel : ZeraViewModel() {
    private val createCategory = CreateCategory()

    private val _state = mutableStateOf(CategoryCreationState())
    val state = _state

    fun onNameChange(value: String) {
        _state.value = _state.value.copy(name = value, errorMessage = null)
    }

    fun onDescriptionChange(value: String) {
        _state.value = _state.value.copy(description = value, errorMessage = null)
    }

    fun onCreateClick() {
        if (_state.value.isSaving) return
        val form = _state.value
        val error = validationError(form.name)
        if (error != null) {
            _state.value = form.copy(errorMessage = error)
            return
        }
        _state.value = form.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val created = createCategory.execute(
                    name = form.name.trim(),
                    description = form.description.trim().takeIf { it.isNotEmpty() },
                )
                ZeraNavigator.push(
                    Route.CategoryCreationSuccess(
                        name = created.name.ifBlank { form.name.trim() },
                        description = created.description.orEmpty(),
                    ),
                )
                _state.value = _state.value.copy(isSaving = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Não foi possível cadastrar a categoria.",
                )
            }
        }
    }

    fun onCancelClick() {
        ZeraNavigator.goBack()
    }

    companion object {
        internal fun validationError(name: String): String? = when {
            name.isBlank() -> "Informe o nome da categoria."
            else -> null
        }
    }
}
