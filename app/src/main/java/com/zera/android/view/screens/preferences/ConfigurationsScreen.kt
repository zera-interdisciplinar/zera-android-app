package com.zera.android.view.screens.preferences

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.lists.OptionListItem
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.overlays.BottomSheet
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.DarkBlue15
import com.zera.android.view.theme.PastelYellow90
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.White95
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.zeraColors
import com.zera.android.viewmodel.preferences.AppThemeOption
import com.zera.android.viewmodel.preferences.ConfigurationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationsScreen(
    viewModel: ConfigurationsViewModel = viewModel(),
) {
    val state by viewModel.state

    Scaffold(
        containerColor = MaterialTheme.zeraColors.navContainer,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                UpperNavBar(
                    title = "Configurações",
                    goBack = true,
                    showActions = false,
                    backgroundVariant = true,
                    variantContentColor = MaterialTheme.zeraColors.onNavContainer,
                    onBackClick = viewModel::onBackClick,
                    modifier = Modifier.padding(horizontal = Spacing.small),
                )
                BodyText(
                    text = "Personalize sua experiência",
                    color = MaterialTheme.zeraColors.onNavContainer,
                    modifier = Modifier.padding(horizontal = Spacing.large),
                )
            }
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
                color = MaterialTheme.zeraColors.onNavContainer,
            )
            OptionListItem(
                itemName = "Aparência",
                description = state.appliedTheme.summary,
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
                color = MaterialTheme.zeraColors.onNavContainer,
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

    if (state.isAppearanceSheetVisible) {
        BottomSheet(
            onDismissRequest = viewModel::onAppearanceSheetDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            TitleText(text = "Escolher aparência", bold = true, color = MaterialTheme.colorScheme.onSurface)
            ThemeOptionCard(
                name = AppThemeOption.WarmLight.label,
                description = AppThemeOption.WarmLight.description,
                selected = state.selectedTheme == AppThemeOption.WarmLight,
                containerColor = PastelYellow90,
                contentColor = DarkBlue15,
                onClick = { viewModel.onThemeSelected(AppThemeOption.WarmLight) },
            )
            ThemeOptionCard(
                name = AppThemeOption.Dark.label,
                description = AppThemeOption.Dark.description,
                selected = state.selectedTheme == AppThemeOption.Dark,
                containerColor = DarkBlue15,
                contentColor = White95,
                onClick = { viewModel.onThemeSelected(AppThemeOption.Dark) },
            )
            ZeraButton(
                text = "Aplicar tema",
                onClick = viewModel::onApplyThemeClick,
                fillMaxWidth = true,
            )
        }
    }
}

/**
 * Card de uma opção de tema no bottom sheet de aparência: pinta o fundo com a cor do
 * próprio tema, como uma pré-visualização, e destaca a opção [selected] com uma borda.
 *
 * Fica local à tela porque as cores vêm do tema que está sendo mostrado, não do tema atual.
 */
@Composable
private fun ThemeOptionCard(
    name: String,
    description: String,
    selected: Boolean,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .semantics { role = Role.RadioButton },
        shape = RoundedCornerShape(Radius.large),
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.micro),
        ) {
            BodyText(text = name, bold = true, color = contentColor)
            CaptionText(text = description, color = contentColor)
        }
    }
}

@Composable
@Preview(heightDp = 900)
private fun ConfigurationsScreenPreview() {
    ZeraTheme {
        ConfigurationsScreen()
    }
}
