package com.zera.android.view.screens.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette
import com.zera.android.viewmodel.manager.ModelCreationSuccessViewModel

private val IconCircleSize = 96.dp

@Composable
fun ModelCreationSuccessScreen(
    modelName: String,
    material: String,
    brand: String,
    notes: String,
    viewModel: ModelCreationSuccessViewModel = viewModel(),
) {
    val backgroundPalette = ZeraColorFamily.Blue.palette()
    val checkPalette = ZeraColorFamily.Green.palette()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundPalette.base,
        contentColor = backgroundPalette.onBase,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                icon = ZeraIcon.Close,
                onClick = viewModel::onCloseClick,
                contentDescription = "Fechar",
                type = ZeraButtonType.Tertiary,
                style = ZeraColorFamily.Yellow,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(Spacing.medium),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.large)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium, Alignment.CenterVertically),
            ) {
                Surface(
                    modifier = Modifier.size(IconCircleSize),
                    shape = CircleShape,
                    color = checkPalette.container,
                    contentColor = checkPalette.onContainer,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        ZeraIcon(
                            icon = ZeraIcon.Check,
                            contentDescription = null,
                            tint = checkPalette.onContainer,
                        )
                    }
                }

                HeadlineText(
                    text = "Modelo cadastrado!",
                    bold = true,
                    color = LocalContentColor.current,
                    textAlign = TextAlign.Center,
                )
                BodyText(
                    text = "O modelo foi enviado para aprovação.",
                    color = LocalContentColor.current,
                    textAlign = TextAlign.Center,
                )

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

                ZeraButton(
                    text = "Registrar outro modelo",
                    onClick = viewModel::onCreateAnotherClick,
                    style = ZeraColorFamily.Yellow,
                    fillMaxWidth = true,
                )
                LabelText(
                    text = "Ir para home",
                    bold = true,
                    color = LocalContentColor.current,
                    onClick = viewModel::onBackToHomeClick,
                )
            }
        }
    }
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
