package com.zera.android.view.components.texts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zera.android.view.theme.ZeraTheme

/**
 * Padronização de texto do app.
 *
 * Cada componente abaixo fixa um estilo do [MaterialTheme.typography] (e, quando faz
 * sentido, uma cor semântica). Use-os no lugar do `Text` cru para manter a hierarquia
 * tipográfica consistente entre telas.
 *
 * Parâmetros comuns:
 * - [modifier]: modificador externo.
 * - [color]: sobrescreve a cor. `Color.Unspecified` (padrão) herda a cor do estilo/contexto.
 * - [textAlign]: alinhamento horizontal.
 * - [maxLines] / [overflow]: controle de truncamento.
 * - [bold]: quando `true`, força `FontWeight.Bold`; quando `false`, mantém o peso do estilo.
 * - [italic]: quando `true`, aplica itálico.
 */

/** Título grande de tela (ex.: "Bem-vindo"). Mapeia para `headlineLarge`. */
@Composable
fun HeadlineText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    bold: Boolean = false,
    italic: Boolean = false,
) = BaseText(
    text = text,
    style = MaterialTheme.typography.headlineLarge,
    modifier = modifier,
    color = color,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
)

/** Título de seção ou card. Mapeia para `titleLarge`. */
@Composable
fun TitleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    bold: Boolean = false,
    italic: Boolean = false,
) = BaseText(
    text = text,
    style = MaterialTheme.typography.titleLarge,
    modifier = modifier,
    color = color,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
)

/** Subtítulo que complementa um [TitleText]. Mapeia para `titleMedium` + `onSurfaceVariant`. */
@Composable
fun SubtitleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    bold: Boolean = false,
    italic: Boolean = false,
) = BaseText(
    text = text,
    style = MaterialTheme.typography.titleMedium,
    modifier = modifier,
    color = color,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
)

/** Texto corrido / parágrafos. Mapeia para `bodyLarge`. */
@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    bold: Boolean = false,
    italic: Boolean = false,
) = BaseText(
    text = text,
    style = MaterialTheme.typography.bodyLarge,
    modifier = modifier,
    color = color,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
)

/** Rótulo de campo, botão ou chip. Mapeia para `labelLarge`. */
@Composable
fun LabelText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    bold: Boolean = false,
    italic: Boolean = false,
) = BaseText(
    text = text,
    style = MaterialTheme.typography.labelLarge,
    modifier = modifier,
    color = color,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
)

/** Texto pequeno auxiliar (timestamps, legendas de imagem). Mapeia para `bodySmall` + `onSurfaceVariant`. */
@Composable
fun CaptionText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    bold: Boolean = false,
    italic: Boolean = false,
) = BaseText(
    text = text,
    style = MaterialTheme.typography.bodySmall,
    modifier = modifier,
    color = color,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
)

/**
 * Texto pequeno em caixa alta acima de um título (padrão Material clássico "overline").
 * Mapeia para `labelSmall` + espaçamento entre letras; o texto é convertido para maiúsculas.
 */
@Composable
fun OverlineText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign: TextAlign? = null,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    bold: Boolean = false,
    italic: Boolean = false,
) = BaseText(
    text = text.uppercase(),
    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
    modifier = modifier,
    color = color,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
)

/** Base compartilhada: um `Text` com estilo fixo e os parâmetros comuns encaminhados. */
@Composable
private fun BaseText(
    text: String,
    style: TextStyle,
    modifier: Modifier,
    color: Color,
    textAlign: TextAlign?,
    maxLines: Int,
    overflow: TextOverflow,
    bold: Boolean,
    italic: Boolean,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
        // `null` = não sobrescreve; mantém o valor definido em `style`.
        fontWeight = if (bold) FontWeight.Bold else null,
        fontStyle = if (italic) FontStyle.Italic else null,
        style = style,
    )
}

@Preview(showBackground = true)
@Composable
private fun TextsPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OverlineText("Overline")
            HeadlineText("Headline")
            TitleText("Title")
            SubtitleText("Subtitle")
            BodyText("Body normal")
            BodyText("Body em negrito", bold = true)
            BodyText("Body em itálico", italic = true)
            LabelText("Label")
            CaptionText("Caption")
        }
    }
}
