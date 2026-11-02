package com.zera.android.view.transition

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zera.android.model.local.SqliteManager

/**
 * Preferência "Reduzir animações". [enabled] é um estado do Compose: o `ZeraNavHost` lê dele
 * a cada transição de tela, então a mudança vale já na próxima navegação.
 *
 * A escolha é gravada no [SqliteManager] e recarregada por [load] ao abrir o app.
 */
object ReduceAnimationsManager {
    var enabled by mutableStateOf(false)
        private set

    /** Lê a preferência salva. Precisa ser chamado depois de `SqliteManager.init`. */
    fun load() {
        enabled = SqliteManager.getReduceAnimations()
    }

    fun apply(enabled: Boolean) {
        this.enabled = enabled
        SqliteManager.saveReduceAnimations(enabled)
    }
}
