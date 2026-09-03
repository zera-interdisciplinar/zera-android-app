package com.zera.android.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.logo.Logo
import com.zera.android.view.components.outros.SplashBackground
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

@Composable
fun LoginScreen() {
    SplashBackground()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = Spacing.xLarge)
        ) {
            Logo(Modifier.width(250.dp).padding(bottom = Spacing.small))
            TitleText(text = "Bem vindo!", bold = true)
            Spacer(Modifier.height(Spacing.medium))
            ZeraTextInput(
                label = "Email",
                placeholder = "Seu email",
                onValueChange = {},
                modifier = Modifier.padding(vertical = Spacing.medium)
            )
            ZeraTextInput(
                label = "Email",
                placeholder = "Seu email",
                onValueChange = {},
                modifier = Modifier.padding(top = Spacing.medium)
            )
            CaptionText(
                text = "Esqueci minha senha",
                underline = true,
                onClick = {},
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth().padding(Spacing.micro),
            )
            Spacer(Modifier.height(Spacing.xLarge))
            ZeraButton(
                text = "Entrar",
                onClick = {

                }
            )
            CaptionText(
                text = "Não tenho cadastro",
                underline = true,
                onClick = {

                }
            )

        }
    }
}

@Composable
@Preview
fun LoginScreenPreview(){
    ZeraTheme() {
        LoginScreen()
    }
}