package com.zera.android.view.components.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.palette

/**
 * Opção liga/desliga em formato de card: nome em destaque e, opcionalmente, uma
 * descrição pequena à esquerda; um switch à direita.
 *
 * Quando [checked], o switch é preenchido com a cor base de [style]; caso contrário,
 * fica com o trilho neutro. Como os demais inputs, é controlado: não guarda o estado,
 * apenas reporta o toque em [onCheckedChange] com o novo valor. O card inteiro é
 * clicável, não só o switch.
 *
 * É a versão "liga/desliga" do [SelectBoxItem], mas não precisa de lista: cada
 * opção é independente.
 *
 * @param name texto principal da opção.
 * @param checked se a opção está ligada (estado controlado pelo chamador).
 * @param onCheckedChange chamado ao tocar no card, com o valor oposto ao de [checked].
 * @param modifier modificador externo opcional. O card já ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 * @param description texto pequeno exibido abaixo do [name]. Omitido quando `null`.
 * @param style família de cor usada para destacar o estado ligado. Ver [ZeraColorFamily].
 */
@Composable
fun SelectSwitchOption(
    name: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    style: ZeraColorFamily = ZeraColorFamily.Green,
) {
    val palette = style.palette()

    Surface(
        onClick = { onCheckedChange(!checked) },
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                role = Role.Switch
                toggleableState = ToggleableState(checked)
            },
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
                BodyText(text = name, bold = true)
                if (description != null) {
                    CaptionText(text = description)
                }
            }
            // Só visual: o toque é tratado pelo card (onCheckedChange = null).
            Switch(
                checked = checked,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = palette.onBase,
                    checkedTrackColor = palette.base,
                    checkedBorderColor = palette.base,
                    uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                    uncheckedTrackColor = MaterialTheme.colorScheme.outline,
                    uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectSwitchOptionPreview() {
    ZeraTheme {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            SelectSwitchOption(
                name = "Alto contraste",
                description = "Melhora a separação de elementos",
                checked = false,
                onCheckedChange = {},
            )
            SelectSwitchOption(
                name = "Reduzir animações",
                description = "Evita movimentos desnecessários",
                checked = true,
                onCheckedChange = {},
            )
            SelectSwitchOption(
                name = "Sem descrição (nome centralizado)",
                checked = true,
                onCheckedChange = {},
            )
        }
    }
}
