package com.zera.android.view.components.inputs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.theme.palette

/** Lado da caixa de seleção do [SelectBoxItem]. */
private val ItemCheckboxSize = 32.dp

/** Espessura da borda do card e da caixa de seleção. */
private val BorderWidth = 1.dp

/** Proporção do ícone de "check" em relação ao lado da caixa de seleção. */
private const val CheckIconRatio = 0.6f

/**
 * Opção de seleção múltipla em formato de card: caixa de seleção à esquerda, nome
 * em destaque e, opcionalmente, uma descrição pequena logo abaixo.
 *
 * Quando [selected], o card ganha borda na cor base de [style] e a caixa é
 * preenchida com um "check"; caso contrário, o card fica sem destaque e a caixa
 * vazia. Quando [description] é `null`, o [name] fica centralizado
 * verticalmente no card.
 *
 * Usado para compor listas (ver [SelectBoxList]) — este composable só desenha um
 * item. Como os demais inputs, é controlado: não guarda o estado de seleção,
 * apenas reporta o toque em [onClick].
 *
 * @param name texto principal da opção.
 * @param selected se a opção está selecionada (estado controlado pelo chamador).
 * @param onClick chamado ao tocar no card, para o chamador alternar a seleção.
 * @param modifier modificador externo opcional. O card já ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 * @param description texto pequeno exibido abaixo do [name]. Omitido quando `null`.
 * @param style família de cor usada para destacar o estado selecionado. Ver [ZeraColorFamily].
 */
@Composable
fun SelectBoxItem(
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    style: ZeraColorFamily = ZeraColorFamily.Blue,
) {
    val borderColor = if (selected) style.palette().base else MaterialTheme.colorScheme.outline

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                role = Role.Checkbox
                toggleableState = ToggleableState(selected)
            },
        shape = RoundedCornerShape(Radius.large),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(BorderWidth, borderColor),
    ) {
        Row(
            modifier = Modifier.padding(Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SelectCheckbox(
                checked = selected,
                size = ItemCheckboxSize,
                radius = Radius.medium,
                style = style,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.micro),
            ) {
                BodyText(text = name, bold = true)
                if (description != null) {
                    CaptionText(text = description)
                }
            }
        }
    }
}

/**
 * Caixa de seleção quadrada, só visual (o toque é tratado por quem a contém):
 * vazia com borda neutra quando desmarcada, e preenchida com a cor base de
 * [style] com um "check" quando [checked]. Compartilhada pelo [SelectBoxItem]
 * e pela linha "Selecionar todos" do [SelectBoxList].
 */
@Composable
internal fun SelectCheckbox(
    checked: Boolean,
    size: Dp,
    radius: Dp,
    style: ZeraColorFamily,
    modifier: Modifier = Modifier,
) {
    val palette = style.palette()

    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(radius),
        color = if (checked) palette.base else MaterialTheme.colorScheme.surface,
        border = BorderStroke(BorderWidth, MaterialTheme.colorScheme.outline),
    ) {
        if (checked) {
            Box(contentAlignment = Alignment.Center) {
                ZeraIcon(
                    icon = ZeraIcon.Check,
                    contentDescription = null,
                    size = size * CheckIconRatio,
                    tint = palette.onBase,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectBoxItemPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            SelectBoxItem(
                name = "Notebook Dell",
                description = "ID 842190 · Eletrônico",
                selected = false,
                onClick = {},
            )
            SelectBoxItem(
                name = "Bateria de notebook",
                description = "ID 842191 · Bateria",
                selected = true,
                onClick = {},
            )
            SelectBoxItem(
                name = "Sem descrição (nome centralizado)",
                selected = false,
                onClick = {},
            )
            SelectBoxItem(
                name = "Sem descrição, selecionado",
                selected = true,
                onClick = {},
            )
        }
    }
}
