package com.zera.android.view.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.inputs.ZeraChipsGroup
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Seleção de um ou mais materiais, pensada para o conteúdo de um `BottomSheet`. A seleção
 * é um rascunho do chamador: só vale depois de [onConfirm], e fechar o sheet a descarta.
 *
 * @param options nomes dos materiais do catálogo.
 * @param selected materiais marcados no rascunho.
 * @param onSelectedChange chamado com a lista atualizada a cada toque.
 * @param onConfirm chamado ao tocar em "Confirmar".
 * @param modifier modificador externo opcional.
 */
@Composable
fun SelectMaterialsForm(
    options: List<String>,
    selected: List<String>,
    onSelectedChange: (List<String>) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        TitleText(text = "Selecione os materiais", bold = true)
        ZeraChipsGroup(
            options = options,
            selectedValues = selected,
            onSelectedValuesChange = onSelectedChange,
            selectMany = true,
            stacked = true,
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
        )
        ZeraButton(text = "Confirmar", onClick = onConfirm, fillMaxWidth = true)
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectMaterialsFormPreview() {
    ZeraTheme {
        SelectMaterialsForm(
            options = listOf("Plástico", "Metal", "Vidro", "Papel", "Bateria", "Placa de circuito", "Cabo", "Tela", "Outro"),
            selected = listOf("Plástico", "Bateria"),
            onSelectedChange = {},
            onConfirm = {},
        )
    }
}
