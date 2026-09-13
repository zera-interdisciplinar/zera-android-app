package com.zera.android.view.components.outros

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette

/**
 * Tag de status: um texto curto dentro de uma pílula colorida.
 *
 * Usa sempre o par (container, conteúdo) — mais claro — de uma [ZeraColorFamily],
 * independente de contexto; não tem noção de "primário/secundário" como
 * [com.zera.android.view.components.buttons.ZeraButton] ou
 * [com.zera.android.view.components.containers.ZeraBox].
 *
 * @param text texto curto exibido dentro da tag.
 * @param modifier modificador externo opcional.
 * @param style família de cor da tag. Ver [ZeraColorFamily].
 * @param contentPadding espaçamento interno entre a borda da pílula e o [text].
 */
@Composable
fun Tag(
    text: String,
    modifier: Modifier = Modifier,
    style: ZeraColorFamily = ZeraColorFamily.Yellow,
    contentPadding: PaddingValues = PaddingValues(horizontal = Spacing.medium, vertical = Spacing.small),
) {
    val palette = style.palette()
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = palette.container,
        contentColor = palette.onContainer,
    ) {
        LabelText(
            text = text,
            bold = true,
            color = palette.onContainer,
            modifier = Modifier.padding(contentPadding),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TagPreview() {
    ZeraTheme {
        ZeraBox(style = ZeraColorFamily.Blue) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                Tag(text = "Em aprovação", style = ZeraColorFamily.Yellow)
                Tag(text = "Aprovado", style = ZeraColorFamily.Green)
                Tag(text = "Rejeitado", style = ZeraColorFamily.Red)
                Tag(text = "Rascunho", style = ZeraColorFamily.Blue)
            }
        }
    }
}
