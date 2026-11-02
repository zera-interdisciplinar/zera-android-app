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
import com.zera.android.view.components.cards.InviteCard
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Dado de um convite pendente exibido em [InviteCard].
 *
 * @param code código do convite, usado também como chave da lista.
 * @param inviteeName nome informado ao criar o convite.
 * @param expiresInHours horas restantes até o convite expirar.
 */
data class InviteItem(
    val code: String,
    val inviteeName: String,
    val expiresInHours: Int,
)

/**
 * Lista rolável de convites pendentes: renderiza um [InviteCard] para cada item de [invites].
 * Rola sozinha, então fica em uma tela não rolável com `Modifier.weight(1f)` ou tamanho fixo.
 *
 * @param invites convites a exibir, na ordem em que devem aparecer.
 * @param onCopyCodeClick ação executada ao tocar em "Copiar código" em um convite.
 * @param modifier modificador externo opcional.
 * @param contentPadding espaçamento interno entre a borda da lista e os itens.
 * @param emptyContent conteúdo exibido quando [invites] está vazio.
 */
@Composable
fun InviteList(
    invites: List<InviteItem>,
    onCopyCodeClick: (InviteItem) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Spacing.medium),
    emptyContent: @Composable () -> Unit = { BodyText(text = "Nenhum convite pendente") },
) {
    if (invites.isEmpty()) {
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
        items(invites, key = { it.code }) { invite ->
            InviteCard(
                code = invite.code,
                name = invite.inviteeName,
                expireTime = invite.expiresInHours,
                onCopyCodeClick = { onCopyCodeClick(invite) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InviteListPreview() {
    ZeraTheme {
        InviteList(
            invites = listOf(
                InviteItem(code = "743731", inviteeName = "Genivaldo da Silva", expiresInHours = 12),
                InviteItem(code = "443729", inviteeName = "Pedro José", expiresInHours = 107),
            ),
            onCopyCodeClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InviteListEmptyPreview() {
    ZeraTheme {
        InviteList(invites = emptyList(), onCopyCodeClick = {})
    }
}
