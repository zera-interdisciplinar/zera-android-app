package com.zera.android.view.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.inputs.SelectSwitchOption
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.preferences.AcessibilityViewModel

@Composable
fun AcessibilityScreen(
    viewModel: AcessibilityViewModel = viewModel(),
) {
    val state by viewModel.state

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Acessibilidade",
                goBack = true,
                showActions = false,
                onBackClick = viewModel::onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.small),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Spacing.medium, vertical = Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                BodyText(
                    text = "Ajuste leitura e interação",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                TitleText(text = "Visual", bold = true, color = MaterialTheme.colorScheme.onSurface)
                TextSizeOption(
                    label = state.textSize.label,
                    onDecreaseClick = viewModel::onDecreaseTextSizeClick,
                    onIncreaseClick = viewModel::onIncreaseTextSizeClick,
                )
                SelectSwitchOption(
                    name = "Alto contraste",
                    description = "Melhora a separação de elementos",
                    checked = state.highContrast,
                    onCheckedChange = viewModel::onHighContrastChange,
                )
                SelectSwitchOption(
                    name = "Reduzir animações",
                    description = "Evita movimentos desnecessários",
                    checked = state.reduceAnimations,
                    onCheckedChange = viewModel::onReduceAnimationsChange,
                )

                TitleText(text = "Interação", bold = true, color = MaterialTheme.colorScheme.onSurface)
                SelectSwitchOption(
                    name = "Leitura por voz",
                    description = "Ouvir conteúdos importantes",
                    checked = state.voiceReading,
                    onCheckedChange = viewModel::onVoiceReadingChange,
                )
                SelectSwitchOption(
                    name = "Confirmação antes de excluir",
                    description = "Reduz ações acidentais",
                    checked = state.confirmBeforeDelete,
                    onCheckedChange = viewModel::onConfirmBeforeDeleteChange,
                )
            }

            ZeraButton(
                text = "Salvar preferências",
                onClick = viewModel::onSaveClick,
                fillMaxWidth = true,
            )
        }
    }
}

/**
 * Card "Tamanho do texto": nome e tamanho atual à esquerda; atalhos "A−" e "A+" à direita.
 *
 * Fica local à tela porque nenhum outro componente tem controles personalizados no
 * lado direito do card.
 */
@Composable
private fun TextSizeOption(
    label: String,
    onDecreaseClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
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
                BodyText(text = "Tamanho do texto", bold = true)
                CaptionText(text = label)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                BodyText(text = "A−", bold = true, onClick = onDecreaseClick)
                BodyText(text = "A+", bold = true, onClick = onIncreaseClick)
            }
        }
    }
}

@Composable
@Preview(heightDp = 900)
private fun AcessibilityScreenPreview() {
    ZeraTheme {
        AcessibilityScreen()
    }
}
