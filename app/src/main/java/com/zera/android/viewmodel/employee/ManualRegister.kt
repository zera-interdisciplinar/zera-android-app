package com.zera.android.viewmodel.employee

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class ManualRegisterState(
    val serialNumber: String = "",
    val itemName: String = "",
    val model: String = "",
    val manufacturingYear: String = "",
    val acquisitionDate: String = "",
    val condition: String = "",
    val hasDamage: Boolean = false,
    val damageType: String = "",
    val usageIntensity: Int = 1,
    val notes: String = "",
    val modelOptions: List<String> = emptyList(),
    val conditionOptions: List<String> = emptyList(),
    val damageTypeOptions: List<String> = emptyList(),
) {
    /** Só a observação é opcional. Tipo de dano só é exigido quando há dano. */
    val canSubmit: Boolean
        get() = serialNumber.isNotBlank() &&
            itemName.isNotBlank() &&
            model.isNotEmpty() &&
            manufacturingYear.isNotBlank() &&
            acquisitionDate.isNotBlank() &&
            condition.isNotEmpty() &&
            (!hasDamage || damageType.isNotEmpty())
}

/**
 * ViewModel da tela de cadastro manual de item (sem etiqueta) do operário.
 *
 * As opções dos dropdowns ainda são fixas (mock) até o back expor modelos e condições.
 */
class ManualRegisterViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(
        ManualRegisterState(
            // TODO: puxar os modelos cadastrados do back
            modelOptions = listOf("Teclado mecânico", "Mouse sem fio", "Chip controlador", "Placa de circuito"),
            conditionOptions = listOf("Novo", "Usado", "Danificado"),
            damageTypeOptions = listOf("Tela quebrada", "Peça faltando", "Não liga", "Enferrujado", "Outro"),
        )
    )
    val state = _state

    fun onSerialNumberChange(value: String) {
        _state.value = _state.value.copy(serialNumber = value)
    }

    fun onItemNameChange(value: String) {
        _state.value = _state.value.copy(itemName = value)
    }

    fun onModelChange(value: String) {
        _state.value = _state.value.copy(model = value)
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
        // TODO: enviar o cadastro ao back quando o endpoint existir
        ZeraNavigator.goBack()
    }

    fun onCancelClick() {
        ZeraNavigator.goBack()
    }
}
