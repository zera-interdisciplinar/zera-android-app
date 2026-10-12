package com.zera.android.model.entity.chat

import java.time.LocalDateTime

/**
 * Mensagem individual do chat com o assistente virtual.
 *
 * @param role quem enviou a mensagem. Ver [MessageRole].
 * @param content texto completo da mensagem.
 * @param sentAt momento do envio.
 */
data class Message(
    val role: MessageRole,
    val content: String,
    val sentAt: LocalDateTime,
)
