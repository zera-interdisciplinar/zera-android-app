package com.zera.android.view.screens.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.navigation.BottomNavBar
import com.zera.android.view.components.navigation.UpperNavBar
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

/**
 * Casca (Scaffold) compartilhada pelas telas da área do gestor.
 *
 * Já monta a [UpperNavBar] (com o [title] da tela), a [BottomNavBar] fixada em
 * [Route.ManagerHome] e o botão flutuante de assistente virtual — com os insets de
 * status bar / navigation bar já aplicados. Cada tela só precisa passar o [title] e
 * o [content], que é desenhado dentro de uma [Column] rolável.
 *
 * @param title título exibido na [UpperNavBar].
 * @param modifier modificador externo opcional, aplicado ao [Scaffold].
 * @param fabIcon ícone do botão flutuante. Quando `null`, nenhum FAB é exibido.
 * @param onFabClick ação do botão flutuante. Só é usada quando [fabIcon] não for `null`.
 * @param content conteúdo da tela, desenhado dentro da [Column] rolável do Scaffold.
 */
@Composable
fun ManagerScaffold(
    title: String,
    modifier: Modifier = Modifier,
    fabIcon: ZeraIcon? = ZeraIcon.Chatbot,
    onFabClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            UpperNavBar(
                title = title,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Spacing.small),
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Route.ManagerHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = Spacing.medium, vertical = Spacing.small),
            )
        },
        floatingActionButton = {
            if (fabIcon != null) {
                IconButton(
                    icon = fabIcon,
                    onClick = onFabClick,
                    contentDescription = "Assistente virtual",
                    size = 56.dp,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.medium, vertical = Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            content = content,
        )
    }
}

@Preview(heightDp = 600)
@Composable
private fun ManagerScaffoldPreview() {
    ZeraTheme {
        ManagerScaffold(title = "Visão geral") {
            BodyText(text = "Conteúdo da tela")
        }
    }
}
