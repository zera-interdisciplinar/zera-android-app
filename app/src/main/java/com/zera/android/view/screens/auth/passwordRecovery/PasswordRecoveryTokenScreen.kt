package com.zera.android.view.screens.auth.passwordRecovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.inputs.ZeraTokenInput
import com.zera.android.view.components.logo.Logo
import com.zera.android.view.components.outros.SplashBackground
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.transition.SharedElementKeys
import com.zera.android.view.transition.sharedTransition
import com.zera.android.viewmodel.auth.passwordRecovery.PasswordRecoveryTokenViewModel

@Composable
fun PasswordRecoveryTokenScreen(
    viewModel: PasswordRecoveryTokenViewModel = viewModel()
) {
    val state by viewModel.state

    SplashBackground()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.large),
            modifier = Modifier.padding(horizontal = Spacing.xLarge)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Logo(
                    Modifier
                        .sharedTransition(SharedElementKeys.Logo)
                        .width(250.dp)
                        .padding(bottom = Spacing.small)
                )
                TitleText("Esqueci minha senha", bold = true)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CaptionText(
                    text = "Um código foi enviado para o seu email, confira e cole aqui ou então",
                    textAlign = TextAlign.Center,
                )
                CaptionText(
                    text = "altere o email",
                    bold = true,
                    onClick = { viewModel.changeEmail() },
                    textAlign = TextAlign.Center,
                )
            }
            ZeraTokenInput(
                value = state.token,
                onValueChange = { viewModel.onTokenChange(it) },
                label = "Código de Acesso",
                size = PasswordRecoveryTokenViewModel.TOKEN_SIZE,
                isError = state.errorMessage != null,
                errorMessage = state.errorMessage,
                onFilled = { viewModel.verifyToken() },
            )
        }
        Spacer(Modifier.height(Spacing.xLarge))
        Box {
            ZeraButton(
                text = if (state.isLoading) "Enviando..." else "Enviar",
                enabled = !state.isLoading,
                onClick = { viewModel.verifyToken() }
            )
        }
    }
}

@Composable
@Preview
fun PasswordRecoveryTokenScreenPreview() {
    ZeraTheme {
        PasswordRecoveryTokenScreen()
    }
}
