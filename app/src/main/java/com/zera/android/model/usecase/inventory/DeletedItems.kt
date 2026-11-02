package com.zera.android.model.usecase.inventory

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Ids dos itens excluídos nesta sessão. As telas que ficam na pilha enquanto o detalhe está aberto
 * observam [ids] para recarregar, já que não buscam os dados de novo ao voltar a ficar visíveis.
 */
object DeletedItems {
    private val _ids = MutableStateFlow(emptySet<String>())
    val ids: StateFlow<Set<String>> = _ids.asStateFlow()

    fun add(itemId: String) {
        _ids.update { it + itemId }
    }
}
