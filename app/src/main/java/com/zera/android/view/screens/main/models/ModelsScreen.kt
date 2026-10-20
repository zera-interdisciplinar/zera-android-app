package com.zera.android.view.screens.main.models

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.inputs.ZeraSearchInput
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.components.lists.OptionList
import com.zera.android.view.components.loading.OptionListSkeleton
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.overlays.BottomSheet
import com.zera.android.view.components.scaffolds.EmployeeScaffold
import com.zera.android.view.components.scaffolds.ManagerScaffold
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.main.models.ModelsState
import com.zera.android.viewmodel.main.models.ModelsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsScreen(
    viewModel: ModelsViewModel = viewModel(),
) {
    val state by viewModel.state

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    ModelsContent(
        state = state,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSearch = viewModel::onSearch,
        onAddModelClick = viewModel::onAddModelClick,
        onModelClick = viewModel::onModelClick,
        onEndReached = viewModel::loadNextPage,
    )

    if (state.isModelUsageSheetVisible) {
        BottomSheet(
            onDismissRequest = { viewModel.setModelUsageSheetVisible(false) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            TitleText(text = "Como usar o Modelo", bold = true)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.large))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(Spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                ModelUsageBullet("Cadastre um modelo com as informações de um tipo de item.")
                ModelUsageBullet("Assim, você evita repetir os mesmos dados a cada novo cadastro.")
                ModelUsageBullet("Vincule novos itens ao modelo existente.")
                ModelUsageBullet("Exemplo: cadastre ‘Notebook’ uma vez e depois informe apenas os dados específicos de cada item.")
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                ZeraButton(
                    text = "Ok, entendi",
                    onClick = { viewModel.setModelUsageSheetVisible(false) },
                    width = 280,
                )
                LabelText("Ainda tenho dúvidas", onClick = {})
            }
        }
    }
}

/**
 * Conteúdo da tela sem o [ModelsViewModel], para as previews não dispararem o `refresh()` do
 * retorno à tela (que reativaria `isLoading` e esconderia o estado que se quer ver).
 */
@Composable
private fun ModelsContent(
    state: ModelsState,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onAddModelClick: () -> Unit,
    onModelClick: (OptionItem) -> Unit,
    onEndReached: () -> Unit,
) {
    val body: @Composable ColumnScope.() -> Unit = {
        ZeraSearchInput(
            value = state.searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = "Pesquisar por descrição, nome ou material...",
            onSearch = onSearch,
        )

        ZeraButton(
            text = "Adicionar novo modelo",
            onClick = onAddModelClick,
            fillMaxWidth = true,
            icon = ZeraIcon.Plus,
        )

        state.errorMessage?.let { message ->
            CaptionText(text = message, color = MaterialTheme.colorScheme.error)
        }
        // Só a região da lista alterna: a busca (que reativa isLoading) e o botão ficam reais.
        if (state.isLoading) {
            SkeletonBox(width = 120.dp, height = 24.dp)
            OptionListSkeleton(modifier = Modifier.weight(1f))
        } else {
            TitleText(text = state.totalModelsLabel, bold = true)
            OptionList(
                options = state.models,
                onItemClick = onModelClick,
                contentPadding = PaddingValues(vertical = Spacing.small),
                modifier = Modifier.weight(1f),
                emptyContent = { BodyText(text = "Nenhum modelo encontrado") },
                onEndReached = onEndReached,
                isLoadingMore = state.isLoadingMore,
            )
        }
    }

    if (state.isEmployee) {
        EmployeeScaffold(
            title = "Modelos",
            currentRoute = Route.Models,
            goBack = true,
            scrollable = false,
            content = body,
        )
    } else {
        ManagerScaffold(
            title = "Modelos",
            currentRoute = Route.Models,
            goBack = true,
            scrollable = false,
            content = body,
        )
    }
}

@Composable
private fun ModelUsageBullet(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        verticalAlignment = Alignment.Top,
    ) {
        BodyText(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
        BodyText(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
@Preview(heightDp = 900)
fun ModelsScreenPreview() {
    ZeraTheme {
        ModelsContent(
            state = ModelsState(
                isLoading = false,
                totalModelsLabel = "1.230 modelos",
                models = listOf(
                    OptionItem(description = "14 itens neste modelo", name = "Placa de vídeo"),
                    OptionItem(description = "7 itens neste modelo", name = "Placa de vídeo"),
                    OptionItem(description = "10 itens neste modelo", name = "Placa de vídeo"),
                    OptionItem(description = "8 itens neste modelo", name = "Placa de vídeo"),
                ),
            ),
            onSearchQueryChange = {},
            onSearch = {},
            onAddModelClick = {},
            onModelClick = {},
            onEndReached = {},
        )
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [ModelsState] nasce `true`). */
@Composable
@Preview(heightDp = 900)
fun ModelsScreenLoadingPreview() {
    ZeraTheme {
        ModelsContent(
            state = ModelsState(),
            onSearchQueryChange = {},
            onSearch = {},
            onAddModelClick = {},
            onModelClick = {},
            onEndReached = {},
        )
    }
}
