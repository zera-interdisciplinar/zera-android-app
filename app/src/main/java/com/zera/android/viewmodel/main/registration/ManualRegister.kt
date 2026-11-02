package com.zera.android.viewmodel.main.registration

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.CreateItemRequestDTO
import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.model.usecase.inventory.CreateItem
import com.zera.android.model.usecase.inventory.GetModels
import com.zera.android.model.usecase.inventory.SubmitItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.appMessageOrFallback
import com.zera.android.viewmodel.errorDescription
import com.zera.android.viewmodel.loadErrorDescription
import com.zera.android.viewmodel.main.items.ItemDetailsViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

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
    val damageTypeOptions: List<String> = ItemDetailsViewModel.damageOptions,
    val isLoading: Boolean = false,
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
    private val submitItem: SubmitItem = SubmitItem(),
    private val launchWork: ((suspend () -> Unit) -> Unit)? = null,
) : ZeraViewModel() {
    private val _state = mutableStateOf(
        ManualRegisterState(
            barcode = initialBarcode.orEmpty(),
            model = initialModel.orEmpty(),
            isLoading = true,
        )
    )
    val state = _state

    private var loadedModels: List<ModelResponseDTO> = emptyList()
    private var refreshModelsOnResume = false

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

    fun onAddModelClick() {
        refreshModelsOnResume = true
        ZeraNavigator.push(Route.ModelCreation)
    }

    /** Chamado quando a tela volta a ficar visível; só recarrega após ir criar um modelo. */
    fun onResume() {
        if (!refreshModelsOnResume) return
        refreshModelsOnResume = false
        startWork { refreshModels() }
    }

    fun onConfirmClick() {
        val current = _state.value
        if (!current.canSubmit || current.isSubmitting) return
        _state.value = current.copy(isSubmitting = true, submitError = null)
        startWork { submit(_state.value) }
    }

    fun onErrorDismiss() {
        _state.value = _state.value.copy(submitError = null, modelsError = null)
    }

    fun onCancelClick() {
        ZeraNavigator.goBack()
    }

    private suspend fun loadModels() {
        try {
            applyModels(getModels.execute(page = 0, size = MODELS_PAGE_SIZE).content)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            loadedModels = emptyList()
            val fallback = listOfNotNull(initialModel?.takeIf { it.isNotEmpty() })
            _state.value = _state.value.copy(
                modelOptions = fallback,
                selectedModelId = null,
                isLoading = false,
                modelsError = errorDescription(error, loadErrorDescription("os modelos")),
            )
        }
    }

    private fun applyModels(models: List<ModelResponseDTO>) {
        loadedModels = models
        val names = models.mapNotNull { it.name?.takeIf(String::isNotBlank) }.distinct()
        val options = if (initialModel.isNullOrEmpty() || initialModel in names) {
            names
        } else {
            names + initialModel
        }
        _state.value = _state.value.copy(
            modelOptions = options,
            selectedModelId = loadedModels.firstOrNull { it.name == _state.value.model }?.id,
            isLoading = false,
            modelsError = null,
        )
    }

    /**
     * Recarrega os modelos ao voltar da criação de um modelo. Diferente de [loadModels], uma
     * falha aqui mantém a lista e a seleção atuais em vez de limpá-las.
     */
    private suspend fun refreshModels() {
        try {
            applyModels(getModels.execute(page = 0, size = MODELS_PAGE_SIZE).content)
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            // Mantém a lista atual: o usuário pode tentar de novo ao voltar à tela.
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
            listOf(
                ItemDetailsViewModel.damageCodeFrom(current.damageType)
                    ?: return failSubmit("Tipo de dano inválido"),
            )
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
            val submitted = submitItem.execute(item.id)
            _state.value = _state.value.copy(isSubmitting = false)
            // TODO: tela de conclusão pós-cadastro (padrão ModelCreationSuccessScreen):
            // "Item cadastrado!", card nome/modelo + ID, chip "Em aprovação",
            // CTAs "Registrar outro item" / "Ver itens". Hoje vai direto ao PDI.
            ZeraNavigator.pushAndPop(Route.ItemDetails(itemId = submitted.id), popCount = 1)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            failSubmit(appMessageOrFallback(error, "Não conseguimos cadastrar o item. Tente novamente."))
        }
    }

    /** [message] é `null` quando a falha foi de conexão: a tela cheia de conexão já aparece. */
    private fun failSubmit(message: String?) {
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
    }
}
