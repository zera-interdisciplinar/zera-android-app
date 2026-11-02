package com.zera.android.model.usecase.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Ids dos alertas dispensados nesta sessão. As listas observam [ids] para tirar o card na hora,
 * devolvê-lo se o fechamento falhar e ficar em sincronia entre as telas.
 */
object DismissedAlerts {
    private val _ids = MutableStateFlow(emptySet<String>())
    val ids: StateFlow<Set<String>> = _ids.asStateFlow()

    fun add(alertId: String) {
        _ids.update { it + alertId }
    }

    fun remove(alertId: String) {
        _ids.update { it - alertId }
    }
}
