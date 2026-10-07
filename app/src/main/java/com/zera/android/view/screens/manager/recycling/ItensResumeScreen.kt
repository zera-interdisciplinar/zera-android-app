package com.zera.android.view.screens.manager.recycling

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.lists.ProductItem
import com.zera.android.view.components.lists.ProductList
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.manager.ItensResumeViewModel

@Composable
fun ItensResumeScreen(
    itemIds: List<String>,
    itemNames: List<String>,
    viewModel: ItensResumeViewModel = viewModel(),
) {
    val state by viewModel.state

    LaunchedEffect(itemIds, itemNames) {
        viewModel.bind(itemIds, itemNames)
    }

    ItensResumeContent(items = state.items)
}

@Composable
private fun ItensResumeContent(
    items: List<ProductItem>,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UpperNavBar(
                title = "Itens selecionados",
                goBack = true,
                showActions = false,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.small),
            )
        },
    ) { innerPadding ->
        ProductList(
            products = items,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@Composable
@Preview(heightDp = 800)
private fun ItensResumeScreenPreview() {
    val names = listOf(
        "Placa de vídeo", "Teclado mecânico", "Mouse sem fio", "Monitor 24 polegadas",
        "Notebook", "Bateria de lítio", "Carregador USB-C", "Roteador Wi-Fi",
        "Impressora a jato de tinta", "Fonte de alimentação", "Memória RAM",
        "Disco rígido externo", "Celular antigo", "Fone de ouvido", "Webcam",
        "Tablet",
    )
    val items = names.mapIndexed { index, name ->
        ProductItem(id = (1000000 + index * 7919).toString(), name = name)
    }

    ZeraTheme {
        ItensResumeContent(items = items)
    }
}
