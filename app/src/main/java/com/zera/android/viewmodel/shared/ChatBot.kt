package com.zera.android.viewmodel.shared

import androidx.compose.runtime.mutableStateOf
import com.zera.android.model.entity.chat.Message
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class ChatBotState(
    val messages: List<Message> = emptyList(),
    val input: String = "",
    val isLoadingMessage: Boolean = false,
    val suggestions: List<String> = listOf(
        "Como cadastrar um produto?",
        "Falar com o suporte",
    ),
)

class ChatBotViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(ChatBotState())
    val state = _state

    fun onInputChange(newValue: String) {
        _state.value = _state.value.copy(input = newValue)
    }

    fun onSend() {
        // TODO: adicionar a mensagem do usuário, limpar o campo e enviar ao chatbot (AI Core API),
        //  exibindo o loading até a resposta chegar.
    }

    fun onSuggestionClick(suggestion: String) {
        // TODO: enviar a sugestão como mensagem do usuário (mesmo fluxo de onSend).
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }
}
