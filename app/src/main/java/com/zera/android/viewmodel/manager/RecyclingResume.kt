package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.components.cards.OpeningHoursItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel

data class RecyclingResumeState(
    val recyclerName: String = "",
    val acceptedMaterials: String = "",
    val address: String = "",
    val distance: String = "",
    val isOpen: Boolean? = null,
    val description: String = "",
    val openingHours: List<OpeningHoursItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    /** Endereço e distância na mesma linha (ex.: "Av. Pavão, 620 · 2,8 km"); omite a parte vazia. */
    val addressLabel: String
        get() = listOf(address, distance).filter { it.isNotEmpty() }.joinToString(" · ")
}

class RecyclingResumeViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(RecyclingResumeState())
    val state = _state

    init {
        loadResume()
    }

    fun onConfirmClick() {
        // TODO: confirmar o descarte no backend antes de navegar (contrato ainda não definido)
        // pushAndPop tira o resumo da pilha: voltar da tela de sucesso não deve reabrir a confirmação.
        ZeraNavigator.pushAndPop(Route.SchedulingSuccess, animation = ScreenAnimation.SlideHorizontal)
    }

    private fun loadResume() {
        // TODO: trocar o mock pela recicladora (nome, materiais, endereço, distância, aberto agora,
        //  sobre o local e horários) e pelos itens selecionados — contrato ainda não definido
        _state.value = _state.value.copy(
            recyclerName = "Recicla Tech Moema",
            acceptedMaterials = "Eletrônicos e baterias",
            address = "Av. Pavão, 620",
            distance = "2,8 km",
            isOpen = true,
            description = "Recebe computadores, periféricos, pilhas, baterias e pequenos eletrodomésticos.",
            openingHours = listOf(
                OpeningHoursItem(days = "Seg–Sex", hours = "08h–18h"),
                OpeningHoursItem(days = "Sábado", hours = "09h–14h"),
            ),
        )
    }
}
