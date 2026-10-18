package com.zera.android.viewmodel.preferences

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

enum class AcessibilityTextSize(val label: String) {
    Small("Pequeno"),
    Default("Padrão"),
    Large("Grande"),
    ExtraLarge("Muito grande"),
}

data class AcessibilityState(
    val textSize: AcessibilityTextSize = AcessibilityTextSize.Default,
    val highContrast: Boolean = false,
    val reduceAnimations: Boolean = true,
    val voiceReading: Boolean = false,
    val confirmBeforeDelete: Boolean = true,
)

class AcessibilityViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(AcessibilityState())
    val state = _state

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onDecreaseTextSizeClick() {
        val sizes = AcessibilityTextSize.entries
        val previous = sizes.getOrNull(_state.value.textSize.ordinal - 1) ?: return
        _state.value = _state.value.copy(textSize = previous)
    }

    fun onIncreaseTextSizeClick() {
        val sizes = AcessibilityTextSize.entries
        val next = sizes.getOrNull(_state.value.textSize.ordinal + 1) ?: return
        _state.value = _state.value.copy(textSize = next)
    }

    fun onHighContrastChange(enabled: Boolean) {
        _state.value = _state.value.copy(highContrast = enabled)
    }

    fun onReduceAnimationsChange(enabled: Boolean) {
        _state.value = _state.value.copy(reduceAnimations = enabled)
    }

    fun onVoiceReadingChange(enabled: Boolean) {
        _state.value = _state.value.copy(voiceReading = enabled)
    }

    fun onConfirmBeforeDeleteChange(enabled: Boolean) {
        _state.value = _state.value.copy(confirmBeforeDelete = enabled)
    }

    fun onSaveClick() {
        // TODO: persistir as preferências de acessibilidade
    }
}
