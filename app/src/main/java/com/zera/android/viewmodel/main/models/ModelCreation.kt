package com.zera.android.viewmodel.main.models

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
import com.zera.android.viewmodel.errorDescription
import com.zera.android.viewmodel.loadErrorDescription
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
    val nameError: String? = null,
    val categoryError: String? = null,
    val materialError: String? = null,
    val brandError: String? = null,
    val notesError: String? = null,
    val isLoading: Boolean = true,
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
        _state.value = _state.value.copy(modelName = value, nameError = null)
    }

    fun onCategoryChange(value: String) {
        _state.value = _state.value.copy(category = value, categoryError = null)
    }

    fun onMaterialChange(value: String) {
        _state.value = _state.value.copy(material = value, materialError = null)
    }

    fun onBrandChange(value: String) {
        _state.value = _state.value.copy(brand = value, brandError = null)
    }

    fun onNotesChange(value: String) {
        _state.value = _state.value.copy(notes = value, notesError = null)
    }

    fun onCreateClick() {
        if (_state.value.isSaving) return
        val form = _state.value
        val categoryId = categories.find { it.name == form.category }?.id
        val materialCode = materials.find { it.name == form.material }?.code
        val errors = fieldErrors(
            name = form.modelName,
            manufacturer = form.brand,
            categoryId = categoryId,
            materialCode = materialCode,
            notes = form.notes,
        )
        if (errors.hasAny) {
            _state.value = form.copy(
                nameError = errors.name,
                categoryError = errors.category,
                materialError = errors.material,
                brandError = errors.manufacturer,
                notesError = errors.notes,
            )
            return
        }
        _state.value = form.copy(
            nameError = null,
            categoryError = null,
            materialError = null,
            brandError = null,
            notesError = null,
            isSaving = true,
            errorMessage = null,
        )
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
                ZeraNavigator.pushAndPop(
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
                    errorMessage = errorDescription(e, "Não conseguimos cadastrar o modelo. Tente novamente."),
                )
            }
        }
    }

    fun onErrorDismiss() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun onCancelClick() {
        ZeraNavigator.goBack()
    }

    private fun loadOptions() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                categories = getCategories.execute()
                materials = getMaterials.execute()
                _state.value = _state.value.copy(
                    categoryOptions = categories.map { it.name },
                    materialOptions = materials.map { it.name },
                    isLoading = false,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = errorDescription(e, loadErrorDescription("as categorias e os materiais")),
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
        ): String? = fieldErrors(name, manufacturer, categoryId, materialCode, notes).first

        internal fun fieldErrors(
            name: String,
            manufacturer: String,
            categoryId: String?,
            materialCode: String?,
            notes: String,
        ) = ModelFieldErrors(
            name = "Informe o nome do modelo.".takeIf { name.isBlank() },
            manufacturer = "Informe a marca.".takeIf { manufacturer.isBlank() },
            category = "Selecione a categoria.".takeIf { categoryId.isNullOrBlank() },
            material = "Selecione o material.".takeIf { materialCode.isNullOrBlank() },
            notes = "A observação pode ter no máximo 500 caracteres.".takeIf { notes.trim().length > 500 },
        )
    }
}

/** Erros de validação do formulário de modelo, um por campo; `null` quando o campo está válido. */
internal data class ModelFieldErrors(
    val name: String? = null,
    val manufacturer: String? = null,
    val category: String? = null,
    val material: String? = null,
    val notes: String? = null,
) {
    val hasAny: Boolean
        get() = first != null

    /** O primeiro erro, na ordem do formulário. */
    val first: String?
        get() = name ?: manufacturer ?: category ?: material ?: notes
}
