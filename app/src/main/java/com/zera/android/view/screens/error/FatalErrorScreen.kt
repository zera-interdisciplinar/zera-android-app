package com.zera.android.view.screens.error

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.error.FatalErrorViewModel

/**
 * Tela exibida quando ocorre um erro inesperado do qual o app não consegue se recuperar.
 * "Ok, entendi" e o voltar do sistema fecham o app.
 */
@Composable
fun FatalErrorScreen(
    viewModel: FatalErrorViewModel = viewModel(),
) {
    val state by viewModel.state
    val activity = LocalContext.current as? Activity

    LaunchedEffect(state.shouldExitApp) {
        if (state.shouldExitApp) activity?.finish()
    }
    BackHandler(onBack = viewModel::onConfirmClick)

    ErrorScreenModel(
        icon = ZeraIcon.Exclamation,
        iconColor = ZeraColorFamily.Red,
        title = "Algo deu errado",
        description = "Ocorreu um erro inesperado. Tente novamente mais tarde.",
        actions = {
            ZeraButton(
                text = "Ok, entendi",
                onClick = viewModel::onConfirmClick,
                fillMaxWidth = true,
            )
        },
    )
}

@Composable
@Preview(heightDp = 800)
private fun FatalErrorScreenPreview() {
    ZeraTheme {
        FatalErrorScreen()
    }
}
