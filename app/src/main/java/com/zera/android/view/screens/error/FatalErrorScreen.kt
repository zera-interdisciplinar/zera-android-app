package com.zera.android.view.screens.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.error.FatalErrorViewModel

/** Tela exibida quando ocorre um erro inesperado do qual o app não consegue se recuperar. */
@Composable
fun FatalErrorScreen(
    viewModel: FatalErrorViewModel = viewModel(),
) {
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
