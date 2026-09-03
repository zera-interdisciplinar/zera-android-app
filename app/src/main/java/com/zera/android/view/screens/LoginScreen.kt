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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonStyle
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.inputs.ZeraInputType
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.logo.Logo
import com.zera.android.view.components.outros.SplashBackground
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

@Composable
fun LoginScreen() {
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }

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
                value = email,
                onValueChange = { email = it },
                type = ZeraInputType.Email,
                imeAction = ImeAction.Next,
                modifier = Modifier.padding(vertical = Spacing.medium)
            )
            ZeraTextInput(
                label = "Senha",
                placeholder = "Senha aqui",
                value = senha,
                onValueChange = { senha = it },
                type = ZeraInputType.Password,
                imeAction = ImeAction.Done,
                modifier = Modifier.padding(top = Spacing.medium)
            )
            CaptionText(
                text = "Esqueci minha senha",
                underline = true,
                onClick = {},
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth().padding(Spacing.small),
            )
            Spacer(Modifier.height(Spacing.xLarge))
            Column() {
                ZeraButton(
                    text = "Entrar",

                    onClick = {

                    },
                    modifier = Modifier.padding(Spacing.small),
                    fillMaxWidth = true,
                )
            }
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