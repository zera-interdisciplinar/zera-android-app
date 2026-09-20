package com.zera.android.view.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

/**
 * Card clicável de atalho: um rótulo curto no topo, um dado em destaque logo abaixo
 * e uma seta de "avançar" à direita.
 *
 * O [value] é exibido como texto puro — a formatação (número, unidade, "25", "R$ 12,00")
 * fica a cargo de quem chama.
 *
 * @param label texto pequeno exibido acima do dado.
 * @param value dado em destaque, já formatado como string.
 * @param onClick ação executada ao tocar no card.
 * @param modifier modificador externo opcional.
 * @param labelIcon ícone opcional exibido ao lado do [label].
 * @param enabled habilita ou desabilita a interação.
 */
@Composable
fun ShortcutCard(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    labelIcon: ZeraIcon? = null,
    enabled: Boolean = true,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(Radius.large),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = Modifier.padding(Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.micro),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.micro),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabelText(text = label)
                    if (labelIcon != null) {
                        ZeraIcon(
                            icon = labelIcon,
                            contentDescription = null,
                            size = Spacing.medium,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                HeadlineText(text = value, bold = true)
            }
            ZeraIcon(
                icon = ZeraIcon.ProceedArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShortcutCardPreview() {
    ZeraTheme {
        Row(
            modifier = Modifier.padding(Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            ShortcutCard(
                label = "Manutenção",
                value = "25",
                onClick = {},
                labelIcon = ZeraIcon.Wrench,
                modifier = Modifier.width(150.dp),
            )
            ShortcutCard(
                label = "Coletas hoje",
                value = "R$ 128,00",
                onClick = {},
            )
        }
    }
}
