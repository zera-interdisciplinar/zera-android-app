package com.zera.android.view.screens.main.models

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
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.main.models.ModelCreationSuccessViewModel

@Composable
fun ModelCreationSuccessScreen(
    modelName: String,
    material: String,
    brand: String,
    notes: String,
    viewModel: ModelCreationSuccessViewModel = viewModel(),
) {
    SuccessScreenLayout(
        title = "Modelo cadastrado!",
        message = "O modelo foi enviado para aprovação.",
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
                        text = modelName.ifBlank { "Modelo sem nome" },
                        bold = true,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (material.isNotBlank()) {
                        LabelText(
                            text = "Material: $material",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (brand.isNotBlank()) {
                        LabelText(
                            text = "Marca: $brand",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (notes.isNotBlank()) {
                        BodyText(
                            text = notes,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        },
        actions = {
            ZeraButton(
                text = "Registrar outro modelo",
                onClick = viewModel::onCreateAnotherClick,
                style = ZeraColorFamily.Yellow,
                fillMaxWidth = true,
            )
        },
    )
}

@Composable
@Preview(heightDp = 900)
private fun ModelCreationSuccessScreenPreview() {
    ZeraTheme {
        ModelCreationSuccessScreen(
            modelName = "Notebook",
            material = "Eletrônico",
            brand = "Dell",
            notes = "Modelo para notebooks corporativos",
        )
    }
}
