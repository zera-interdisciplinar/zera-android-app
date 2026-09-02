package com.zera.android.view.components.buttons

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable

enum class ZeraButtonType {
    Primary,
    Secondary,
}
@Composable
internal fun buttonColors(
    style: ZeraButtonStyle,
    type: ZeraButtonType,
): ButtonColors {
    val palette = style.palette()
    return when (type) {
        ZeraButtonType.Primary -> ButtonDefaults.buttonColors(
            containerColor = palette.base,
            contentColor = palette.onBase,
        )

        ZeraButtonType.Secondary -> ButtonDefaults.buttonColors(
            containerColor = palette.container,
            contentColor = palette.onContainer,
        )
    }
}
