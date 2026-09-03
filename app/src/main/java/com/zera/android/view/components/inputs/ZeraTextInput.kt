package com.zera.android.view.components.inputs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.ZeraTheme

@Composable
fun ZeraTextInput(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = ""
){
    Column() {
        LabelText(label)
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                BodyText(placeholder, alpha = 0.5f)
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                disabledIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
            ),
            modifier = modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = false)
@Composable
fun ZeraTextInputPreview(){
    ZeraTheme() {
        ZeraTextInput(label = "Nome", placeholder = "Hello World", onValueChange = {})
    }
}