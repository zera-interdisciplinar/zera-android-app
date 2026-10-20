package com.zera.android.view.screens.main.indexes

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.graphs.BarGraphItem
import com.zera.android.view.components.graphs.CircularGraph
import com.zera.android.view.components.graphs.HorizontalBarGraph
import com.zera.android.view.components.graphs.VerticalBarGraph
import com.zera.android.view.components.graphs.VerticalBarGraphItem
import com.zera.android.view.components.inputs.ZeraChipsGroup
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.scaffolds.ManagerScaffold
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.SubtitleText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette
import com.zera.android.viewmodel.main.indexes.IndexesState
import com.zera.android.viewmodel.main.indexes.IndexesViewModel

// Alturas aproximadas dos blocos reais; o esqueleto só indica a disposição.
private val SkeletonRateCardHeight = 110.dp
private val SkeletonTitleHeight = 24.dp
private val SkeletonMonthlyGraphHeight = 200.dp
private val SkeletonCategoryGraphHeight = 160.dp

@Composable
fun IndexesScreen(
    viewModel: IndexesViewModel = viewModel()
) {
    val state by viewModel.state
    val greenPalette = ZeraColorFamily.Green.palette()

    ManagerScaffold(
        title = "Indicadores",
        currentRoute = Route.Indexes,
        goBack = true,
    ) {
        SubtitleText(text = "Acompanhe desempenho e impacto")

        state.errorMessage?.let { message ->
            CaptionText(text = message, color = MaterialTheme.colorScheme.error)
        }

        // Os filtros ficam reais: a troca de filtro reativa isLoading e o skeleton volta nos gráficos.
        ZeraChipsGroup(
            options = state.periodFilters,
            selected = state.selectedFilter,
            onSelectedChange = viewModel::onFilterChange,
            style = ZeraColorFamily.Blue,
        )

        if (state.isLoading) {
            IndexesSkeleton()
        } else {
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
                    val deltaPalette = if (state.recyclingRateDeltaPositive) {
                        greenPalette
                    } else {
                        ZeraColorFamily.Red.palette()
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.micro)) {
                        LabelText(text = "Taxa de reciclagem")
                        HeadlineText(text = state.recyclingRateLabel, bold = true)
                        if (state.recyclingRateDeltaLabel.isNotBlank()) {
                            CaptionText(
                                text = state.recyclingRateDeltaLabel,
                                color = deltaPalette.base,
                                bold = true,
                            )
                        }
                    }
                    CircularGraph(
                        progress = { state.recyclingRateProgress },
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
                barWidth = 55.dp
            )

            TitleText(
                text = "Resíduos por categoria",
                bold = true,
                color = MaterialTheme.colorScheme.onSurface,
            )
            HorizontalBarGraph(items = state.residuesByCategory)
        }
    }
}

/** Esqueleto do card de taxa de reciclagem e dos dois gráficos, emitido direto na `Column` do scaffold. */
@Composable
private fun IndexesSkeleton() {
    SkeletonBox(height = SkeletonRateCardHeight, borderRadius = Radius.large)

    SkeletonBox(width = 140.dp, height = SkeletonTitleHeight)
    SkeletonBox(height = SkeletonMonthlyGraphHeight, borderRadius = Radius.large)

    SkeletonBox(width = 180.dp, height = SkeletonTitleHeight)
    SkeletonBox(height = SkeletonCategoryGraphHeight, borderRadius = Radius.large)
}

@Composable
@Preview(heightDp = 900)
fun IndexesScreenPreview() {
    val previewViewModel = IndexesViewModel().apply {
        state.value = IndexesState(
            recyclingRateLabel = "72%",
            recyclingRateDeltaLabel = "↑ 5% no período",
            recyclingRateProgress = 0.72f,
            monthlyEvolution = listOf(
                VerticalBarGraphItem(label = "Jul", value = 120f),
                VerticalBarGraphItem(label = "Ago", value = 180f),
                VerticalBarGraphItem(label = "Set", value = 150f),
            ),
            residuesByCategory = listOf(
                BarGraphItem(label = "Plásticos", percentage = 0.45f),
                BarGraphItem(label = "Metal", percentage = 0.3f),
                BarGraphItem(label = "Placas", percentage = 0.15f),
            ),
            isLoading = false,
        )
    }
    ZeraTheme {
        IndexesScreen(viewModel = previewViewModel)
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [IndexesState] nasce `true`). */
@Composable
@Preview(heightDp = 900)
fun IndexesScreenLoadingPreview() {
    val previewViewModel = IndexesViewModel().apply {
        state.value = IndexesState()
    }
    ZeraTheme {
        IndexesScreen(viewModel = previewViewModel)
    }
}
