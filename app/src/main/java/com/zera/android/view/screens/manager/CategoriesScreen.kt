package com.zera.android.view.screens.manager

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
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
import com.zera.android.viewmodel.manager.CategoriesState
import com.zera.android.viewmodel.manager.CategoriesViewModel

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = viewModel(),
) {
    val state by viewModel.state

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    ManagerScaffold(
        title = "Categorias",
        currentRoute = Route.Categories,
        goBack = true,
        scrollable = false,
        onFabClick = { /* TODO: abrir chatbot */ },
    ) {
        ZeraSearchInput(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            placeholder = "Pesquisar por nome ou descrição...",
            onSearch = {},
        )

        ZeraButton(
            text = "Adicionar nova categoria",
            onClick = viewModel::onAddCategoryClick,
            fillMaxWidth = true,
            icon = ZeraIcon.Plus,
        )

        state.errorMessage?.let { message ->
            CaptionText(text = message, color = MaterialTheme.colorScheme.error)
        }
        TitleText(text = state.totalCategoriesLabel, bold = true)
        OptionList(
            options = state.categories,
            onItemClick = null,
            contentPadding = PaddingValues(vertical = Spacing.small),
            modifier = Modifier.weight(1f),
            emptyContent = { BodyText(text = "Nenhuma categoria encontrada") },
        )
    }
}

@Composable
@Preview(heightDp = 900)
fun CategoriesScreenPreview() {
    val previewViewModel = CategoriesViewModel().apply {
        state.value = CategoriesState(
            totalCategoriesLabel = "2 Categorias",
            categories = listOf(
                OptionItem(name = "Informática", description = "Notebooks e monitores"),
                OptionItem(name = "Mobiliário", description = ""),
            ),
        )
    }
    ZeraTheme {
        CategoriesScreen(viewModel = previewViewModel)
    }
}
