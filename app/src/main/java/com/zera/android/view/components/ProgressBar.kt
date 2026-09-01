package com.zera.android.view.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.theme.ZeraTheme

/**
 * Barra de progresso linear do app.
 *
 * @param progress quando `null`, exibe um indicador indeterminado; caso contrário,
 * mostra o progresso atual (0f..1f).
 */
@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    progress: (() -> Float)? = null,
) {
    val trackColor = MaterialTheme.colorScheme.secondary
    val color = MaterialTheme.colorScheme.primary

    if (progress == null) {
        LinearProgressIndicator(
            modifier = modifier,
            color = color,
            trackColor = trackColor,
        )
    } else {
        LinearProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = color,
            trackColor = trackColor,
        )
    }
}

@Preview
@Composable
private fun ProgressBarIndeterminatePreview() {
    ZeraTheme {
        ProgressBar(modifier = Modifier.width(200.dp))
    }
}

@Preview
@Composable
private fun ProgressBarDeterminatePreview() {
    ZeraTheme {
        ProgressBar(modifier = Modifier.width(200.dp), progress = { 0.6f })
    }
}
