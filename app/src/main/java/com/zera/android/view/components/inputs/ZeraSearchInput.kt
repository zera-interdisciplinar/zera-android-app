package com.zera.android.view.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

/**
 * Barra de pesquisa padrão do app: campo de texto com ícone de lupa fixo à
 * esquerda e um botão para limpar o texto quando houver conteúdo digitado.
 *
 * Assim como [ZeraTextInput], é um componente controlado — não guarda o texto
 * internamente, apenas reporta as alterações via [onValueChange].
 *
 * @param value texto atual da busca (estado controlado pelo chamador).
 * @param onValueChange chamado a cada alteração do texto digitado.
 * @param modifier modificador externo opcional.
 * @param placeholder texto de dica exibido quando [value] está vazio.
 * @param onSearch chamado quando o usuário aciona a busca pelo teclado (ação "Buscar").
 * @param width largura fixa. Quando `null`, o campo ocupa toda a largura disponível.
 */
@Composable
fun ZeraSearchInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Pesquisar",
    onSearch: () -> Unit,
    width: Dp? = null,
) {
    val widthModifier =
        if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .then(widthModifier)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(Radius.large),
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = RoundedCornerShape(Radius.large),
            )
            .padding(start = Spacing.medium, end = Spacing.small, top = Spacing.small, bottom = Spacing.small),
    ) {
        ZeraIcon(
            icon = ZeraIcon.MagnifyingGlass,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Spacing.small),
        ) {
            if (value.isEmpty()) {
                BodyText(text = placeholder, alpha = 0.5f)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = true,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search,
                ),
                keyboardActions = KeyboardActions(onSearch = { onSearch?.invoke() }),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (value.isNotEmpty()) {
            IconButton(onClick = { onValueChange("") }) {
                ZeraIcon(
                    icon = ZeraIcon.Close,
                    contentDescription = "Limpar busca",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    size = 16.dp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ZeraSearchInputPreview() {
    ZeraTheme {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            ZeraSearchInput(
                value = "",
                placeholder = "Pesquisar por ID, Nome ou Material...",
                onValueChange = {},
                onSearch = {}
            )
            ZeraSearchInput(
                value = "Placa de vídeo",
                onValueChange = {},
                onSearch = {}
            )
        }
    }
}
