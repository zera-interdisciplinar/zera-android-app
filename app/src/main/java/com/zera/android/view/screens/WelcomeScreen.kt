package com.zera.android.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonStyle
import com.zera.android.view.components.logo.Logo
import com.zera.android.view.components.outros.SplashBackground
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

@Composable
fun WelcomeScreen(){
    SplashBackground()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Logo(Modifier.width(250.dp).padding(bottom = Spacing.small))
            TitleText(text = "Bem vindo!", bold = true)
            Spacer(Modifier.height(Spacing.huge))
            ZeraButton(
                text = "Login",
                style = ZeraButtonStyle.Yellow,
                onClick = {

                }
            )
            Spacer(Modifier.height(Spacing.medium))
            LabelText(
                text = "Primeiro Acesso",
                underline = true,
                bold = true,
                onClick = {

                }
            )
        }
    }

}

@Composable
@Preview
fun WelcomeScreenPreview(){
    ZeraTheme() {
        WelcomeScreen()
    }
}