package com.zera.android.view.components.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DrawerDefaults.scrimColor
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.outros.SplashBackground
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

private val SheetShape = RoundedCornerShape(topStart = Radius.xLarge, topEnd = Radius.xLarge)

/**
 * Base de um bottom sheet modal: container que sobe da parte inferior da tela,
 * já com o *drag handle*, o formato dos cantos e o espaçamento interno padrão
 * resolvidos.
 *
 * Não define nenhum conteúdo próprio — [content] é livre para compor o que for
 * necessário (formulário, lista de opções, resumo de edição, ...), como nas telas
 * "Quais danos o item possui?" e "Editar produto". O chamador controla a
 * visibilidade normalmente (ex.: `var showSheet by remember { mutableStateOf(false) }`
 * e só compor o [BottomSheet] com `if (showSheet) { ... }`), já que o sheet só
 * existe na árvore enquanto estiver visível.
 *
 * @param onDismissRequest chamado quando o usuário arrasta o sheet para fechar,
 * toca fora dele ou aperta voltar.
 * @param modifier modificador externo opcional.
 * @param sheetState estado do sheet (posição, se está expandido/parcialmente expandido).
 * @param scrimColor cor do overlay atrás do sheet, escurecendo o restante da tela
 * enquanto ele está aberto; clicar nessa área também dispara [onDismissRequest].
 * @param contentPadding espaçamento interno entre a borda do sheet e o [content].
 * @param content conteúdo desenhado dentro do sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    contentPadding: PaddingValues = PaddingValues(Spacing.large),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = SheetShape,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = scrimColor,
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            content = content,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview()
@Composable
private fun BottomSheetPreview() {
    ZeraTheme {
        BottomSheet(
            onDismissRequest = {},
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            TitleText(text = "Quais danos o item possui?")
            BodyText(text = "Conteúdo customizável: formulário, lista de opções, resumo de edição...")
            ZeraButton(text = "Confirmar danos", onClick = {}, fillMaxWidth = true)
        }
    }
}
