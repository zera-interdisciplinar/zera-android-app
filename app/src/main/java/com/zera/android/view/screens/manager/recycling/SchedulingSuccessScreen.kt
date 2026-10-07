package com.zera.android.view.screens.manager.recycling

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.cards.SchedulingSummaryCard
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette
import com.zera.android.viewmodel.manager.SchedulingSuccessViewModel

/** Diâmetro do círculo de ícone. */
private val IconCircleSize = 96.dp

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
    val backgroundPalette = ZeraColorFamily.Blue.palette()
    val checkPalette = ZeraColorFamily.Green.palette()

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundPalette.base,
        contentColor = backgroundPalette.onBase,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                icon = ZeraIcon.Close,
                onClick = viewModel::onCloseClick,
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
                    .padding(Spacing.large)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium, Alignment.CenterVertically),
            ) {
                Surface(
                    modifier = Modifier.size(IconCircleSize),
                    shape = CircleShape,
                    color = checkPalette.container,
                    contentColor = checkPalette.onContainer,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        ZeraIcon(icon = ZeraIcon.Check, contentDescription = null, tint = checkPalette.onContainer)
                    }
                }

                HeadlineText(text = "Descarte registrado!", bold = true, color = LocalContentColor.current)
                BodyText(
                    text = "O descarte foi gravado no inventário.",
                    bold = true,
                    color = LocalContentColor.current,
                    textAlign = TextAlign.Center,
                )

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
                Column(){
                    ZeraButton(
                        text = if (state.isGeneratingReport) "Gerando relatório…" else "Gerar Relatorio",
                        onClick = viewModel::onGenerateReport,
                        type = ZeraButtonType.Primary,
                        style = ZeraColorFamily.Yellow,
                        fillMaxWidth = true,
                        enabled = !state.isGeneratingReport,
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
                    state.errorMessage?.let { message ->
                        CaptionText(text = message, color = ZeraColorFamily.Yellow.palette().base)
                    }
                }
                LabelText(
                    text = "Voltar para o início",
                    bold = true,
                    color = LocalContentColor.current,
                    onClick = viewModel::onBackToHomeClick,
                )
            }
        }
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
