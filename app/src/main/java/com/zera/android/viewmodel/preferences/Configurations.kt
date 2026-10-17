package com.zera.android.viewmodel.preferences

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.ThemeManager
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel

enum class AppThemeOption(
    val label: String,
    val description: String,
    val summary: String,
) {
    WarmLight("Claro quente", "Maior conforto à luz do dia", "Usar tema claro"),
    Dark("Escuro", "Maior conforto à noite", "Usar tema escuro"),
}

data class ConfigurationsState(
    val appliedTheme: AppThemeOption = AppThemeOption.WarmLight,
    val selectedTheme: AppThemeOption = AppThemeOption.WarmLight,
    val isAppearanceSheetVisible: Boolean = false,
    val notificationsSummary: String = "Alertas importantes ativados",
    val registrationItemsSummary: String = "6 campos visíveis",
    val privacySummary: String = "Sessão e permissões",
)

class ConfigurationsViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(
        ConfigurationsState(
            appliedTheme = ThemeManager.current,
            selectedTheme = ThemeManager.current,
        ),
    )
    val state = _state

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onAppearanceClick() {
        _state.value = _state.value.copy(
            selectedTheme = _state.value.appliedTheme,
            isAppearanceSheetVisible = true,
        )
    }

    fun onAppearanceSheetDismiss() {
        _state.value = _state.value.copy(isAppearanceSheetVisible = false)
    }

    fun onThemeSelected(theme: AppThemeOption) {
        _state.value = _state.value.copy(selectedTheme = theme)
    }

    fun onApplyThemeClick() {
        val theme = _state.value.selectedTheme
        ThemeManager.apply(theme)
        _state.value = _state.value.copy(
            appliedTheme = theme,
            isAppearanceSheetVisible = false,
        )
    }

    fun onNotificationsClick() {
        // TODO: navegar para as configurações de notificações
    }

    fun onAccessibilityClick() {
        ZeraNavigator.push(Route.Acessibility, ScreenAnimation.SlideHorizontal)
    }

    fun onTalkToZoeClick() {
        // TODO: navegar para a conversa com o Zoe
    }
}
