package com.zera.android.view.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zera.android.model.local.SqliteManager
import com.zera.android.viewmodel.preferences.AppThemeOption

/**
 * Tema escolhido pelo usuário. [current] é um estado do Compose: ler dele dentro de uma
 * composição (como no `MainActivity`) recompõe o app inteiro quando o tema muda.
 *
 * A escolha é gravada no [SqliteManager] e recarregada por [load] ao abrir o app.
 */
object ThemeManager {
    var current by mutableStateOf(AppThemeOption.WarmLight)
        private set

    /** Lê o tema salvo. Precisa ser chamado depois de `SqliteManager.init`. */
    fun load() {
        val saved = SqliteManager.getTheme()
        current = AppThemeOption.entries.firstOrNull { it.name == saved } ?: AppThemeOption.WarmLight
    }

    fun apply(theme: AppThemeOption) {
        current = theme
        SqliteManager.saveTheme(theme.name)
    }
}
