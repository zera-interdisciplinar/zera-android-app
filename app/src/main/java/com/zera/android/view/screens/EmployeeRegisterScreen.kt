package com.zera.android.view.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.inputs.ZeraTextInput
import com.zera.android.view.components.inputs.ZeraTokenInput
import com.zera.android.view.components.logo.Logo
import com.zera.android.view.components.outros.SplashBackground
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

@Composable
fun EmployeeRegisterScreen(){
    var token by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    SplashBackground()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.large),
            modifier = Modifier.padding(horizontal = Spacing.xLarge)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                Logo(Modifier.width(250.dp).padding(bottom = Spacing.small))
                TitleText("Cadastrar funcionario", bold = true)
            }
            ZeraTextInput(
                value = name,
                label = "Nome",
                placeholder = "Insira seu nome aqui",
                onValueChange = { name = it }
            )
            ZeraTokenInput(
                value = token,
                onValueChange = { token = it },
                label = "Código de convite",
                onFilled = {},
            )
        }
        Spacer(Modifier.height(Spacing.xLarge))
        Box{
            ZeraButton(
                text = "Entrar",
                onClick = {}
            )
        }

    }
}


@Composable
@Preview
fun EmployeeRegisterScreenPreview(){
    ZeraTheme() {
        EmployeeRegisterScreen()
    }
}