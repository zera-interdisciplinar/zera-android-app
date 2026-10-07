package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.viewmodel.ZeraViewModel

data class ScheduledDisposalsState(
    val nextDisposalDate: String = "",
    val nextDisposalRecyclerName: String = "",
    val disposals: List<OptionItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    /** `true` quando há um próximo descarte para destacar no card azul. */
    val hasNextDisposal: Boolean
        get() = nextDisposalDate.isNotEmpty()
}

class ScheduledDisposalsViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(ScheduledDisposalsState())
    val state = _state

    init {
        loadDisposals()
    }

    fun onScheduleNextClick() {
        // TODO: definir para onde leva o agendamento de um novo descarte
    }

    fun onDisposalClick(disposal: OptionItem) {
        // TODO: navegar para os detalhes do descarte agendado
    }

    private fun loadDisposals() {
        // TODO: trocar o mock pelos descartes agendados (próximo descarte e lista) — contrato ainda não definido
        _state.value = _state.value.copy(
            nextDisposalDate = "21 ago · 08:00",
            nextDisposalRecyclerName = "Recicla Tech Moema",
            disposals = List(5) {
                OptionItem(
                    name = "Rua Emanuel Gonsalvez  21 set",
                    description = "Às 13h - 14 itens",
                )
            },
        )
    }
}
