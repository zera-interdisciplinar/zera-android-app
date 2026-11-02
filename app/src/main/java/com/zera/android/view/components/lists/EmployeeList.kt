package com.zera.android.view.components.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Dado de um colaborador exibido em [EmployeeList].
 *
 * @param id identificador do colaborador, usado como [EmployeeListItem] e como
 *   chave de recomposição da lista.
 * @param name nome do colaborador.
 * @param role cargo do colaborador (ex.: "Operador").
 * @param isPending quando `true`, o [EmployeeListItem] destaca o item como convite
 *   pendente ("Pendente") em vez de colaborador ativo ("Ativo").
 */
data class EmployeeItem(
    val id: String,
    val name: String,
    val role: String,
    val isPending: Boolean = false,
)

/**
 * Lista de colaboradores: recebe os dados em [employees] e renderiza um
 * [EmployeeListItem] para cada um.
 *
 * Não rola sozinha nem é lazy: ocupa a altura de todos os itens, para acompanhar a rolagem
 * da tela. O tamanho da equipe é limitado pela busca (`ListTeamEmployees`).
 *
 * @param employees colaboradores a exibir, na ordem em que devem aparecer.
 * @param onItemClick ação executada ao tocar em um item, recebendo o [EmployeeItem] clicado.
 * @param modifier modificador externo opcional.
 * @param contentPadding espaçamento interno entre a borda da lista e os itens.
 * @param emptyContent conteúdo exibido quando [employees] está vazio. Por padrão,
 *   uma mensagem de texto simples.
 */
@Composable
fun EmployeeList(
    employees: List<EmployeeItem>,
    onItemClick: (EmployeeItem) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Spacing.medium),
    emptyContent: @Composable () -> Unit = { BodyText(text = "Nenhum colaborador encontrado") },
) {
    if (employees.isEmpty()) {
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        employees.forEach { employee ->
            key(employee.id) {
                EmployeeListItem(
                    name = employee.name,
                    role = employee.role,
                    isPending = employee.isPending,
                    onClick = { onItemClick(employee) },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmployeeListPreview() {
    ZeraTheme {
        EmployeeList(
            employees = listOf(
                EmployeeItem(id = "1", name = "Caroll the Best", role = "Operador", isPending = true),
                EmployeeItem(id = "2", name = "Kevin Não Jun", role = "Operador"),
                EmployeeItem(id = "3", name = "Artur Sem H", role = "Operador"),
            ),
            onItemClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmployeeListEmptyPreview() {
    ZeraTheme {
        EmployeeList(
            employees = emptyList(),
            onItemClick = {},
        )
    }
}
