package com.zera.android.view.components.chat

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

private val AvatarIconSize = 20.dp

/**
 * Avatar circular do assistente, exibido ao lado das mensagens de [MessageRole.Assistant]
 * e do [LoadingMessageContainer].
 *
 * @param modifier modificador externo opcional.
 */
@Composable
internal fun AssistantAvatar(modifier: Modifier = Modifier) {
    ZeraBox(
        modifier = modifier,
        style = ZeraColorFamily.Yellow,
        shape = CircleShape,
        contentPadding = PaddingValues(Spacing.small),
    ) {
        ZeraIcon(
            icon = ZeraIcon.Chatbot,
            contentDescription = null,
            size = AvatarIconSize,
            tint = LocalContentColor.current,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AssistantAvatarPreview() {
    ZeraTheme {
        AssistantAvatar(modifier = Modifier)
    }
}
