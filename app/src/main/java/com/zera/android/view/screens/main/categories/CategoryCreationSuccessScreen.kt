package com.zera.android.view.screens.main.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.layouts.SuccessScreenLayout
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.main.categories.CategoryCreationSuccessViewModel

@Composable
fun CategoryCreationSuccessScreen(
    name: String,
    description: String,
    viewModel: CategoryCreationSuccessViewModel = viewModel(),
) {
    SuccessScreenLayout(
        title = "Categoria cadastrada!",
        message = "Ela já pode ser escolhida ao cadastrar um modelo.",
        onCloseClick = viewModel::onCloseClick,
        onBackToHomeClick = viewModel::onBackToHomeClick,
        backToHomeText = "Ir para home",
        content = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Radius.xLarge),
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                ) {
                    TitleText(
                        text = name.ifBlank { "Categoria sem nome" },
                        bold = true,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (description.isNotBlank()) {
                        BodyText(
                            text = description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        },
        actions = {
            ZeraButton(
                text = "Registrar outra categoria",
                onClick = viewModel::onCreateAnotherClick,
                style = ZeraColorFamily.Yellow,
                fillMaxWidth = true,
            )
        },
    )
}

@Composable
@Preview(heightDp = 900)
private fun CategoryCreationSuccessScreenPreview() {
    ZeraTheme {
        CategoryCreationSuccessScreen(
            name = "Informática",
            description = "Notebooks e monitores",
        )
    }
}
