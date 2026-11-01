package com.zera.android.view.screens.error

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.error.ConnectionErrorViewModel

/** Tela exibida quando o aparelho está sem conexão. */
@Composable
fun ConnectionErrorScreen(
    viewModel: ConnectionErrorViewModel = viewModel(),
) {
    val state by viewModel.state
    val activity = LocalContext.current as? Activity

    LaunchedEffect(state.shouldExitApp) {
        if (state.shouldExitApp) activity?.finish()
    }

    ErrorScreenModel(
        icon = ZeraIcon.Exclamation,
        iconColor = ZeraColorFamily.Yellow,
        title = "Você está offline",
        description = "Seus dados serão enviados quando a conexão voltar.",
        actions = {
            ZeraButton(
                text = "Tentar novamente",
                onClick = viewModel::onRetryClick,
                fillMaxWidth = true,
            )
            LabelText(
                text = "Sair do aplicativo",
                bold = true,
                color = MaterialTheme.colorScheme.primary,
                onClick = viewModel::onExitAppClick,
            )
        },
    )
}

@Composable
@Preview(heightDp = 800)
private fun ConnectionErrorScreenPreview() {
    ZeraTheme {
        ConnectionErrorScreen()
    }
}
