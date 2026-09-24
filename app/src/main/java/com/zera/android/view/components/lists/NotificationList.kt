package com.zera.android.view.components.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.cards.Notification
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme

/**
 * Dado de uma notificação exibida em [NotificationList].
 *
 * @param id identificador da notificação, usado como chave de recomposição da lista.
 * @param label texto em destaque do card.
 * @param text texto de apoio exibido abaixo do [label].
 * @param style família de cor do card. Ver [ZeraColorFamily].
 */
data class NotificationItem(
    val id: String,
    val label: String,
    val text: String? = null,
    val style: ZeraColorFamily = ZeraColorFamily.Yellow,
)

/**
 * Lista rolável de notificações: recebe os dados em [notifications] e renderiza um
 * [Notification] para cada um.
 *
 * A lista ocupa apenas a altura do seu conteúdo. Quando for colocada dentro de um
 * container com rolagem vertical, limite a altura pelo [modifier]
 * (ex.: `Modifier.heightIn(max = 400.dp)`).
 *
 * @param notifications notificações a exibir, na ordem em que devem aparecer.
 * @param modifier modificador externo opcional.
 * @param onItemClick ação de "Resolver agora" de cada card, recebendo a
 *   [NotificationItem] clicada. Quando `null` (padrão), a ação não é exibida.
 * @param contentPadding espaçamento interno entre a borda da lista e os cards.
 * @param emptyContent conteúdo exibido quando [notifications] está vazio. Por padrão,
 *   uma mensagem de texto simples.
 */
@Composable
fun NotificationList(
    notifications: List<NotificationItem>,
    modifier: Modifier = Modifier,
    onItemClick: ((NotificationItem) -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(Spacing.medium),
    emptyContent: @Composable () -> Unit = { BodyText(text = "Nenhuma notificação") },
) {
    if (notifications.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(contentPadding),
            contentAlignment = Alignment.Center,
        ) {
            emptyContent()
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        items(notifications, key = { it.id }) { notification ->
            Notification(
                label = notification.label,
                text = notification.text,
                style = notification.style,
                redirect = onItemClick?.let { { it(notification) } },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationListPreview() {
    ZeraTheme {
        NotificationList(
            notifications = listOf(
                NotificationItem(
                    id = "1",
                    label = "5 produtos sem classificação",
                    text = "Aguardando revisão do gestor",
                    style = ZeraColorFamily.Yellow,
                ),
                NotificationItem(
                    id = "2",
                    label = "Material reciclável em rota incorreta",
                    text = "Verifique a ocorrência registrada",
                    style = ZeraColorFamily.Red,
                ),
            ),
            onItemClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationListEmptyPreview() {
    ZeraTheme {
        NotificationList(
            notifications = emptyList(),
        )
    }
}
