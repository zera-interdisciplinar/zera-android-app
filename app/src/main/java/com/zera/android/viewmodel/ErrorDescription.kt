package com.zera.android.viewmodel

import com.zera.android.model.remote.client.isConnectionError

/** Descrição padrão para uma carga que falhou, completando "não conseguimos carregar ...". */
internal fun loadErrorDescription(what: String): String =
    "Houve uma instabilidade e não conseguimos carregar $what."

/**
 * Descrição do popup de erro (`ErrorFeedbackPopup`) para [error], ou `null` quando é falta de
 * conexão: nesse caso a tela cheia de conexão já aparece e a tela não mostra popup.
 *
 * [description] é o texto para o usuário. Nunca repassar `e.message`, que pode trazer código
 * HTTP ou detalhe técnico (ver spec `telas-de-erro.md`).
 */
internal fun errorDescription(error: Throwable, description: String): String? =
    if (error.isConnectionError()) null else description
