package com.zera.android.view.screens.manager.recycling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.lists.OptionList
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.OverlineText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.screens.manager.ManagerScaffold
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette
import com.zera.android.viewmodel.manager.ScheduledDisposalsViewModel

@Composable
fun ScheduledDisposalsScreen(
    viewModel: ScheduledDisposalsViewModel = viewModel(),
) {
    val state by viewModel.state

    ManagerScaffold(
        title = "Descartes agendados",
        currentRoute = Route.ScheduledDisposals,
        goBack = true,
        scrollable = false,
        fabIcon = null,
    ) {
        if (state.hasNextDisposal) {
            ZeraBox(
                modifier = Modifier.fillMaxWidth(),
                style = ZeraColorFamily.Blue,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    OverlineText(
                        text = "PRÓXIMO DESCARTE",
                        color = ZeraColorFamily.Yellow.palette().base,
                        bold = true,
                    )
                    TitleText(
                        text = state.nextDisposalDate,
                        bold = true,
                        color = LocalContentColor.current,
                    )
                    BodyText(
                        text = state.nextDisposalRecyclerName,
                        color = LocalContentColor.current,
                    )
                }
            }
        }

        ZeraButton(
            text = "Agendar próximo descarte",
            onClick = viewModel::onScheduleNextClick,
            fillMaxWidth = true,
        )

        state.errorMessage?.let { message ->
            CaptionText(text = message, color = MaterialTheme.colorScheme.error)
        }

        OptionList(
            options = state.disposals,
            onItemClick = viewModel::onDisposalClick,
            contentPadding = PaddingValues(vertical = Spacing.small),
            modifier = Modifier.weight(1f),
            emptyContent = { BodyText(text = "Nenhum descarte agendado") },
        )
    }
}

@Composable
@Preview(heightDp = 900)
private fun ScheduledDisposalsScreenPreview() {
    ZeraTheme {
        ScheduledDisposalsScreen()
    }
}
