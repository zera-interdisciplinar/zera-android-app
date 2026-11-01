package com.zera.android.view.screens.main.recycling

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.cards.Notification
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.components.lists.OptionList
import com.zera.android.view.components.loading.OptionListSkeleton
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.navigation.SideBarOverlay
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.OverlineText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.main.recycling.SchedulingDetailsState
import com.zera.android.viewmodel.main.recycling.SchedulingDetailsViewModel

// Alturas aproximadas dos blocos reais; o esqueleto só indica a disposição.
private val SkeletonHeaderCardHeight = 104.dp
private val SkeletonButtonHeight = 48.dp
private val SkeletonTitleHeight = 24.dp
private const val SkeletonItemCount = 3

@Composable
fun SchedulingDetailsScreen(
    scheduleId: String = "",
    viewModel: SchedulingDetailsViewModel = viewModel(),
) {
    val state by viewModel.state
    var showSideBar by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(scheduleId) {
        viewModel.load(scheduleId)
    }

    LaunchedEffect(state.pendingUri) {
        val uri = state.pendingUri ?: return@LaunchedEffect
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
        viewModel.consumePendingUri()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                UpperNavBar(
                    title = "Descarte",
                    goBack = true,
                    onSideBarClick = { showSideBar = true },
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
                if (state.isLoading) {
                    SchedulingDetailsSkeleton()
                } else {
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
                                text = state.placeName,
                                color = LocalContentColor.current,
                            )
                        }
                    }

                    ZeraButton(
                        text = if (state.reportUrl.isNullOrBlank()) "Gerar relatório" else "Ver relatório",
                        onClick = viewModel::onGenerateReportClick,
                        loading = state.isGeneratingReport,
                        fillMaxWidth = true,
                    )

                    OptionList(
                        options = state.options,
                        onItemClick = { viewModel.onItemsClick() },
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

                    if (state.canCancel) {
                        Notification(
                            label = "Precisa cancelar?",
                            text = "Os itens voltam para o estoque.",
                            style = ZeraColorFamily.Yellow,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        ZeraButton(
                            text = "Cancelar descarte",
                            onClick = viewModel::onCancelSchedulingClick,
                            loading = state.isCancelling,
                            style = ZeraColorFamily.Red,
                            type = ZeraButtonType.Secondary,
                            fillMaxWidth = true,
                        )
                    }

                    state.errorMessage?.let { message ->
                        CaptionText(text = message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        SideBarOverlay(
            visible = showSideBar,
            onDismiss = { showSideBar = false },
            items = state.sideBarItems,
            currentRoute = Route.SchedulingDetails(scheduleId),
        )
    }
}

/**
 * Esqueleto do cabeçalho, do botão de relatório, da lista de itens e do contato, emitido direto
 * na `Column` da tela. O bloco de cancelamento é condicional (`canCancel`) e fica de fora.
 */
@Composable
private fun SchedulingDetailsSkeleton() {
    SkeletonBox(height = SkeletonHeaderCardHeight, borderRadius = Radius.large)
    SkeletonBox(height = SkeletonButtonHeight, borderRadius = Radius.large)
    OptionListSkeleton(count = SkeletonItemCount)

    SkeletonBox(width = 220.dp, height = SkeletonTitleHeight)
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
        repeat(2) {
            SkeletonBox(
                modifier = Modifier.weight(1f),
                height = SkeletonButtonHeight,
                borderRadius = Radius.large,
            )
        }
    }
}

// Com scheduleId em branco a tela não dispara a carga real, então o state injetado é preservado.
@Composable
@Preview(heightDp = 900)
private fun SchedulingDetailsScreenPreview() {
    val previewViewModel = SchedulingDetailsViewModel().apply {
        state.value = SchedulingDetailsState(
            scheduledAt = "12/10/2026 · 14:30",
            placeName = "Cooperativa Verde",
            options = listOf(
                OptionItem(name = "Notebook", description = "ID 2659646"),
                OptionItem(name = "Teclado mecânico", description = "ID 1182036"),
            ),
            contactButtonsEnabled = true,
            canCancel = true,
            isLoading = false,
        )
    }
    ZeraTheme {
        SchedulingDetailsScreen(viewModel = previewViewModel)
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [SchedulingDetailsState] nasce `true`). */
@Composable
@Preview(heightDp = 900)
private fun SchedulingDetailsScreenLoadingPreview() {
    val previewViewModel = SchedulingDetailsViewModel().apply {
        state.value = SchedulingDetailsState()
    }
    ZeraTheme {
        SchedulingDetailsScreen(scheduleId = "s1", viewModel = previewViewModel)
    }
}
