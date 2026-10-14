package com.zera.android.viewmodel.preferences

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class ConfigurationsState(
    val appearanceSummary: String = "Usar tema claro",
    val notificationsSummary: String = "Alertas importantes ativados",
    val registrationItemsSummary: String = "6 campos visíveis",
    val privacySummary: String = "Sessão e permissões",
)

class ConfigurationsViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(ConfigurationsState())
    val state = _state

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onAppearanceClick() {
        // TODO: navegar para as configurações de aparência
    }

    fun onNotificationsClick() {
        // TODO: navegar para as configurações de notificações
    }

    fun onPrivacyClick() {
        // TODO: navegar para as configurações de privacidade
    }

    fun onAccessibilityClick() {
        // TODO: navegar para as configurações de acessibilidade
    }

    fun onTalkToZoeClick() {
        // TODO: navegar para a conversa com o Zoe
    }
}
