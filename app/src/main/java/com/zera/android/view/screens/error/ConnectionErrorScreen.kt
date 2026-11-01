package com.zera.android.view.screens.error

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zera.android.model.remote.client.NetworkStatus
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.error.ConnectionErrorViewModel

/**
 * Tela exibida quando o aparelho está sem conexão. "Tentar novamente" checa a conexão antes de
 * sair; ainda offline, a tela continua.
 *
 * @param fromBoot `true` quando aberta pela Splash. Vinda do uso, o voltar do sistema faz o mesmo
 *   que "Tentar novamente" (volta para a tela anterior); vinda da Splash, a pilha só tem esta tela
 *   e o voltar fecha o app.
 */
@Composable
fun ConnectionErrorScreen(
    fromBoot: Boolean = false,
    viewModel: ConnectionErrorViewModel = run {
        val appContext = LocalContext.current.applicationContext
        viewModel(
            factory = viewModelFactory {
                initializer {
                    ConnectionErrorViewModel(
                        fromBoot = fromBoot,
                        isOnline = { NetworkStatus.isOnline(appContext) },
                    )
                }
            },
        )
    },
) {
    val state by viewModel.state
    val activity = LocalContext.current as? Activity

    LaunchedEffect(state.shouldExitApp) {
        if (state.shouldExitApp) activity?.finish()
    }
    BackHandler(enabled = !fromBoot, onBack = viewModel::onRetryClick)

    ErrorScreenModel(
        icon = ZeraIcon.Exclamation,
        iconColor = ZeraColorFamily.Yellow,
        title = "Você está offline",
        description = "Verifique sua conexão com a internet e tente novamente.",
        actions = {
            ZeraButton(
                text = "Tentar novamente",
                onClick = viewModel::onRetryClick,
                fillMaxWidth = true,
                loading = state.isChecking,
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
