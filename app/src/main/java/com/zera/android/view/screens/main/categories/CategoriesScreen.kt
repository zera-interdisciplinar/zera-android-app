package com.zera.android.view.screens.main.categories

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.feedback.ErrorFeedbackPopup
import com.zera.android.view.components.inputs.ZeraSearchInput
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.components.lists.OptionList
import com.zera.android.view.components.loading.OptionListSkeleton
import com.zera.android.view.components.loading.SkeletonBox
import com.zera.android.view.components.scaffolds.ManagerScaffold
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.main.categories.CategoriesState
import com.zera.android.viewmodel.main.categories.CategoriesViewModel

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = viewModel(),
) {
    val state by viewModel.state

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    CategoriesContent(
        state = state,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onAddCategoryClick = viewModel::onAddCategoryClick,
        onErrorDismiss = viewModel::onErrorDismiss,
    )
}

/**
 * Conteúdo da tela sem o [CategoriesViewModel], para as previews não dispararem o `refresh()` do
 * retorno à tela (que reativaria `isLoading` e esconderia o estado que se quer ver).
 */
@Composable
private fun CategoriesContent(
    state: CategoriesState,
    onSearchQueryChange: (String) -> Unit,
    onAddCategoryClick: () -> Unit,
    onErrorDismiss: () -> Unit,
) {
    ManagerScaffold(
        title = "Categorias",
        currentRoute = Route.Categories,
        goBack = true,
        scrollable = false,
    ) {
        ZeraSearchInput(
            value = state.searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = "Pesquisar por nome ou descrição...",
            onSearch = {},
        )

        ZeraButton(
            text = "Adicionar nova categoria",
            onClick = onAddCategoryClick,
            fillMaxWidth = true,
            icon = ZeraIcon.Plus,
        )

        // Só a região da lista alterna: a busca e o botão ficam reais.
        if (state.isLoading) {
            SkeletonBox(width = 120.dp, height = 24.dp)
            OptionListSkeleton(modifier = Modifier.weight(1f))
        } else {
            TitleText(text = state.totalCategoriesLabel, bold = true)
            OptionList(
                options = state.categories,
                onItemClick = null,
                contentPadding = PaddingValues(vertical = Spacing.small),
                modifier = Modifier.weight(1f),
                emptyContent = {
                    if (!state.loadFailed) BodyText(text = "Nenhuma categoria encontrada")
                },
            )
        }
    }

    state.errorMessage?.let { message ->
        ErrorFeedbackPopup(description = message, onDismissRequest = onErrorDismiss)
    }
}

@Composable
@Preview(heightDp = 900)
fun CategoriesScreenPreview() {
    ZeraTheme {
        CategoriesContent(
            state = CategoriesState(
                isLoading = false,
                totalCategoriesLabel = "2 Categorias",
                categories = listOf(
                    OptionItem(name = "Informática", description = "Notebooks e monitores"),
                    OptionItem(name = "Mobiliário", description = ""),
                ),
            ),
            onSearchQueryChange = {},
            onAddCategoryClick = {},
            onErrorDismiss = {},
        )
    }
}

/** A mesma tela com a carga em andamento (`isLoading` de [CategoriesState] nasce `true`). */
@Composable
@Preview(heightDp = 900)
fun CategoriesScreenLoadingPreview() {
    ZeraTheme {
        CategoriesContent(
            state = CategoriesState(),
            onSearchQueryChange = {},
            onAddCategoryClick = {},
            onErrorDismiss = {},
        )
    }
}
