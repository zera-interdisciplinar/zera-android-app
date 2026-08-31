package com.zera.android.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.theme.ZeraTheme

@Composable
fun ProgressBar(
    modifier: Modifier = Modifier
){
    LinearProgressIndicator(
        trackColor = MaterialTheme.colorScheme.secondary,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
@Preview
fun ProgressBarPreview(){
    ZeraTheme() {
        ProgressBar()
    }
}