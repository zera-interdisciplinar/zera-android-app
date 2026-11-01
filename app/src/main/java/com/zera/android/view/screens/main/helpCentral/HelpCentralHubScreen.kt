package com.zera.android.view.screens.main.helpCentral

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.containers.ZeraBoxType
import com.zera.android.view.components.inputs.ZeraSearchInput
import com.zera.android.view.components.lists.OptionItem
import com.zera.android.view.components.lists.OptionList
import com.zera.android.view.components.loading.OptionListSkeleton
import com.zera.android.view.components.scaffolds.EmployeeScaffold
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.main.helpCentral.HelpCentralHubState
import com.zera.android.viewmodel.main.helpCentral.HelpCentralHubViewModel

/**
 * Central de Ajuda: busca de documentos, atalho para conversar com o assistente (Zack)
 * e a lista dos documentos encontrados.
 */
@Composable
fun HelpCentralHubScreen(
    viewModel: HelpCentralHubViewModel = viewModel(),
) {
    val state by viewModel.state

    EmployeeScaffold(
        title = "Central de ajuda",
        currentRoute = Route.HelpCentralHub,
        goBack = true,
        fabIcon = null,
        scrollable = false,
    ) {
        ZeraBox(
            modifier = Modifier.fillMaxWidth(),
            style = ZeraColorFamily.Blue,
            contentPadding = PaddingValues(Spacing.medium),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TitleText(
                    text = "Como podemos ajudar?",
                    bold = true,
                    textAlign = TextAlign.Center,
                    color = LocalContentColor.current,
                )
                ZeraSearchInput(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = "Pesquisar por ID, Nome ou Material...",
                    onSearch = viewModel::onSearch,
                )
            }
        }

        ZeraBox(
            modifier = Modifier.fillMaxWidth(),
            style = ZeraColorFamily.Green,
            type = ZeraBoxType.Secondary,
            contentPadding = PaddingValues(Spacing.medium),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.micro),
                ) {
                    BodyText(text = "Ainda precisa de ajuda?", bold = true)
                    CaptionText(text = "Fale com o Zack")
                }
                ZeraButton(
                    text = "Conversar",
                    onClick = viewModel::onChatClick,
                    icon = ZeraIcon.Chatbot,
                )
            }
        }

        TitleText(
            text = "Documentos",
            bold = true,
            color = MaterialTheme.colorScheme.onSurface,
        )
        state.errorMessage?.let { message ->
            CaptionText(text = message, color = MaterialTheme.colorScheme.error)
        }
        if (state.isLoading) {
            OptionListSkeleton(modifier = Modifier.weight(1f))
        } else {
            OptionList(
                options = state.documents,
                onItemClick = viewModel::onDocumentClick,
                contentPadding = PaddingValues(vertical = Spacing.small),
                modifier = Modifier.weight(1f),
                emptyContent = { BodyText(text = "Nenhum documento encontrado") },
            )
        }
    }
}

@Composable
@Preview(heightDp = 900)
fun HelpCentralHubScreenPreview() {
    val previewViewModel = HelpCentralHubViewModel().apply {
        state.value = HelpCentralHubState(
            documents = listOf(
                OptionItem(
                    name = "Como cadastrar um item",
                    description = "Passo a passo do cadastro manual e por scan",
                    icon = ZeraIcon.InfoCircle,
                ),
                OptionItem(
                    name = "Descarte de eletrônicos",
                    description = "Materiais aceitos e como agendar a coleta",
                    icon = ZeraIcon.Recycle,
                ),
            ),
        )
    }
    ZeraTheme {
        HelpCentralHubScreen(viewModel = previewViewModel)
    }
}

/** A mesma tela sem nenhum documento encontrado. */
@Composable
@Preview(heightDp = 900)
fun HelpCentralHubScreenEmptyPreview() {
    ZeraTheme {
        HelpCentralHubScreen(viewModel = HelpCentralHubViewModel())
    }
}
