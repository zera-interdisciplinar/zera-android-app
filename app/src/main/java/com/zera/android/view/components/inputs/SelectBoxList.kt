package com.zera.android.view.components.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import kotlinx.coroutines.flow.distinctUntilChanged

/** Lado da caixa de seleção da linha "Selecionar todos". */
private val SelectAllCheckboxSize = 16.dp

/**
 * Dado de uma opção exibida em [SelectBoxList].
 *
 * @param name texto principal da opção (nome em destaque).
 * @param value valor que a opção carrega, devolvido no conjunto `selected` de [SelectBoxList]
 *   quando ela é selecionada. Também é a chave de recomposição da lista, então precisa ser
 *   único entre as opções de uma mesma lista.
 * @param description texto pequeno exibido abaixo do [name]. Quando `null`, o
 *   [SelectBoxItem] mostra só o nome, centralizado.
 */
data class SelectOption(
    val name: String,
    val value: String,
    val description: String? = null,
)

/**
 * Lista rolável de opções de seleção múltipla: renderiza um [SelectBoxItem] para
 * cada [SelectOption] de [options].
 *
 * Assim como [ZeraChipsGroup], é um componente controlado — não guarda a
 * seleção internamente. Cabe ao chamador manter [selected] (o conjunto de
 * `value`s selecionados) e atualizá-lo com o novo conjunto recebido em
 * [onSelectedChange].
 *
 * A lista ocupa apenas a altura do seu conteúdo, até o limite imposto pelo
 * [modifier]. Dentro de um container com rolagem vertical, limite a altura
 * (ex.: `Modifier.heightIn(max = 400.dp)`) ou use um container de altura fixa/`weight`.
 *
 * @param options opções a exibir, na ordem em que devem aparecer.
 * @param selected `value`s das opções atualmente selecionadas.
 * @param onSelectedChange chamado com o novo conjunto de `value`s selecionados a
 *   cada toque em uma opção (ou na linha "Selecionar todos").
 * @param modifier modificador externo opcional.
 * @param hasSelectAll quando `true`, exibe acima da lista uma linha "Selecionar
 *   todos" com uma caixa que marca/desmarca todas as [options] de uma vez. Marcada
 *   automaticamente quando todas as opções estão selecionadas. Não aparece com a
 *   lista vazia. Com paginação, age só sobre as [options] já carregadas: quando uma
 *   nova página chega, a caixa volta a ficar desmarcada até as novas opções serem selecionadas.
 * @param selectAllLabel texto da linha "Selecionar todos". Só tem efeito com [hasSelectAll].
 * @param style família de cor usada para destacar as opções selecionadas. Ver [ZeraColorFamily].
 * @param contentPadding espaçamento interno entre a borda da lista e os itens.
 * @param emptyContent conteúdo exibido quando [options] está vazio. Por padrão,
 *   uma mensagem de texto simples.
 * @param onEndReached chamado ao chegar perto do fim da lista, para carregar a próxima
 *   página (mesmo gatilho do [com.zera.android.view.components.lists.ProductList]).
 * @param isLoadingMore quando verdadeiro, exibe um indicador no rodapé da lista e
 *   suspende novas chamadas de [onEndReached].
 */
@Composable
fun SelectBoxList(
    options: List<SelectOption>,
    selected: Set<String>,
    onSelectedChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    hasSelectAll: Boolean = false,
    selectAllLabel: String = "Selecionar Todos",
    style: ZeraColorFamily = ZeraColorFamily.Blue,
    contentPadding: PaddingValues = PaddingValues(vertical = Spacing.small),
    emptyContent: @Composable () -> Unit = { BodyText(text = "Nenhuma opção disponível") },
    onEndReached: () -> Unit = {},
    isLoadingMore: Boolean = false,
) {
    if (options.isEmpty()) {
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

    val allSelected = options.all { it.value in selected }

    val listState = rememberLazyListState()
    LaunchedEffect(listState, options.size, isLoadingMore) {
        if (isLoadingMore) return@LaunchedEffect
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: return@snapshotFlow false
            val total = info.totalItemsCount
            total > 0 && lastVisible >= total - 4
        }
            .distinctUntilChanged()
            .collect { nearEnd ->
                if (nearEnd) onEndReached()
            }
    }

    Column(modifier = modifier) {
        if (hasSelectAll) {
            SelectAllRow(
                label = selectAllLabel,
                checked = allSelected,
                style = style,
                onCheckedChange = { checked ->
                    val values = options.map { it.value }
                    onSelectedChange(if (checked) selected + values else selected - values.toSet())
                },
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            items(options, key = { it.value }) { option ->
                SelectBoxItem(
                    name = option.name,
                    description = option.description,
                    selected = option.value in selected,
                    style = style,
                    onClick = {
                        onSelectedChange(
                            if (option.value in selected) selected - option.value else selected + option.value,
                        )
                    },
                )
            }
            if (isLoadingMore) {
                item(key = "loading-more") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.small),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

/** Linha "Selecionar todos": rótulo + caixa pequena, ambos na mesma área de toque. */
@Composable
private fun SelectAllRow(
    label: String,
    checked: Boolean,
    style: ZeraColorFamily,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange)
            .padding(vertical = Spacing.small),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LabelText(text = label)
        SelectCheckbox(
            checked = checked,
            size = SelectAllCheckboxSize,
            radius = Radius.small,
            style = style,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectBoxListPreview() {
    ZeraTheme {
        var selected by remember { mutableStateOf(setOf("842191")) }
        SelectBoxList(
            modifier = Modifier.padding(Spacing.medium),
            options = listOf(
                SelectOption(name = "Notebook Dell", value = "842190", description = "ID 842190 · Eletrônico"),
                SelectOption(name = "Bateria de notebook", value = "842191", description = "ID 842191 · Bateria"),
                SelectOption(name = "Teclado mecânico", value = "123456", description = "ID 123456 · Periférico"),
                SelectOption(name = "Item sem descrição", value = "000999"),
            ),
            selected = selected,
            onSelectedChange = { selected = it },
            hasSelectAll = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectBoxListEmptyPreview() {
    ZeraTheme {
        SelectBoxList(
            options = emptyList(),
            selected = emptySet(),
            onSelectedChange = {},
            hasSelectAll = true,
        )
    }
}
