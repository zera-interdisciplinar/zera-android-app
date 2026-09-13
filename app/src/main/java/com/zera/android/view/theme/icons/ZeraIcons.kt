package com.zera.android.view.theme.icons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Desenha um ícone do catálogo [ZeraIcon].
 *
 * Aplica um tint por cima do drawable, então funciona mesmo com ícones que têm
 * cor fixa no XML. Para manter as cores originais, passe `tint = Color.Unspecified`.
 *
 * ```
 * ZeraIcon(ZeraIcon.Home, contentDescription = null)
 * ZeraIcon(ZeraIcon.Check, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
 * ZeraIcon(ZeraIcon.Gear, contentDescription = "Configurações", size = Spacing.xLarge)
 * ```
 *
 * @param icon ícone do catálogo a exibir.
 * @param contentDescription descrição para acessibilidade. Passe `null` quando o
 *   ícone for puramente decorativo (o significado já está no texto ao lado).
 * @param modifier modificador externo opcional.
 * @param size tamanho (largura e altura). Ícones não quadrados são esticados para
 *   caber; passe `Dp.Unspecified` para respeitar o tamanho intrínseco do drawable.
 * @param tint cor aplicada ao ícone. Ver [ZeraIconDefaults.Tint].
 */
@Composable
fun ZeraIcon(
    icon: ZeraIcon,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = ZeraIconDefaults.Size,
    tint: Color = ZeraIconDefaults.Tint,
) {
    Icon(
        painter = painterResource(icon.resId),
        contentDescription = contentDescription,
        modifier = if (size != Dp.Unspecified) modifier.size(size) else modifier,
        tint = tint,
    )
}

@Preview(showBackground = true)
@Composable
private fun ZeraIconPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                ZeraIcon(ZeraIcon.Home, contentDescription = null)
                ZeraIcon(ZeraIcon.Bell, contentDescription = null)
                ZeraIcon(ZeraIcon.Truck, contentDescription = null)
                ZeraIcon(ZeraIcon.QrCode, contentDescription = null)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                ZeraIcon(
                    ZeraIcon.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                )
                ZeraIcon(
                    ZeraIcon.Exclamation,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
                ZeraIcon(
                    ZeraIcon.Gear,
                    contentDescription = null,
                    size = Spacing.xLarge,
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}
