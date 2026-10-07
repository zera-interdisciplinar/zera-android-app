package com.zera.android.viewmodel.employee

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class ManualRegisterState(
    val serialNumber: String = "",
    val itemName: String = "",
    val model: String = "",
    val brand: String = "",
    val condition: String = "",
    val hasDamage: String = "",
    val notes: String = "",
    val modelOptions: List<String> = emptyList(),
    val conditionOptions: List<String> = emptyList(),
    val hasDamageOptions: List<String> = emptyList(),
) {
    /** Só a observação é opcional. */
    val canSubmit: Boolean
        get() = serialNumber.isNotBlank() &&
            itemName.isNotBlank() &&
            model.isNotEmpty() &&
            brand.isNotBlank() &&
            condition.isNotEmpty() &&
            hasDamage.isNotEmpty()
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
            hasDamageOptions = listOf("Sim", "Não"),
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

    fun onBrandChange(value: String) {
        _state.value = _state.value.copy(brand = value)
    }

    fun onConditionChange(value: String) {
        _state.value = _state.value.copy(condition = value)
    }

    fun onHasDamageChange(value: String) {
        _state.value = _state.value.copy(hasDamage = value)
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
