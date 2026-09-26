package com.zera.android.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.logo.Logo
import com.zera.android.view.components.progressbars.ProgressBar
import com.zera.android.view.components.outros.SplashBackground
import com.zera.android.view.components.texts.SubtitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.transition.SharedElementKeys
import com.zera.android.view.transition.sharedTransition
import com.zera.android.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = viewModel(),
) {
    SplashBackground()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Logo(modifier = Modifier.sharedTransition(SharedElementKeys.Logo).width(250.dp))
            Spacer(Modifier.height(Spacing.medium))
            SubtitleText(
                text = "TRANSFORMANDO DESCARTE EM SOLUÇÃO",
                bold = true
            )
            Spacer(Modifier.height(Spacing.xLarge))
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
