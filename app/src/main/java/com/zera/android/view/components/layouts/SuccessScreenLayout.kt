package com.zera.android.view.components.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.zeraColors

/**
 * Estrutura comum das telas de sucesso: fundo azul-escuro (`navContainer`), botão de fechar, [SuccessIconCircle],
 * [title], [message], o [content] da tela, os [actions] e o link de voltar para o início.
 * O conteúdo principal e os botões ficam a cargo de cada tela.
 *
 * @param title título exibido abaixo do ícone.
 * @param message texto de apoio exibido abaixo do título.
 * @param onCloseClick chamado ao tocar no X do canto superior direito.
 * @param onBackToHomeClick chamado ao tocar no link de voltar.
 * @param modifier modificador externo opcional.
 * @param backToHomeText texto do link de voltar.
 * @param content conteúdo principal da tela (cards, resumos), entre a mensagem e os botões.
 * @param actions botões de ação, entre o [content] e o link de voltar.
 */
@Composable
fun SuccessScreenLayout(
    title: String,
    message: String,
    onCloseClick: () -> Unit,
    onBackToHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
    backToHomeText: String = "Voltar para o início",
    content: @Composable ColumnScope.() -> Unit = {},
    actions: @Composable ColumnScope.() -> Unit = {},
) {
    // navContainer em vez do primary: o primary do escuro é um azul claro, e os cards
    // (surface) precisam de um fundo mais escuro que eles para se destacar.
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.zeraColors.navContainer,
        contentColor = MaterialTheme.zeraColors.onNavContainer,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                icon = ZeraIcon.Close,
                onClick = onCloseClick,
                contentDescription = "Fechar",
                type = ZeraButtonType.Tertiary,
                style = ZeraColorFamily.Yellow,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(Spacing.medium),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.large)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium, Alignment.CenterVertically),
            ) {
                SuccessIconCircle()
                HeadlineText(
                    text = title,
                    bold = true,
                    color = LocalContentColor.current,
                    textAlign = TextAlign.Center,
                )
                BodyText(
                    text = message,
                    color = LocalContentColor.current,
                    textAlign = TextAlign.Center,
                )
                content()
                actions()
                LabelText(
                    text = backToHomeText,
                    bold = true,
                    color = LocalContentColor.current,
                    onClick = onBackToHomeClick,
                )
            }
        }
    }
}

@Composable
@Preview(heightDp = 900)
private fun SuccessScreenLayoutPreview() {
    ZeraTheme {
        SuccessScreenLayout(
            title = "Tudo certo!",
            message = "A operação foi concluída.",
            onCloseClick = {},
            onBackToHomeClick = {},
            actions = {
                ZeraButton(
                    text = "Continuar",
                    onClick = {},
                    style = ZeraColorFamily.Yellow,
                    fillMaxWidth = true,
                )
            },
        )
    }
}
