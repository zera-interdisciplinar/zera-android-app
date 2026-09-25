package com.zera.android.view.components.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

/** Diâmetro do botão de fechar (X). */
private val CloseButtonSize = 32.dp

/**
 * Base de um popup modal: container branco arredondado, centralizado, que sobrepõe
 * toda a tela (usa o `Dialog` do Compose, que já traz o scrim e o comportamento de
feat: * fechar ao tocar fora ou apertar voltar). Por padrão tem um botão de fechar (X) fixo no
 * canto superior direito, que pode ser ocultado com [hasDismissButton].
 *
 * Não define nenhum conteúdo próprio — [content] é livre para compor o que for
 * necessário (ex.: [InviteCreatedDialog]). O chamador controla a visibilidade
 * normalmente (ex.: um campo `String?`/`Boolean` no `State` do `ViewModel`, não-nulo/
 * `true` quando deve aparecer) e só compõe o [PopupDialog] com `if (...) { ... }`, já
 * que ele só existe na árvore enquanto estiver visível — mesma convenção do [BottomSheet].
 *
 * @param onDismissRequest chamado quando o usuário toca no X, toca fora do popup ou
 *   aperta voltar.
 * @param modifier modificador externo opcional.
 * @param hasDismissButton quando `true` (padrão), exibe o botão de fechar (X) no canto
 *   superior direito. Tocar fora ou apertar voltar continua fechando o popup.
 * @param contentPadding espaçamento interno entre a borda do popup e o [content].
 * @param content conteúdo desenhado dentro do popup.
 */
@Composable
fun PopupDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    hasDismissButton: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(Spacing.large),
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(Radius.xLarge),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Box {
                if (hasDismissButton) {
                    IconButton(
                        icon = ZeraIcon.Close,
                        onClick = onDismissRequest,
                        contentDescription = "Fechar",
                        type = ZeraButtonType.Tertiary,
                        size = CloseButtonSize,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(Spacing.small),
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(contentPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                    content = content,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PopupDialogPreview() {
    ZeraTheme {
        PopupDialog(onDismissRequest = {}) {
            TitleText(text = "Título do popup", bold = true)
            BodyText(text = "Conteúdo customizável, mesma ideia do BottomSheet.")
        }
    }
}
