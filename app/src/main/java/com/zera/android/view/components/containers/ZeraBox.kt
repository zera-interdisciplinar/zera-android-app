package com.zera.android.view.components.containers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme

/**
 * Container simples que se destaca do fundo com uma cor sólida.
 *
 * A cor segue a mesma configuração dos botões: o [style] define a família de cor
 * e o [type] decide entre o preenchimento sólido ([ZeraBoxType.Primary]) e o
 * container claro com borda ([ZeraBoxType.Secondary]).
 *
 * A cor de conteúdo correspondente é propagada via `LocalContentColor` — [ZeraIcon]
 * e o `Text` do Material a herdam automaticamente. Os componentes de texto do app
 * (`BodyText`, `HeadlineText`, ...) têm cor própria, então passe
 * `color = LocalContentColor.current` neles quando estiverem sobre um box colorido.
 *
 * @param modifier modificador externo opcional.
 * @param style família de cor do box. Ver [ZeraColorFamily].
 * @param type hierarquia visual dentro do estilo. Ver [ZeraBoxType].
 * @param shape forma do box.
 * @param contentPadding espaçamento interno entre a borda do box e o [content].
 * @param content conteúdo desenhado dentro do box.
 */
@Composable
fun ZeraBox(
    modifier: Modifier = Modifier,
    style: ZeraColorFamily = ZeraColorFamily.Blue,
    type: ZeraBoxType = ZeraBoxType.Primary,
    shape: Shape = RoundedCornerShape(Radius.large),
    contentPadding: PaddingValues = PaddingValues(Spacing.large),
    content: @Composable BoxScope.() -> Unit,
) {
    val (container, contentColor) = boxColorPair(style, type)
    Surface(
        modifier = modifier,
        shape = shape,
        color = container,
        contentColor = contentColor,
        border = boxBorder(style, type),
    ) {
        Box(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ZeraBoxPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            ZeraBox(style = ZeraColorFamily.Blue) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.micro)) {
                        BodyText(
                            text = "Ocupação do Estoque",
                            bold = true,
                            color = LocalContentColor.current,
                        )
                        HeadlineText(
                            text = "300 Itens cadastrados",
                            color = LocalContentColor.current,
                        )
                    }
                    ZeraBox(
                        style = ZeraColorFamily.Yellow,
                        shape = CircleShape,
                        contentPadding = PaddingValues(
                            horizontal = Spacing.large,
                            vertical = Spacing.small,
                        ),
                    ) {
                        BodyText(text = "71%", bold = true, color = LocalContentColor.current)
                    }
                }
            }

            ZeraBox(style = ZeraColorFamily.Green, type = ZeraBoxType.Secondary) {
                BodyText(text = "Secundário com borda", color = LocalContentColor.current)
            }
        }
    }
}
