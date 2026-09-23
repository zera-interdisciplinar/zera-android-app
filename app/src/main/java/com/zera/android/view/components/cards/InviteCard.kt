package com.zera.android.view.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.containers.ZeraBoxType
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme

/**
 * Card de convite pendente: código do convite, nome do operador convidado e o
 * prazo de expiração, com uma ação para copiar o código.
 *
 * @param code código do convite.
 * @param name nome do operador convidado.
 * @param expireTime horas restantes até o convite expirar.
 * @param modifier modificador externo opcional.
 * @param onCopyCodeClick ação executada ao tocar em "Copiar código".
 */
@Composable
fun InviteCard(
    code: String,
    name: String,
    expireTime: Int,
    modifier: Modifier = Modifier,
    onCopyCodeClick: () -> Unit = {},
) {
    ZeraBox(
        modifier = modifier.fillMaxWidth(),
        style = ZeraColorFamily.Yellow,
        type = ZeraBoxType.Secondary,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.micro)) {
            CaptionText(
                text = "Código $code",
                bold = true,
                color = LocalContentColor.current,
            )
            BodyText(
                text = name,
                bold = true,
                color = LocalContentColor.current,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.micro)) {
                CaptionText(
                    text = "Expira em ${expireTime}h ·",
                    color = LocalContentColor.current,
                )
                LabelText(
                    text = "Copiar código",
                    bold = true,
                    color = LocalContentColor.current,
                    onClick = onCopyCodeClick,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InviteCardPreview() {
    ZeraTheme {
        InviteCard(
            code = "120443",
            name = "Operadora Carol the Best",
            expireTime = 23,
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
