package com.zera.android.view.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.lists.OptionListItem
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette
import com.zera.android.viewmodel.preferences.ConfigurationsViewModel

@Composable
fun ConfigurationsScreen(
    viewModel: ConfigurationsViewModel = viewModel(),
) {
    val state by viewModel.state
    val accent = ZeraColorFamily.Yellow.palette().base

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            ConfigurationsHeader(
                accent = accent,
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.medium, vertical = Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            TitleText(
                text = "Preferências",
                bold = true,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            OptionListItem(
                itemName = "Aparência",
                description = state.appearanceSummary,
                onClick = viewModel::onAppearanceClick,
                hasIcon = false,
            )
            OptionListItem(
                itemName = "Notificações",
                description = state.notificationsSummary,
                onClick = viewModel::onNotificationsClick,
                hasIcon = false,
            )
            OptionListItem(
                itemName = "Privacidade",
                description = state.privacySummary,
                onClick = viewModel::onPrivacyClick,
                hasIcon = false,
            )

            TitleText(
                text = "Suporte e acesso",
                bold = true,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            OptionListItem(
                itemName = "Acessibilidade",
                description = "",
                onClick = viewModel::onAccessibilityClick,
                hasIcon = false,
            )
            OptionListItem(
                itemName = "Falar com o Zoe",
                description = "",
                onClick = viewModel::onTalkToZoeClick,
                hasIcon = false,
            )
        }
    }
}

/**
 * Cabeçalho da tela: botão "Voltar", título e subtítulo na cor de destaque sobre o fundo primary.
 *
 * Fica local à tela porque a `UpperNavBar` só suporta o branco sobre o fundo primary.
 */
@Composable
private fun ConfigurationsHeader(
    accent: Color,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(Spacing.small),
        verticalArrangement = Arrangement.spacedBy(Spacing.micro),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = onBackClick,
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(Spacing.none),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = accent,
                ),
            ) {
                ZeraIcon(
                    icon = ZeraIcon.LeftUTurn,
                    contentDescription = "Voltar",
                    size = 17.dp,
                    tint = accent,
                )
            }
            BodyText(text = "Voltar", bold = true, color = accent)
        }
        HeadlineText(text = "Configurações", bold = true, color = accent)
        BodyText(
            text = "Personalize sua experiência",
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
@Preview(heightDp = 900)
private fun ConfigurationsScreenPreview() {
    ZeraTheme {
        ConfigurationsScreen()
    }
}
