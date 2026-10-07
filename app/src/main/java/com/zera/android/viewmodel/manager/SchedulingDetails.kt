package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.viewmodel.ZeraViewModel

data class SchedulingDetailsState(
    val recyclerName: String = "",
    val scheduledAt: String = "",
    val options: List<OptionItem> = emptyList(),
    val contactEmail: String = "",
    val contactPhone: String = "",
    val contactButtonsEnabled: Boolean = true,
    val errorMessage: String? = null,
)

class SchedulingDetailsViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(SchedulingDetailsState())
    val state = _state

    init {
        loadScheduling()
    }

    fun onGenerateReportClick() {
        // TODO: criar o processo para gerar relatório
    }

    fun onOptionClick(option: OptionItem) {
        // TODO: navegar para alterar data/horário ou editar produtos do agendamento
    }

    fun onEmailClick() {
        // TODO: abrir o app de e-mail para contactEmail (exige Context)
    }

    fun onPhoneClick() {
        // TODO: abrir o discador para contactPhone (exige Context)
    }

    fun onCancelSchedulingClick() {
        // TODO: confirmar e cancelar o agendamento — contrato ainda não definido
    }

    private fun loadScheduling() {
        // TODO: trocar o mock pelo agendamento real — contrato ainda não definido
        _state.value = _state.value.copy(
            recyclerName = "Recicla Tech Moema",
            scheduledAt = "21 ago · 08:00",
            options = listOf(
                OptionItem(name = "Alterar data ou horário", description = ""),
                OptionItem(name = "Adicionar ou remover produtos", description = ""),
            ),
            contactEmail = "contato@reciclatech.com.br",
            contactPhone = "(11) 4002-8922",
        )
    }
}
