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
import androidx.compose.runtime.LaunchedEffect
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
    placeId: String,
    placeName: String,
    placeAddress: String,
    distanceMeters: Long,
    itemIds: List<String>,
    itemNames: List<String>,
    isOpen: Boolean? = null,
    description: String = "",
    openingDays: List<String> = emptyList(),
    openingHourLabels: List<String> = emptyList(),
    recyclingBusinessId: String = "",
    contactEmail: String = "",
    viewModel: RecyclingResumeViewModel = viewModel(),
) {
    val state by viewModel.state

    LaunchedEffect(placeId, placeName, placeAddress, distanceMeters, itemIds, itemNames, recyclingBusinessId) {
        viewModel.bind(
            placeId, placeName, placeAddress, distanceMeters, itemIds, itemNames,
            isOpen, description, openingDays, openingHourLabels, recyclingBusinessId, contactEmail,
        )
    }

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

                if (state.description.isNotBlank()) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        TitleText(text = "Sobre o local", bold = true)
                        BodyText(
                            text = state.description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                if (state.openingHours.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        TitleText(text = "Horários", bold = true)
                        OpeningHoursCard(items = state.openingHours)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    ZeraButton(
                        text = "Visualizar Itens selecionados",
                        onClick = viewModel::onViewItemsClick,
                        fillMaxWidth = true
                    )
                }

                state.errorMessage?.let { message ->
                    CaptionText(text = message, color = MaterialTheme.colorScheme.error)
                }
            }

            ZeraButton(
                text = if (state.isConfirming) "Registrando…" else "Confirmar descarte",
                onClick = viewModel::onConfirmClick,
                fillMaxWidth = true,
                style = ZeraColorFamily.Green,
                enabled = !state.isConfirming && state.itemIds.isNotEmpty(),
            )
        }
    }
}

@Composable
@Preview(heightDp = 800)
private fun RecyclingResumeScreenPreview() {
    ZeraTheme {
        RecyclingResumeScreen(
            placeId = "places/ChIJ",
            placeName = "Recicla Tech Moema",
            placeAddress = "Av. Pavão, 620",
            distanceMeters = 2800,
            itemIds = listOf("1", "2"),
            itemNames = listOf("Notebook", "Bateria"),
        )
    }
}
