package com.zera.android.view.screens.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.graphs.CircularGraph
import com.zera.android.view.components.graphs.HorizontalBarGraph
import com.zera.android.view.components.graphs.VerticalBarGraph
import com.zera.android.view.components.inputs.ZeraChipsGroup
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.SubtitleText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette
import com.zera.android.viewmodel.manager.IndexesViewModel

@Composable
fun IndexesScreen(
    viewModel: IndexesViewModel = viewModel()
) {
    val state by viewModel.state
    val greenPalette = ZeraColorFamily.Green.palette()

    ManagerScaffold(
        title = "Indicadores",
        goBack = true,
    ) {
        SubtitleText(text = "Acompanhe desempenho e impacto")

        ZeraChipsGroup(
            options = state.periodFilters,
            selected = state.selectedFilter,
            onSelectedChange = viewModel::onFilterChange,
            style = ZeraColorFamily.Blue,
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Radius.large),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.large),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.micro)) {
                    LabelText(text = "Taxa de reciclagem")
                    HeadlineText(text = state.recyclingRateLabel, bold = true)
                    CaptionText(
                        text = state.recyclingRateDeltaLabel,
                        color = greenPalette.base,
                        bold = true,
                    )
                }
                CircularGraph(
                    progress = { state.recyclingGoal },
                    label = "Meta",
                    color = greenPalette.base,
                )
            }
        }

        TitleText(
            text = "Evolução mensal",
            bold = true,
            color = MaterialTheme.colorScheme.onSurface,
        )
        VerticalBarGraph(
            title = "Materiais descartados (kg)",
            items = state.monthlyEvolution,
        )

        TitleText(
            text = "Resíduos por categoria",
            bold = true,
            color = MaterialTheme.colorScheme.onSurface,
        )
        HorizontalBarGraph(items = state.residuesByCategory)
    }
}

@Composable
@Preview(heightDp = 900)
fun IndexesScreenPreview() {
    ZeraTheme {
        IndexesScreen()
    }
}
