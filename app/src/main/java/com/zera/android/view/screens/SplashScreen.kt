package com.zera.android.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.logo.Logo
import com.zera.android.view.components.progressbars.ProgressBar
import com.zera.android.view.components.outros.SplashBackground
import com.zera.android.view.theme.Typography
import com.zera.android.view.theme.ZeraTheme

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        SplashBackground(modifier = Modifier.fillMaxSize())
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Logo(modifier = Modifier.width(250.dp).padding(16.dp))
            Text(
                text = "TRANSFORMANDO DESCARTE EM SOLUÇÃO",
                style = Typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(32.dp))
            ProgressBar()
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Carregando informações...",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
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
