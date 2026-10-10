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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.cards.Notification
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.lists.OptionList
import com.zera.android.view.components.overlays.BottomSheet
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.OverlineText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.manager.SchedulingDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulingDetailsScreen(
    scheduleId: String = "",
    viewModel: SchedulingDetailsViewModel = viewModel(),
) {
    val state by viewModel.state
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
                        text = state.placeName.ifBlank { state.recyclerName },
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
                enabled = !state.isCancelling,
                style = ZeraColorFamily.Red,
                type = ZeraButtonType.Secondary,
                fillMaxWidth = true,
            )

            state.errorMessage?.let { message ->
                CaptionText(text = message, color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (state.showDateSheet) {
        val initial = SchedulingDetailsViewModel.initialDateMillis(state.rawScheduledAt)
        val (hour, minute) = SchedulingDetailsViewModel.initialTime(state.rawScheduledAt)
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initial)
        val timePickerState = rememberTimePickerState(initialHour = hour, initialMinute = minute)
        BottomSheet(
            onDismissRequest = viewModel::dismissDateSheet,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            TitleText(text = "Alterar data ou horário", bold = true)
            Column(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                DatePicker(state = datePickerState, showModeToggle = false)
                TimePicker(state = timePickerState)
            }
            state.errorMessage?.let { message ->
                CaptionText(text = message, color = MaterialTheme.colorScheme.error)
            }
            ZeraButton(
                text = "Salvar",
                fillMaxWidth = true,
                enabled = !state.isSaving && datePickerState.selectedDateMillis != null,
                onClick = {
                    val millis = datePickerState.selectedDateMillis ?: return@ZeraButton
                    viewModel.confirmSchedule(millis, timePickerState.hour, timePickerState.minute)
                },
            )
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
