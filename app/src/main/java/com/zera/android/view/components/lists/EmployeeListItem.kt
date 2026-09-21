package com.zera.android.view.components.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette

/** Diâmetro do avatar circular. */
private val AvatarSize = 48.dp

/**
 * Item clicável de uma lista de colaboradores: avatar circular à esquerda, nome e
 * cargo/status ao centro, seta de "avançar" à direita.
 *
 * Usado para compor listas (ver [EmployeeList]) — este composable só desenha a linha
 * de um item. Ainda não recebe uma imagem de perfil: o avatar é só um círculo colorido
 * conforme [isPending].
 *
 * @param name nome do colaborador.
 * @param role cargo do colaborador (ex.: "Operador"), combinado com o status
 *   ("Ativo"/"Pendente") na linha abaixo do nome.
 * @param isPending quando `true`, mostra "Pendente" em vez de "Ativo" e destaca o
 *   avatar e o texto de status na cor de convite pendente (ver [ZeraColorFamily.Yellow]).
 * @param onClick ação executada ao tocar no item.
 * @param modifier modificador externo opcional. O item já ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 */
@Composable
fun EmployeeListItem(
    name: String,
    role: String,
    isPending: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pendingPalette = ZeraColorFamily.Yellow.palette()
    val avatarColor = if (isPending) pendingPalette.container else MaterialTheme.colorScheme.surfaceContainerLow
    val statusColor = if (isPending) pendingPalette.base else MaterialTheme.colorScheme.onSurfaceVariant
    val statusWord = if (isPending) "Pendente" else "Ativo"

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.large),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = Modifier.padding(Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(AvatarSize)
                    .background(color = avatarColor, shape = CircleShape),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.micro),
            ) {
                BodyText(text = name, bold = true)
                CaptionText(text = "$role · $statusWord", color = statusColor)
            }
            ZeraIcon(
                icon = ZeraIcon.ProceedArrow,
                contentDescription = null,
                size = Spacing.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmployeeListItemPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            EmployeeListItem(name = "Caroll the Best", role = "Operador", isPending = true, onClick = {})
            EmployeeListItem(name = "Kevin Não Jun", role = "Operador", isPending = false, onClick = {})
        }
    }
}
