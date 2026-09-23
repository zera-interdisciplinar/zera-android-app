package com.zera.android.view.components.graphs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

@Composable
fun CircularGraph(
    progress: () -> Float,
    label: String? = null,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        Box(
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(size = 84.dp),
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Butt,
                color = color,
                trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                gapSize = 0.dp
            )
            TitleText("${(progress()*100).toInt()}%", bold = true)
        }
        if (label != null) {
            LabelText(label, bold = true)
        }
    }
}

@Preview(showBackground = false)
@Composable
fun CircularGraphPreview(){
    ZeraTheme() {
        CircularGraph(
            progress = {0.75f},
            label = "Meta",
        )
    }
}
