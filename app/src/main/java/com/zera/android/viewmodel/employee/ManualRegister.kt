package com.zera.android.viewmodel.employee

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.CreateItemRequestDTO
import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.model.usecase.inventory.CreateItem
import com.zera.android.model.usecase.inventory.GetModels
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.manager.ItemDetailsViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private val DAMAGE_LABELS = listOf(
    "Tela quebrada",
    "Peça faltando",
    "Não liga",
    "Enferrujado",
    "Outro",
)

data class ManualRegisterState(
    val barcode: String = "",
    val serialNumber: String = "",
    val itemName: String = "",
    val model: String = "",
    val selectedModelId: String? = null,
    val manufacturingYear: String = "",
    val acquisitionDate: String = "",
    val condition: String = "",
    val hasDamage: Boolean = false,
    val damageType: String = "",
    val usageIntensity: Int = 1,
    val notes: String = "",
    val modelOptions: List<String> = emptyList(),
    val conditionOptions: List<String> = ItemDetailsViewModel.conditionOptions,
    val damageTypeOptions: List<String> = DAMAGE_LABELS,
    val isLoadingModels: Boolean = false,
    val modelsError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
) {
    /** Só a observação é opcional. Tipo de dano só é exigido quando há dano. */
    val canSubmit: Boolean
        get() = serialNumber.isNotBlank() &&
            itemName.isNotBlank() &&
            selectedModelId != null &&
            manufacturingYear.isNotBlank() &&
            acquisitionDate.isNotBlank() &&
            condition.isNotEmpty() &&
            (!hasDamage || damageType.isNotEmpty())
}

class ManualRegisterViewModel(
    private val initialModel: String? = null,
    initialBarcode: String? = null,
    private val getModels: GetModels = GetModels(),
    private val createItem: CreateItem = CreateItem(),
    private val launchWork: ((suspend () -> Unit) -> Unit)? = null,
) : ZeraViewModel() {
    private val _state = mutableStateOf(
        ManualRegisterState(
            barcode = initialBarcode.orEmpty(),
            model = initialModel.orEmpty(),
            isLoadingModels = true,
        )
    )
    val state = _state

    private var loadedModels: List<ModelResponseDTO> = emptyList()

    init {
        startWork { loadModels() }
    }

    fun onBarcodeChange(value: String) {
        _state.value = _state.value.copy(barcode = value)
    }

    fun onSerialNumberChange(value: String) {
        _state.value = _state.value.copy(serialNumber = value)
    }

    fun onItemNameChange(value: String) {
        _state.value = _state.value.copy(itemName = value)
    }

    fun onModelChange(value: String) {
        _state.value = _state.value.copy(
            model = value,
            selectedModelId = loadedModels.firstOrNull { it.name == value }?.id,
        )
    }

    fun onManufacturingYearChange(value: String) {
        _state.value = _state.value.copy(manufacturingYear = value)
    }

    fun onAcquisitionDateChange(value: String) {
        _state.value = _state.value.copy(acquisitionDate = value)
    }

    fun onConditionChange(value: String) {
        _state.value = _state.value.copy(condition = value)
    }

    fun onHasDamageChange(selected: Boolean) {
        val hasDamage = !selected
        _state.value = _state.value.copy(
            hasDamage = hasDamage,
            damageType = if (hasDamage) _state.value.damageType else "",
        )
    }

    fun onDamageTypeChange(value: String) {
        _state.value = _state.value.copy(damageType = value)
    }

    fun onUsageIntensityChange(value: Int) {
        _state.value = _state.value.copy(usageIntensity = value)
    }

    fun onNotesChange(value: String) {
        _state.value = _state.value.copy(notes = value)
    }

    fun onConfirmClick() {
        val current = _state.value
        if (!current.canSubmit || current.isSubmitting) return
        _state.value = current.copy(isSubmitting = true, submitError = null)
        startWork { submit(_state.value) }
    }

    fun onCancelClick() {
        ZeraNavigator.goBack()
    }

    private suspend fun loadModels() {
        try {
            val page = getModels.execute(page = 0, size = MODELS_PAGE_SIZE)
            loadedModels = page.content
            val names = page.content.mapNotNull { it.name?.takeIf(String::isNotBlank) }.distinct()
            val options = if (initialModel.isNullOrEmpty() || initialModel in names) {
                names
            } else {
                names + initialModel
            }
            _state.value = _state.value.copy(
                modelOptions = options,
                selectedModelId = loadedModels.firstOrNull { it.name == _state.value.model }?.id,
                isLoadingModels = false,
                modelsError = null,
            )
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            loadedModels = emptyList()
            val fallback = listOfNotNull(initialModel?.takeIf { it.isNotEmpty() })
            _state.value = _state.value.copy(
                modelOptions = fallback,
                selectedModelId = null,
                isLoadingModels = false,
                modelsError = error.message ?: "Falha ao carregar modelos",
            )
        }
    }

    private suspend fun submit(current: ManualRegisterState) {
        val modelId = current.selectedModelId ?: return
        val condition = ItemDetailsViewModel.conditionCodeFrom(current.condition)
            ?: return failSubmit("Condição inválida")
        val acquiredAt = toIsoDate(current.acquisitionDate)
            ?: return failSubmit("Data de aquisição inválida")
        val year = current.manufacturingYear.toIntOrNull()
            ?: return failSubmit("Ano de fabricação inválido")
        val damages = if (current.hasDamage) {
            listOf(damageCodeFrom(current.damageType) ?: return failSubmit("Tipo de dano inválido"))
        } else {
            emptyList()
        }

        try {
            val item = createItem.execute(
                CreateItemRequestDTO(
                    name = current.itemName.trim(),
                    modelId = modelId,
                    barcode = current.barcode.trim().takeIf { it.isNotEmpty() },
                    serialNumber = current.serialNumber.trim(),
                    condition = condition,
                    hasDamages = current.hasDamage,
                    damages = damages,
                    notes = current.notes.trim().takeIf { it.isNotEmpty() },
                    acquiredAt = acquiredAt,
                    manufacturingYear = year,
                    usageIntensity = current.usageIntensity,
                ),
            )
            _state.value = _state.value.copy(isSubmitting = false)
            // TODO: tela de conclusão pós-cadastro (padrão ModelCreationSuccessScreen):
            // "Item cadastrado!", card nome/modelo + ID, chip "Em aprovação",
            // CTAs "Registrar outro item" / "Ver itens". Hoje vai direto ao PDI.
            ZeraNavigator.pushAndPop(Route.ItemDetails(itemId = item.id), popCount = 1)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            failSubmit(error.message ?: "Falha ao cadastrar item")
        }
    }

    private fun failSubmit(message: String) {
        _state.value = _state.value.copy(isSubmitting = false, submitError = message)
    }

    private fun startWork(block: suspend () -> Unit) {
        val launcher = launchWork
        if (launcher != null) launcher(block) else viewModelScope.launch { block() }
    }

    companion object {
        internal const val MODELS_PAGE_SIZE = 100

        internal fun toIsoDate(value: String): String? {
            val parts = value.split("/")
            if (parts.size != 3) return null
            val day = parts[0]
            val month = parts[1]
            val year = parts[2]
            if (day.length != 2 || month.length != 2 || year.length != 4) return null
            if (day.toIntOrNull() == null || month.toIntOrNull() == null || year.toIntOrNull() == null) {
                return null
            }
            return "$year-$month-$day"
        }

        internal fun damageCodeFrom(label: String): String? = when (label) {
            "Tela quebrada" -> "BROKEN_SCREEN"
            "Peça faltando" -> "MISSING_PART"
            "Não liga" -> "DOES_NOT_POWER_ON"
            "Enferrujado" -> "OXIDATION"
            "Outro" -> "OTHER"
            else -> null
        }
    }
}
