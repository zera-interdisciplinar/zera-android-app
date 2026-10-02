package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class SchedulingSuccessState(
    val recyclerName: String = "",
    val scheduledAt: String = "",
    val materials: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class SchedulingSuccessViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(SchedulingSuccessState())
    val state = _state

    init {
        loadScheduling()
    }

    fun onEmailClick() {
        // TODO: abrir o app de e-mail para contactEmail (exige Context; definir como acionar a partir da tela)
    }

    fun onPhoneClick() {
        // TODO: abrir o discador para contactPhone (exige Context; definir como acionar a partir da tela)
    }

    fun onCloseClick() {
        ZeraNavigator.pushAndPopAll(Route.ManagerHome)
    }

    fun onBackToHomeClick() {
        ZeraNavigator.pushAndPopAll(Route.ManagerHome)
    }

    fun onGenerateReport() {
        // TODO: criar o processo para gerar relatorio
    }

    private fun loadScheduling() {
        // TODO: trocar o mock pelo agendamento confirmado (recicladora, data/horário, materiais e contatos)
        //  — contrato ainda não definido
        _state.value = _state.value.copy(
            recyclerName = "Recicla Tech Moema",
            scheduledAt = "21 de agosto · 08:00",
            materials = "Notebooks e baterias",
            contactEmail = "contato@reciclatech.com.br",
            contactPhone = "(11) 4002-8922",
        )
    }
}
