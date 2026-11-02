package com.zera.android.view.components.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

private val ConversationAvatarSize = 40.dp

/**
 * Linha do inbox de conversas.
 *
 * @param preview início da conversa; em branco, mostra "Nova conversa".
 * @param onClick abre a conversa.
 * @param modifier modificador externo opcional.
 * @param timeLabel horário já formatado; omitido quando nulo ou em branco.
 * @param avatar imagem do agente, desenhada sem tint e recortada em círculo — use um
 *   drawable que já traga o próprio fundo (ex.: [ZeraIcon.ChatbotBase]).
 */
@Composable
fun ConversationRow(
    preview: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    timeLabel: String? = null,
    avatar: ZeraIcon = ZeraIcon.ChatbotBase,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = Spacing.medium, vertical = Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            Image(
                painter = avatar.painter(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(ConversationAvatarSize)
                    .clip(CircleShape),
            )
            BodyText(
                text = preview.ifBlank { "Nova conversa" },
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (!timeLabel.isNullOrBlank()) {
                CaptionText(
                    text = timeLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Preview(showBackground = true)
@Composable
private fun ConversationRowPreview() {
    ZeraTheme {
        ConversationRow(
            preview = "Quais foram os resultados dos indicadores...",
            timeLabel = "09:11",
            onClick = {},
        )
    }
}
