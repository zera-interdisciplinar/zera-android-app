package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.dto.inventory.MaterialCatalogDTO
import com.zera.android.model.usecase.inventory.CreateModel
import com.zera.android.model.usecase.inventory.GetCategories
import com.zera.android.model.usecase.inventory.GetMaterials
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class ModelCreationState(
    val modelName: String = "",
    val category: String = "",
    val categoryOptions: List<String> = emptyList(),
    val material: String = "",
    val materialOptions: List<String> = emptyList(),
    val brand: String = "",
    val notes: String = "",
    val isLoadingOptions: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

class ModelCreationViewModel : ZeraViewModel() {
    private val getCategories = GetCategories()
    private val getMaterials = GetMaterials()
    private val createModel = CreateModel()

    private val _state = mutableStateOf(ModelCreationState())
    val state = _state

    private var categories: List<CategoryResponseDTO> = emptyList()
    private var materials: List<MaterialCatalogDTO> = emptyList()

    init {
        loadOptions()
    }

    fun onModelNameChange(value: String) {
        _state.value = _state.value.copy(modelName = value, errorMessage = null)
    }

    fun onCategoryChange(value: String) {
        _state.value = _state.value.copy(category = value, errorMessage = null)
    }

    fun onMaterialChange(value: String) {
        _state.value = _state.value.copy(material = value, errorMessage = null)
    }

    fun onBrandChange(value: String) {
        _state.value = _state.value.copy(brand = value, errorMessage = null)
    }

    fun onNotesChange(value: String) {
        _state.value = _state.value.copy(notes = value, errorMessage = null)
    }

    fun onCreateClick() {
        if (_state.value.isSaving) return
        val form = _state.value
        val categoryId = categories.find { it.name == form.category }?.id
        val materialCode = materials.find { it.name == form.material }?.code
        val error = validationError(
            name = form.modelName,
            manufacturer = form.brand,
            categoryId = categoryId,
            materialCode = materialCode,
            notes = form.notes,
        )
        if (error != null) {
            _state.value = form.copy(errorMessage = error)
            return
        }
        _state.value = form.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val created = createModel.execute(
                    name = form.modelName.trim(),
                    manufacturer = form.brand.trim(),
                    categoryId = categoryId.orEmpty(),
                    materialCodes = listOf(materialCode.orEmpty()),
                    notes = form.notes.trim().takeIf { it.isNotEmpty() },
                )
                val materialLabel = created.materials
                    .map { it.name.ifBlank { it.code.orEmpty() } }
                    .filter { it.isNotBlank() }
                    .joinToString(", ")
                    .ifBlank { form.material }
                ZeraNavigator.push(
                    Route.ModelCreationSuccess(
                        modelName = created.name?.takeIf { it.isNotBlank() } ?: form.modelName.trim(),
                        material = materialLabel,
                        brand = created.manufacturer?.takeIf { it.isNotBlank() } ?: form.brand.trim(),
                        notes = created.notes.orEmpty(),
                    ),
                )
                _state.value = _state.value.copy(isSaving = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Não foi possível cadastrar o modelo.",
                )
            }
        }
    }

    fun onCancelClick() {
        ZeraNavigator.goBack()
    }

    private fun loadOptions() {
        _state.value = _state.value.copy(isLoadingOptions = true, errorMessage = null)
        viewModelScope.launch {
            try {
                categories = getCategories.execute()
                materials = getMaterials.execute()
                _state.value = _state.value.copy(
                    categoryOptions = categories.map { it.name },
                    materialOptions = materials.map { it.name },
                    isLoadingOptions = false,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoadingOptions = false,
                    errorMessage = e.message ?: "Não foi possível carregar categoria e material.",
                )
            }
        }
    }

    companion object {
        internal fun validationError(
            name: String,
            manufacturer: String,
            categoryId: String?,
            materialCode: String?,
            notes: String,
        ): String? = when {
            name.isBlank() -> "Informe o nome do modelo."
            manufacturer.isBlank() -> "Informe a marca."
            categoryId.isNullOrBlank() -> "Selecione a categoria."
            materialCode.isNullOrBlank() -> "Selecione o material."
            notes.trim().length > 500 -> "A observação pode ter no máximo 500 caracteres."
            else -> null
        }
    }
}
