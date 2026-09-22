package com.zera.android.view.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.inputs.ZeraInputType
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.inputs.ZeraTokenInput
import com.zera.android.view.components.logo.Logo
import com.zera.android.view.components.outros.SplashBackground
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.transition.SharedElementKeys
import com.zera.android.view.transition.sharedTransition
import com.zera.android.viewmodel.auth.SignUpViewModel

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = viewModel()
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
                TitleText("Cadastrar funcionario", bold = true)
            }
            ZeraTextInput(
                value = state.name,
                label = "Nome",
                placeholder = "Insira seu nome aqui",
                onValueChange = { viewModel.onNameChange(it) }
            )
            ZeraTextInput(
                value = state.email,
                label = "Email",
                placeholder = "Seu email",
                onValueChange = { viewModel.onEmailChange(it) },
                type = ZeraInputType.Email
            )
            ZeraTextInput(
                value = state.password,
                label = "Senha",
                placeholder = "Senha aqui",
                onValueChange = { viewModel.onPasswordChange(it) },
                type = ZeraInputType.Password
            )
            ZeraTokenInput(
                value = state.token,
                onValueChange = { viewModel.onTokenChange(it) },
                label = "Código de convite",
                isError = state.errorMessage != null && state.token.length != 6,
                onFilled = { viewModel.signUp() },
            )
        }
        Spacer(Modifier.height(Spacing.xLarge))
        if (state.errorMessage != null) {
            CaptionText(
                text = state.errorMessage ?: "",
                color = Color.Red,
                modifier = Modifier.padding(horizontal = Spacing.xLarge, vertical = Spacing.small),
            )
        }
        Box {
            ZeraButton(
                text = if (state.isLoading) "Cadastrando..." else "Cadastrar",
                enabled = !state.isLoading,
                onClick = { viewModel.signUp() }
            )
        }
        CaptionText(
            text = "Já tenho conta",
            underline = true,
            onClick = { ZeraNavigator.push(Route.SignIn) },
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(Spacing.small),
        )
    }
}

@Composable
@Preview
fun SignUpScreenPreview() {
    ZeraTheme {
        SignUpScreen()
    }
}
