    package com.zera.android.view.components.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.inputs.ZeraInputPopup
import com.zera.android.view.components.inputs.ZeraInputType
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import androidx.compose.ui.unit.dp

/** Diâmetro do botão de edição. */
private val EditButtonSize = 32.dp

/**
 * Linha de detalhe de um campo: rótulo pequeno em cima, valor em destaque embaixo,
 * com um botão de editar (lápis) opcional à direita.
 *
 * Usada para compor telas de detalhe (ex.: "Detalhes do Item") a partir de uma
 * lista de campos — diferente de [ProductListItem], que representa um item
 * completo de uma lista de produtos.
 *
 * @param label rótulo curto do campo (ex.: "Categoria").
 * @param value valor atual do campo, exibido em negrito.
 * @param inputType tipo de entrada usado pelo popup de edição.
 * @param validate validação do valor digitado. Retorna a mensagem de erro ou `null`.
 * @param onConfirm ação executada com o novo valor confirmado.
 * @param modifier modificador externo opcional. A linha já ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 * @param onEditClick ação executada ao tocar no lápis de edição. Quando `null`
 *   (padrão), o lápis não é exibido.
 */
@Composable
fun EditableFieldRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null,
    inputType: ZeraInputType = ZeraInputType.Text,
    validate: (String) -> String? = { null },
    onConfirm: (String) -> Unit = {},
) {
    var showInputPopup by remember { mutableStateOf(false) }

    Surface(
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
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.micro),
            ) {
                CaptionText(text = label)
                BodyText(text = value, bold = true)
            }
            if (onEditClick != null) {
                IconButton(
                    icon = ZeraIcon.Edit,
                    onClick = {
                        onEditClick()
                        showInputPopup = true
                    },
                    contentDescription = "Editar $label",
                    type = ZeraButtonType.Tertiary,
                    size = EditButtonSize,
                )
            }
        }
    }

    if (showInputPopup) {
        ZeraInputPopup(
            label = label,
            value = value,
            inputType = inputType,
            onDismissRequest = { showInputPopup = false },
            onConfirm = { newValue ->
                onConfirm(newValue)
                showInputPopup = false
            },
            validate = validate,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditableFieldRowPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            EditableFieldRow(label = "Categoria", value = "Eletrônico", onEditClick = {})
            EditableFieldRow(label = "Material", value = "Metal e Plástico", onEditClick = {})
            EditableFieldRow(label = "Cadastrado Por", value = "Gustavo Macal")
        }
    }
}
