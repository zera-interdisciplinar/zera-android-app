package com.zera.android.view.components.inputs

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

/**
 * Tipos de entrada suportados pelo [ZeraTextInput].
 *
 * Cada tipo define o teclado exibido, a capitalização automática e se o
 * conteúdo deve ser mascarado (senha).
 */
enum class ZeraInputType {
    Text,
    Email,
    Password,
    Phone,
    Number,
    Decimal;
    val isSecret: Boolean
        get() = this == Password
    fun keyboardOptions(imeAction: ImeAction = ImeAction.Default): KeyboardOptions {
        val keyboardType = when (this) {
            Text -> KeyboardType.Text
            Email -> KeyboardType.Email
            Password -> KeyboardType.Password
            Phone -> KeyboardType.Phone
            Number -> KeyboardType.Number
            Decimal -> KeyboardType.Decimal
        }
        val capitalization = when (this) {
            Text -> KeyboardCapitalization.Sentences
            else -> KeyboardCapitalization.None
        }
        return KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = capitalization,
            imeAction = imeAction,
        )
    }
}
