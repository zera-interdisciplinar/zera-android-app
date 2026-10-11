package com.zera.android.view.components.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.model.entity.chat.MessageRole
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.transition.ScreenAnimation

/**
 * Mensagem individual do chat.
 *
 * O [role] decide o visual: mensagens do usuário ficam à direita, na cor primária; as do
 * assistente ficam à esquerda, sobre fundo branco e acompanhadas do [AssistantAvatar].
 *
 * @param role quem enviou a mensagem. Ver [MessageRole].
 * @param content texto da mensagem.
 * @param modifier modificador externo opcional.
 * @param navigateTo rota de destino de um link ao final da mensagem. Quando `null` (padrão),
 *   o link não é exibido. Quando informada, tocar no link faz um `push` para ela.
 * @param navigateToLabel texto do link exibido quando [navigateTo] não for `null`.
 */
@Composable
fun MessageContainer(
    role: MessageRole,
    content: String,
    modifier: Modifier = Modifier,
    navigateTo: Route? = null,
    navigateToLabel: String = "",
) {
    val isUser = role == MessageRole.User
    val containerColor =
        if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor =
        if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val linkColor =
        if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary

    // O espaço reservado do lado oposto impede a bolha de ocupar a largura toda.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isUser) Spacing.huge else Spacing.none,
                end = if (isUser) Spacing.none else Spacing.huge,
            ),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        if (!isUser) {
            AssistantAvatar(modifier = Modifier.padding(end = Spacing.small))
        }
        Surface(
            modifier = Modifier.weight(1f, fill = false),
            shape = RoundedCornerShape(Radius.large),
            color = containerColor,
            contentColor = contentColor,
        ) {
            Column(
                modifier = Modifier.padding(Spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                BodyText(text = content, color = contentColor)
                if (navigateTo != null) {
                    Row(
                        modifier = Modifier.clickable {
                            ZeraNavigator.push(navigateTo, animation = ScreenAnimation.SlideHorizontal)
                        },
                        horizontalArrangement = Arrangement.spacedBy(Spacing.micro),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LabelText(text = navigateToLabel, bold = true, color = linkColor)
                        ZeraIcon(
                            icon = ZeraIcon.ProceedArrow,
                            contentDescription = null,
                            size = Spacing.medium,
                            tint = linkColor,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MessageContainerPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            MessageContainer(
                role = MessageRole.User,
                content = "Onde descarto uma bateria de notebook?",
            )
            MessageContainer(
                role = MessageRole.Assistant,
                content = "Baterias devem ir para pontos de coleta de eletrônicos ou pilhas. Não descarte no lixo comum.",
                navigateTo = Route.Recycling,
                navigateToLabel = "Ver pontos próximos",
            )
            MessageContainer(
                role = MessageRole.Assistant,
                content = "Olá! Como posso ajudar você hoje?",
            )
        }
    }
}
