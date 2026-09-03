package com.zera.android.view.components.texts

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

@Composable
fun HeadlineText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    alpha: Float = 1f,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    onClick: (() -> Unit)? = null,
) = BaseText(
    text = text,
    style = MaterialTheme.typography.headlineLarge,
    modifier = modifier,
    color = color,
    alpha = alpha,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
    underline = underline,
    onClick = onClick,
)

/** Título de seção ou card. Mapeia para `titleLarge`. */
@Composable
fun TitleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    alpha: Float = 1f,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    onClick: (() -> Unit)? = null,
) = BaseText(
    text = text,
    style = MaterialTheme.typography.titleLarge,
    modifier = modifier,
    color = color,
    alpha = alpha,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
    underline = underline,
    onClick = onClick,
)

/** Subtítulo que complementa um [TitleText]. Mapeia para `titleMedium` + `onSurfaceVariant`. */
@Composable
fun SubtitleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    alpha: Float = 1f,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    onClick: (() -> Unit)? = null,
) = BaseText(
    text = text,
    style = MaterialTheme.typography.titleMedium,
    modifier = modifier,
    color = color,
    alpha = alpha,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
    underline = underline,
    onClick = onClick,
)

/** Texto corrido / parágrafos. Mapeia para `bodyLarge`. */
@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    alpha: Float = 1f,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    onClick: (() -> Unit)? = null,
) = BaseText(
    text = text,
    style = MaterialTheme.typography.bodyLarge,
    modifier = modifier,
    color = color,
    alpha = alpha,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
    underline = underline,
    onClick = onClick,
)

/** Rótulo de campo, botão ou chip. Mapeia para `labelLarge`. */
@Composable
fun LabelText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    alpha: Float = 1f,
    textAlign: TextAlign? = null,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    onClick: (() -> Unit)? = null,
) = BaseText(
    text = text,
    style = MaterialTheme.typography.labelLarge,
    modifier = modifier,
    color = color,
    alpha = alpha,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
    underline = underline,
    onClick = onClick,
)

/** Texto pequeno auxiliar (timestamps, legendas de imagem). Mapeia para `bodySmall` + `onSurfaceVariant`. */
@Composable
fun CaptionText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    alpha: Float = 1f,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    onClick: (() -> Unit)? = null,
) = BaseText(
    text = text,
    style = MaterialTheme.typography.bodySmall,
    modifier = modifier,
    color = color,
    alpha = alpha,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
    underline = underline,
    onClick = onClick,
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
    alpha: Float = 1f,
    textAlign: TextAlign? = null,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    onClick: (() -> Unit)? = null,
) = BaseText(
    text = text.uppercase(),
    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
    modifier = modifier,
    color = color,
    alpha = alpha,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    bold = bold,
    italic = italic,
    underline = underline,
    onClick = onClick,
)

/** Base compartilhada: um `Text` com estilo fixo e os parâmetros comuns encaminhados. */
@Composable
private fun BaseText(
    text: String,
    style: TextStyle,
    modifier: Modifier,
    color: Color,
    alpha: Float,
    textAlign: TextAlign?,
    maxLines: Int,
    overflow: TextOverflow,
    bold: Boolean,
    italic: Boolean,
    underline: Boolean,
    onClick: (() -> Unit)?,
) {
    Text(
        text = text,
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
        color = color.copy(alpha = color.alpha * alpha),
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
        fontWeight = if (bold) FontWeight.Bold else null,
        fontStyle = if (italic) FontStyle.Italic else null,
        textDecoration = if (underline) TextDecoration.Underline else null,
        style = style,
    )
}

@Preview(showBackground = true)
@Composable
private fun TextsPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
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
            BodyText("Body com 50% de opacidade", alpha = 0.5f)
            LabelText("Label clicável", onClick = {})
            BodyText("Body sublinhado", underline = true)
        }
    }
}
