package com.zera.android.view.components.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

/**
 * Botão circular composto apenas por um ícone, sem texto.
 *
 * Compartilha a paleta de cores e a hierarquia visual do [ZeraButton]: o [style]
 * define a família de cor e o [type] decide entre o preenchimento sólido
 * ([ZeraButtonType.Primary]) e o container claro com borda ([ZeraButtonType.Secondary]).
 *
 * @param icon ícone do catálogo [ZeraIcon] exibido no centro do botão.
 * @param onClick ação executada ao tocar no botão.
 * @param contentDescription descrição para acessibilidade. Passe `null` apenas quando
 *   o botão for puramente decorativo ou redundante com um rótulo próximo.
 * @param modifier modificador externo opcional.
 * @param style família de cor do botão. Ver [ZeraColorFamily].
 * @param type hierarquia visual dentro do estilo. Ver [ZeraButtonType].
 * @param enabled habilita ou desabilita a interação.
 * @param size diâmetro do botão em dp. O ícone ocupa metade desse valor.
 */
@Composable
fun IconButton(
    icon: ZeraIcon,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    style: ZeraColorFamily = ZeraColorFamily.Blue,
    type: ZeraButtonType = ZeraButtonType.Primary,
    enabled: Boolean = true,
    size: Dp = 48.dp,
) {
    val (_, content) = buttonColorPair(style, type)
    Button(
        onClick = onClick,
        modifier = modifier.size(size),
        enabled = enabled,
        colors = buttonColors(style, type),
        border = buttonBorder(style, type),
        shape = CircleShape,
        contentPadding = PaddingValues(Spacing.none),
    ) {
        ZeraIcon(
            icon = icon,
            contentDescription = contentDescription,
            size = size / 2,
            tint = content,
        )
    }
}

@Preview
@Composable
private fun IconButtonPreview() {
    ZeraTheme {
        Row(
            modifier = Modifier.padding(Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            IconButton(
                icon = ZeraIcon.Plus,
                onClick = {},
                contentDescription = "Adicionar",
                style = ZeraColorFamily.Blue,
            )
            IconButton(
                icon = ZeraIcon.Close,
                onClick = {},
                contentDescription = "Fechar",
                style = ZeraColorFamily.Red,
                type = ZeraButtonType.Secondary,
            )
            IconButton(
                icon = ZeraIcon.Bell,
                onClick = {},
                contentDescription = "Notificações",
                type = ZeraButtonType.Secondary,
                style = ZeraColorFamily.Yellow,
            )
        }
    }
}
