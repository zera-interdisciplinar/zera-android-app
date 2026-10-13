package com.zera.android.model.entity.chat

import com.zera.android.view.navigation.Route
import java.time.LocalDateTime

/**
 * Mensagem individual do chat com o assistente virtual.
 *
 * @param role quem enviou a mensagem. Ver [MessageRole].
 * @param content texto completo da mensagem.
 * @param sentAt momento do envio.
 * @param navigateTo rota opcional do atalho `redirect_to`.
 * @param navigateToLabel texto do atalho quando [navigateTo] não é nulo.
 */
data class Message(
    val role: MessageRole,
    val content: String,
    val sentAt: LocalDateTime,
    val navigateTo: Route? = null,
    val navigateToLabel: String = "",
)
