package com.zera.android.view.components.inputs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette

/**
 * Grupo de chips: cada string de [options] vira um chip. Por padrão, tocar num
 * deles chama [onSelectedChange]; com [selectMany], alterna a seleção na lista
 * [selectedValues] e chama [onSelectedValuesChange].
 *
 * Assim como [ZeraTextInput], é um componente controlado — não guarda a seleção
 * internamente, apenas reporta o toque. Cabe ao chamador manter [selected] e
 * repassar o novo valor recebido em [onSelectedChange].
 *
 * @param options textos exibidos, um chip por item.
 * @param selected texto do chip atualmente selecionado, ou `null` quando nenhum está.
 * @param onSelectedChange chamado com o texto do chip tocado.
 * @param selectMany quando `true`, permite selecionar mais de um chip.
 * @param selectedValues valores atualmente selecionados no modo múltiplo.
 * @param onSelectedValuesChange chamado com a lista atualizada no modo múltiplo.
 * @param showX quando `true`, mostra um X ao lado da label dos chips selecionados.
 *   Também é ativado automaticamente quando [selectMany] for `true`.
 * @param modifier modificador externo opcional.
 * @param label rótulo exibido acima do grupo. Omitido quando vazio.
 * @param style família de cor usada para destacar o chip selecionado. Ver [ZeraColorFamily].
 * @param stacked quando `true`, os chips quebram linha (via [FlowRow]) conforme
 *   necessário. Quando `false` (padrão), ficam numa única linha rolável
 *   horizontalmente — útil quando não há espaço vertical sobrando.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ZeraChipsGroup(
    options: List<String>,
    selected: String? = null,
    onSelectedChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    label: String = "",
    style: ZeraColorFamily = ZeraColorFamily.Yellow,
    stacked: Boolean = false,
    selectMany: Boolean = false,
    selectedValues: List<String> = emptyList(),
    onSelectedValuesChange: (List<String>) -> Unit = {},
    showX: Boolean = false,
) {
    val showSelectedX = showX || selectMany

    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            LabelText(
                text = label,
                modifier = Modifier.padding(bottom = Spacing.small),
                bold = true,
            )
        }
        if (stacked) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                options.forEach { option ->
                    ZeraChip(
                        text = option,
                        selected = if (selectMany) option in selectedValues else option == selected,
                        style = style,
                        showX = showSelectedX,
                        onClick = {
                            if (selectMany) {
                                val updatedValues = if (option in selectedValues) {
                                    selectedValues - option
                                } else {
                                    selectedValues + option
                                }
                                onSelectedValuesChange(updatedValues)
                            } else {
                                onSelectedChange(option)
                            }
                        },
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                options.forEach { option ->
                    ZeraChip(
                        text = option,
                        selected = if (selectMany) option in selectedValues else option == selected,
                        style = style,
                        showX = showSelectedX,
                        onClick = {
                            if (selectMany) {
                                val updatedValues = if (option in selectedValues) {
                                    selectedValues - option
                                } else {
                                    selectedValues + option
                                }
                                onSelectedValuesChange(updatedValues)
                            } else {
                                onSelectedChange(option)
                            }
                        },
                    )
                }
            }
        }
    }
}

/**
 * Chip individual do [ZeraChipsGroup]: pílula com borda que, quando [selected],
 * assume a cor de [style] (mesmo par borda/container claro do
 * [com.zera.android.view.components.containers.ZeraBox] secundário); caso
 * contrário, usa as cores neutras do tema.
 */
@Composable
private fun ZeraChip(
    text: String,
    selected: Boolean,
    style: ZeraColorFamily,
    showX: Boolean,
    onClick: () -> Unit,
) {
    val palette = style.palette()
    val borderColor = if (selected) palette.base.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (selected) palette.container else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) palette.onContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(Radius.large),
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.medium, vertical = Spacing.small),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LabelText(
                text = text,
                bold = selected,
                color = contentColor,
            )
            if (showX && selected) {
                ZeraIcon(
                    icon = ZeraIcon.Close,
                    contentDescription = null,
                    size = Spacing.small,
                    tint = contentColor,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ZeraChipsGroupPreview() {
    ZeraTheme {
        var danoSelecionado by remember { mutableStateOf<String?>("Peça faltando") }
        var filtroSelecionado by remember { mutableStateOf<String?>("Todos") }
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.large),
        ) {
            ZeraChipsGroup(
                label = "Possui danos?",
                options = listOf("Tela quebrada", "Não liga", "Peça faltando", "Oxidação", "Outro"),
                selected = danoSelecionado,
                onSelectedChange = { danoSelecionado = it },
                stacked = true,
            )
            ZeraChipsGroup(
                options = listOf("Todos", "Pendentes", "Categoria", "Em estoque", "Arquivados"),
                selected = filtroSelecionado,
                onSelectedChange = { filtroSelecionado = it },
                style = ZeraColorFamily.Blue,
            )
        }
    }
}
