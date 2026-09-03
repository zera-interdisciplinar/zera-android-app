package com.zera.android.view.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.ZeraTheme

@Composable
fun WelcomeScreen(){
    TitleText("Hey it's me, identity crisity")
}

@Composable
@Preview
fun WelcomeScreenPreview(){
    ZeraTheme() {
        WelcomeScreen()
    }
}