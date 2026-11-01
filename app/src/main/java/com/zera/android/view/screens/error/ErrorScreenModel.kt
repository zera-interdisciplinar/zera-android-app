package com.zera.android.view.screens.error

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette

/** Diâmetro do círculo de fundo do ícone. */
private val IconCircleSize = 96.dp

/** Tamanho do ícone dentro do círculo. */
private val IconSize = 40.dp

/**
 * Estrutura comum das telas de erro: fundo `surface` em tela cheia, com o ícone num círculo,
 * o [title] e a [description] centralizados, e os [actions] presos ao rodapé. Os botões e o
 * que cada um faz ficam a cargo de cada tela (ex.: [ConnectionErrorScreen]).
 *
 * @param icon ícone exibido no círculo do topo.
 * @param iconColor família de cor do ícone (base) e do círculo de fundo (container).
 * @param title título exibido abaixo do ícone.
 * @param description texto de apoio exibido abaixo do título.
 * @param modifier modificador externo opcional.
 * @param actions botões de ação, empilhados no rodapé da tela.
 */
@Composable
fun ErrorScreenModel(
    icon: ZeraIcon,
    iconColor: ZeraColorFamily,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actions: @Composable ColumnScope.() -> Unit = {},
) {
    val iconPalette = iconColor.palette()
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium, Alignment.CenterVertically),
            ) {
                Box(
                    modifier = Modifier
                        .size(IconCircleSize)
                        .background(iconPalette.container, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    ZeraIcon(
                        icon = icon,
                        contentDescription = null,
                        size = IconSize,
                        tint = iconPalette.base,
                    )
                }
                TitleText(
                    text = title,
                    bold = true,
                    textAlign = TextAlign.Center,
                )
                BodyText(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                content = actions,
            )
        }
    }
}

@Composable
@Preview(heightDp = 800)
private fun ErrorScreenModelPreview() {
    ZeraTheme {
        ErrorScreenModel(
            icon = ZeraIcon.Exclamation,
            iconColor = ZeraColorFamily.Yellow,
            title = "Algo deu errado",
            description = "Tente novamente em alguns instantes.",
            actions = {
                ZeraButton(
                    text = "Tentar novamente",
                    onClick = {},
                    fillMaxWidth = true,
                )
                LabelText(
                    text = "Voltar",
                    bold = true,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = {},
                )
            },
        )
    }
}
