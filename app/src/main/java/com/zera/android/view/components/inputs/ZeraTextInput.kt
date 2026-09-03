package com.zera.android.view.components.inputs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

@Composable
fun ZeraTextInput(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = "",
    width: Dp? = null,
){
    Column(
        modifier = modifier.then(
            if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()
        )
    ) {
        LabelText(label, Modifier.padding(Spacing.small))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                BodyText(placeholder, alpha = 0.5f)
            },
            singleLine = true,
            shape = RoundedCornerShape(Radius.large),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier.then(
                if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()
            )
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