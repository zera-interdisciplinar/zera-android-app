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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.inputs.ZeraInputType
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.logo.Logo
import com.zera.android.view.components.outros.SplashBackground
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.transition.SharedElementKeys
import com.zera.android.view.transition.sharedTransition
import com.zera.android.viewmodel.auth.passwordRecovery.PasswordRecoveryEmailViewModel

@Composable
fun PasswordRecoveryEmailScreen(
    viewModel: PasswordRecoveryEmailViewModel = viewModel()
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
            ZeraTextInput(
                value = state.email,
                label = "Email",
                placeholder = "Digite seu email corporativo",
                onValueChange = { viewModel.onEmailChange(it) },
                type = ZeraInputType.Email,
                imeAction = ImeAction.Done,
                isError = state.errorMessage != null,
                errorMessage = state.errorMessage,
            )
            state.successMessage?.let { message ->
                CaptionText(
                    text = message,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Spacer(Modifier.height(Spacing.xLarge))
        Box {
            ZeraButton(
                text = "Enviar",
                enabled = !state.isLoading,
                loading = state.isLoading,
                onClick = viewModel::sendEmail
            )
        }
    }
}

@Composable
@Preview
fun PasswordRecoveryEmailScreenPreview() {
    ZeraTheme {
        PasswordRecoveryEmailScreen()
    }
}
