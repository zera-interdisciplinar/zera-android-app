package com.zera.android.view.components.texts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Texto em markdown (títulos, negrito, itálico, listas, links, citações e código).
 *
 * Usa a biblioteca `multiplatform-markdown-renderer` com o type scale e as cores do app, para
 * o markdown ficar igual aos outros componentes de texto: parágrafos e listas como [BodyText],
 * `#` como [HeadlineText], `##` como [TitleText] e `###` em diante como [SubtitleText].
 *
 * O conteúdo é desenhado numa `Column` comum (não é lazy), então pode ficar dentro de um
 * container com rolagem vertical.
 *
 * @param text conteúdo em markdown.
 * @param modifier modificador externo opcional. Por padrão ocupa toda a largura.
 * @param color cor do texto e dos links.
 */
@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val typography = MaterialTheme.typography
    val body = typography.bodyLarge
    val subtitle = typography.titleMedium.copy(fontWeight = FontWeight.Bold)

    Markdown(
        content = text,
        modifier = modifier.fillMaxWidth(),
        colors = markdownColor(
            text = color,
            codeText = color,
            codeBackground = MaterialTheme.colorScheme.surfaceVariant,
            dividerColor = MaterialTheme.colorScheme.outlineVariant,
        ),
        typography = markdownTypography(
            h1 = typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            h2 = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            h3 = subtitle,
            h4 = subtitle,
            h5 = subtitle,
            h6 = subtitle,
            text = body,
            paragraph = body,
            ordered = body,
            bullet = body,
            list = body,
            quote = body.copy(fontStyle = FontStyle.Italic),
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun MarkdownTextPreview() {
    ZeraTheme {
        MarkdownText(
            text = """
                # Vidro
                Separe, embale e **identifique** antes de levar ao ponto de coleta.

                ## Como descartar
                1. Lave o recipiente
                2. Embale em jornal ou papelão
                3. Escreva *vidro* na embalagem

                ### O que não pode
                - Espelhos
                - Lâmpadas

                > Nunca descarte no lixo comum.
            """.trimIndent(),
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
