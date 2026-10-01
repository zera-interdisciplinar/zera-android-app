package com.zera.android.view.screens.manager.recycling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.cards.OpeningHoursCard
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.outros.Tag
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.OverlineText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette
import com.zera.android.viewmodel.manager.RecyclingResumeViewModel

@Composable
fun RecyclingResumeScreen(
    viewModel: RecyclingResumeViewModel = viewModel(),
) {
    val state by viewModel.state

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Resumo de Descarte",
                goBack = true,
                showActions = false,
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
                verticalArrangement = Arrangement.spacedBy(Spacing.large),
            ) {
                ZeraBox(
                    modifier = Modifier.fillMaxWidth(),
                    style = ZeraColorFamily.Blue,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        OverlineText(
                            text = state.recyclerName,
                            color = ZeraColorFamily.Yellow.palette().base,
                            bold = true,
                        )
                        TitleText(
                            text = state.acceptedMaterials,
                            bold = true,
                            color = LocalContentColor.current,
                        )
                        BodyText(
                            text = state.addressLabel,
                            color = LocalContentColor.current,
                        )
                        state.isOpen?.let { isOpen ->
                            Tag(
                                text = if (isOpen) "Aberto agora" else "Fechado",
                                style = if (isOpen) ZeraColorFamily.Green else ZeraColorFamily.Red,
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    TitleText(text = "Sobre o local", bold = true)
                    BodyText(
                        text = state.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    TitleText(text = "Horários", bold = true)
                    OpeningHoursCard(items = state.openingHours)
                }

                // TODO: listar os itens selecionados para descarte (vêm da seleção em ItensSelection)

                state.errorMessage?.let { message ->
                    CaptionText(text = message, color = MaterialTheme.colorScheme.error)
                }
            }

            ZeraButton(
                text = "Confirmar descarte",
                onClick = viewModel::onConfirmClick,
                fillMaxWidth = true,
            )
        }
    }
}

@Composable
@Preview(heightDp = 800)
private fun RecyclingResumeScreenPreview() {
    ZeraTheme {
        RecyclingResumeScreen()
    }
}
