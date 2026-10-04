package com.zera.android.view.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.OverlineText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeraDropdownInput(
    values: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    value: String = "",
    label: String = "",
    placeholder: String = "",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    width: Dp? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    val borderColor =
        if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
    val widthModifier =
        if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()

    Column(modifier = modifier.then(widthModifier)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable,
                        enabled = enabled,
                    )
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(Radius.large),
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(Radius.large),
                    )
                    .padding(horizontal = Spacing.medium, vertical = Spacing.small),
            ) {
                if (label.isNotEmpty()) {
                    OverlineText(text = label, bold = true)
                    Spacer(modifier = Modifier.height(Spacing.micro))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BodyText(
                        text = value.ifEmpty { placeholder },
                        modifier = Modifier.weight(1f),
                        alpha = if (value.isEmpty()) 0.5f else 1f,
                    )
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                values.forEach { option ->
                    DropdownMenuItem(
                        text = { BodyText(text = option) },
                        contentPadding = PaddingValues(horizontal = Spacing.medium),
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                        enabled = enabled,
                    )
                }
            }
        }

        if (isError && !errorMessage.isNullOrEmpty()) {
            CaptionText(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = Spacing.small, top = Spacing.small),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ZeraDropdownInputPreview() {
    ZeraTheme {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            ZeraDropdownInput(
                values = listOf("Eletrônico", "Mecânico", "Chip"),
                value = "",
                onValueChange = {},
                label = "Material",
                placeholder = "Selecione uma categoria",
            )
        }
    }
}
