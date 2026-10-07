package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class ModelCreationState(
    val modelName: String = "",
    val material: String = "",
    val brand: String = "",
    val notes: String = "",
//    val materialOptions: List<String> = emptyList(),
    val materialOptions: List<String> = listOf<String>("Plastico", "Ferro", "Aço", "Seila") //TODO: Este é um placeholder, devemos puxar a lista de materiais disponiveis do back, ou definir.
)

class ModelCreationViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(ModelCreationState())
    val state = _state


    fun onModelNameChange(value: String) {
        _state.value = _state.value.copy(modelName = value)
    }

    fun onMaterialChange(value: String) {
        _state.value = _state.value.copy(material = value)
    }

    fun onMaterialOptionsChange(options: List<String>) {
        _state.value = _state.value.copy(materialOptions = options)
    }

    fun onBrandChange(value: String) {
        _state.value = _state.value.copy(brand = value)
    }

    fun onNotesChange(value: String) {
        _state.value = _state.value.copy(notes = value)
    }

    fun onCreateClick() {
        val form = _state.value
        ZeraNavigator.push(
            Route.ModelCreationSuccess(
                modelName = form.modelName,
                material = form.material,
                brand = form.brand,
                notes = form.notes,
            ),
        )
    }

    fun onCancelClick() {
        ZeraNavigator.goBack()
    }
}
