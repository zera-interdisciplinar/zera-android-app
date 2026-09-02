package com.zera.android.view.components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.theme.ZeraTheme

/**
 * Botão padrão do app.
 *
 * @param text texto exibido dentro do botão.
 * @param onClick ação executada ao tocar no botão.
 * @param modifier modificador externo opcional.
 * @param fillMaxWidth quando `true`, o botão ocupa toda a largura disponível do container.
 * @param style família de cor do botão. Ver [ZeraButtonStyle].
 * @param type hierarquia visual dentro do estilo. Ver [ZeraButtonType].
 * @param enabled habilita ou desabilita a interação.
 */
@Composable
fun ZeraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = false,
    style: ZeraButtonStyle = ZeraButtonStyle.Blue,
    type: ZeraButtonType = ZeraButtonType.Primary,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier),
        enabled = enabled,
        colors = buttonColors(style, type),
    ) {
        Text(text = text)
    }
}

@Preview
@Composable
private fun ZeraButtonPreview() {
    ZeraTheme {
        ZeraButton(
            text = "Continuar",
            onClick = {},
            modifier = Modifier.padding(16.dp),
            style = ZeraButtonStyle.Yellow,
        )
    }
}

@Preview
@Composable
private fun ZeraButtonSecondaryPreview() {
    ZeraTheme {
        ZeraButton(
            text = "Secundário",
            onClick = {},
            fillMaxWidth = true,
            style = ZeraButtonStyle.Yellow,
            type = ZeraButtonType.Secondary,
            modifier = Modifier.padding(16.dp),
        )
    }
}
