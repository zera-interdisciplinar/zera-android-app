package com.zera.android.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.logo.Logo
import com.zera.android.view.components.progressbars.ProgressBar
import com.zera.android.view.components.outros.SplashBackground
import com.zera.android.view.components.texts.SubtitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.ZeraTheme
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    LaunchedEffect(Unit) {
        delay(2000)
        ZeraNavigator.pushAndPop(Route.Welcome)
    }
    SplashBackground()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Logo(modifier = Modifier.width(250.dp))
            Spacer(Modifier.height(16.dp))
            SubtitleText(
                text = "TRANSFORMANDO DESCARTE EM SOLUÇÃO",
                bold = true
            )
            Spacer(Modifier.height(32.dp))
            ProgressBar()
        }
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    ZeraTheme {
        SplashScreen()
    }
}
