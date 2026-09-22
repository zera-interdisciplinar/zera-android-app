package com.zera.android.view.screens.auth

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
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.logo.Logo
import com.zera.android.view.components.outros.SplashBackground
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.transition.SharedElementKeys
import com.zera.android.view.transition.sharedTransition

@Composable
fun WelcomeScreen(){
    SplashBackground()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = Spacing.xLarge)
        ) {
            Logo(Modifier.sharedTransition(SharedElementKeys.Logo).width(250.dp).padding(bottom = Spacing.small))
            TitleText(text = "Bem vindo!", bold = true)
            Spacer(Modifier.height(Spacing.huge))
            Column() {
                ZeraButton(
                    text = "Login",
                    style = ZeraColorFamily.Blue,
                    fillMaxWidth = true,
                    maxWidth = 200,
                    onClick = {
                        ZeraNavigator.push(Route.SignIn)
                    }
                )
                Spacer(Modifier.height(Spacing.small))
                ZeraButton(
                    text = "Primeiro Acesso",
                    style = ZeraColorFamily.Yellow,
                    type = ZeraButtonType.Secondary,
                    fillMaxWidth = true,
                    onClick = {
                        ZeraNavigator.push(Route.SignUp)
                    }
                )
            }
            Spacer(Modifier.height(Spacing.medium))

            // TODO: botões provisórios para teste, remover antes de mergear
            Column {
                ZeraButton(
                    text = "[TESTE] Home Funcionário",
                    style = ZeraColorFamily.Blue,
                    type = ZeraButtonType.Secondary,
                    fillMaxWidth = true,
                    onClick = {
                        ZeraNavigator.push(Route.EmployeeHome)
                    }
                )
                Spacer(Modifier.height(Spacing.small))
                ZeraButton(
                    text = "[TESTE] Home Gestor",
                    style = ZeraColorFamily.Yellow,
                    type = ZeraButtonType.Secondary,
                    fillMaxWidth = true,
                    onClick = {
                        ZeraNavigator.push(Route.ManagerHome)
                    }
                )
            }
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