package com.zera.android.view.screens.main.recycling

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.cards.SchedulingSummaryCard
import com.zera.android.view.components.feedback.ErrorFeedbackPopup
import com.zera.android.view.components.layouts.SuccessScreenLayout
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette
import com.zera.android.viewmodel.main.recycling.SchedulingSuccessViewModel

@Composable
fun SchedulingSuccessScreen(
    recyclerName: String,
    scheduledAt: String,
    materials: String,
    contactEmail: String,
    contactPhone: String,
    itemNames: List<String>,
    disposalId: String,
    viewModel: SchedulingSuccessViewModel = viewModel(),
) {
    val state by viewModel.state
    val context = LocalContext.current

    LaunchedEffect(recyclerName, scheduledAt, materials, contactEmail, contactPhone, itemNames, disposalId) {
        viewModel.bind(
            recyclerName = recyclerName,
            scheduledAt = scheduledAt,
            materials = materials,
            contactEmail = contactEmail,
            contactPhone = contactPhone,
            itemNames = itemNames,
            disposalId = disposalId,
        )
    }

    LaunchedEffect(state.pendingUri) {
        val uri = state.pendingUri ?: return@LaunchedEffect
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(Intent.createChooser(intent, null)) }
        viewModel.consumePendingUri()
    }

    SuccessScreenLayout(
        title = "Descarte registrado!",
        message = "O descarte foi gravado no inventário.",
        onCloseClick = viewModel::onCloseClick,
        onBackToHomeClick = viewModel::onBackToHomeClick,
        content = {
            SchedulingSummaryCard(
                recyclerName = state.recyclerName,
                scheduledAt = state.scheduledAt,
                materials = state.materials,
            )
            TitleText(
                text = "Contato",
                bold = true,
                color = LocalContentColor.current,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        actions = {
            Column {
                ZeraButton(
                    text = "Gerar Relatorio",
                    onClick = viewModel::onGenerateReport,
                    type = ZeraButtonType.Primary,
                    style = ZeraColorFamily.Yellow,
                    fillMaxWidth = true,
                    loading = state.isGeneratingReport,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                ) {
                    ZeraButton(
                        text = "E-mail",
                        onClick = viewModel::onEmailClick,
                        style = ZeraColorFamily.Blue,
                        type = ZeraButtonType.Secondary,
                        fillMaxWidth = true,
                        modifier = Modifier.weight(1f),
                    )
                    ZeraButton(
                        text = "Telefone",
                        onClick = viewModel::onPhoneClick,
                        style = ZeraColorFamily.Blue,
                        type = ZeraButtonType.Secondary,
                        fillMaxWidth = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
    )

    state.errorMessage?.let { message ->
        ErrorFeedbackPopup(description = message, onDismissRequest = viewModel::onErrorDismiss)
    }
}

@Composable
@Preview(heightDp = 900)
private fun SchedulingSuccessScreenPreview() {
    ZeraTheme {
        SchedulingSuccessScreen(
            recyclerName = "Recicla Tech Moema",
            scheduledAt = "21 de agosto · 08:00",
            materials = "Notebooks e baterias",
            contactEmail = "contato@reciclatech.com.br",
            contactPhone = "1140028922",
            itemNames = listOf("Notebook", "Bateria"),
            disposalId = "disposal-1",
        )
    }
}
