package com.zera.android.view.screens.manager.recycling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.cards.Notification
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.lists.OptionList
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.OverlineText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.manager.SchedulingDetailsViewModel

@Composable
fun SchedulingDetailsScreen(
    viewModel: SchedulingDetailsViewModel = viewModel(),
) {
    val state by viewModel.state

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Agendamento",
                goBack = true,
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
                .padding(horizontal = Spacing.medium, vertical = Spacing.small)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            ZeraBox(
                modifier = Modifier.fillMaxWidth(),
                style = ZeraColorFamily.Blue,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    OverlineText(
                        text = "Descarte",
                        color = LocalContentColor.current,
                        bold = true,
                    )
                    TitleText(
                        text = state.scheduledAt,
                        bold = true,
                        color = LocalContentColor.current,
                    )
                    LabelText(
                        text = state.recyclerName,
                        color = LocalContentColor.current,
                    )
                }
            }

            ZeraButton(
                text = "Gerar relatório",
                onClick = viewModel::onGenerateReportClick,
                fillMaxWidth = true,
            )

            OptionList(
                options = state.options,
                onItemClick = viewModel::onOptionClick,
                hasIcon = false,
                contentPadding = PaddingValues(Spacing.none),
                modifier = Modifier.heightIn(max = 400.dp),
            )

            TitleText(
                text = "Contato com a Recicladora",
                bold = true,
                color = MaterialTheme.colorScheme.primary,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                ZeraButton(
                    text = "E-mail",
                    onClick = viewModel::onEmailClick,
                    enabled = state.contactButtonsEnabled,
                    style = ZeraColorFamily.Yellow,
                    fillMaxWidth = true,
                    modifier = Modifier.weight(1f),
                )
                ZeraButton(
                    text = "Telefone",
                    onClick = viewModel::onPhoneClick,
                    enabled = state.contactButtonsEnabled,
                    style = ZeraColorFamily.Green,
                    fillMaxWidth = true,
                    modifier = Modifier.weight(1f),
                )
            }

            Notification(
                label = "Precisa cancelar?",
                text = "Avise o local para liberar o horário.",
                style = ZeraColorFamily.Yellow,
                modifier = Modifier.fillMaxWidth(),
            )

            ZeraButton(
                text = "Cancelar agendamento",
                onClick = viewModel::onCancelSchedulingClick,
                style = ZeraColorFamily.Red,
                type = ZeraButtonType.Secondary,
                fillMaxWidth = true,
            )

            state.errorMessage?.let { message ->
                CaptionText(text = message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
@Preview(heightDp = 900)
private fun SchedulingDetailsScreenPreview() {
    ZeraTheme {
        SchedulingDetailsScreen()
    }
}
