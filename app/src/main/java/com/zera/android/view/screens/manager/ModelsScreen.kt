package com.zera.android.view.screens.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.inputs.ZeraSearchInput
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.components.lists.OptionList
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.manager.ModelsState
import com.zera.android.viewmodel.manager.ModelsViewModel

@Composable
fun ModelsScreen(
    viewModel: ModelsViewModel = viewModel(),
) {
    val state by viewModel.state

    ManagerScaffold(
        title = "Modelos",
        currentRoute = Route.Models,
        goBack = true,
        scrollable = false,
        onFabClick = { /* TODO: abrir chatbot */ },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            ZeraSearchInput(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = "Pesquisar por descrição, nome ou material...",
                onSearch = viewModel::onSearch,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                icon = ZeraIcon.Filter,
                onClick = viewModel::onFilterClick,
                contentDescription = "Filtros",
                type = ZeraButtonType.Base,
            )
        }

        ZeraButton(
            text = "Adicionar novo modelo",
            onClick = viewModel::onAddModelClick,
            fillMaxWidth = true,
            icon = ZeraIcon.Plus,
        )

        state.errorMessage?.let { message ->
            CaptionText(text = message, color = MaterialTheme.colorScheme.error)
        }
        TitleText(text = state.totalModelsLabel, bold = true)
        OptionList(
            options = state.models,
            onItemClick = viewModel::onModelClick,
            contentPadding = PaddingValues(vertical = Spacing.small),
            modifier = Modifier.weight(1f),
            emptyContent = { BodyText(text = "Nenhum modelo encontrado") },
        )
    }
}

@Composable
@Preview(heightDp = 900)
fun ModelsScreenPreview() {
    val previewViewModel = ModelsViewModel().apply {
        state.value = ModelsState(
            totalModelsLabel = "1.230 modelos",
            models = listOf(
                OptionItem(description = "14 itens neste modelo", name = "Placa de vídeo"),
                OptionItem(description = "7 itens neste modelo", name = "Placa de vídeo"),
                OptionItem(description = "10 itens neste modelo", name = "Placa de vídeo"),
                OptionItem(description = "8 itens neste modelo", name = "Placa de vídeo"),
            ),
        )
    }
    ZeraTheme {
        ModelsScreen(viewModel = previewViewModel)
    }
}
