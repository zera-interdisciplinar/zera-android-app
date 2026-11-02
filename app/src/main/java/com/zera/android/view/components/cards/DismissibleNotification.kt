package com.zera.android.view.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/** Maior que a animação de saída da lista: até lá, o card ainda pode estar na tela. */
private val SwipeResetDelay = 1.seconds

/**
 * [Notification] que pode ser dispensado deslizando para qualquer lado ou tocando no X.
 *
 * Quem chama remove o item da lista em [onDismiss]. Se o card continuar na tela, como quando a
 * dispensa falha, o gesto é desfeito sozinho para ele não ficar fora do lugar.
 *
 * @param label texto em destaque no topo do card.
 * @param onDismiss ação executada ao completar o deslize ou tocar no X.
 * @param modifier modificador externo opcional.
 * @param style família de cor da notificação. Ver [ZeraColorFamily].
 * @param text texto de apoio exibido abaixo do [label].
 * @param redirect ação exibida ao final do card. Quando `null` (padrão), não é exibida.
 * @param redirectLabel texto da ação de [redirect].
 * @param neutral quando `true`, usa o visual neutro do [Notification], ignorando [style].
 */
@Composable
fun DismissibleNotification(
    label: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    style: ZeraColorFamily = ZeraColorFamily.Yellow,
    text: String? = null,
    redirect: (() -> Unit)? = null,
    redirectLabel: String = "Resolver agora",
    neutral: Boolean = false,
) {
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val swipeState = rememberSwipeToDismissBoxState()

    LaunchedEffect(swipeState.currentValue) {
        if (swipeState.currentValue == SwipeToDismissBoxValue.Settled) return@LaunchedEffect
        currentOnDismiss()
        delay(SwipeResetDelay)
        swipeState.reset()
    }

    SwipeToDismissBox(
        state = swipeState,
        modifier = modifier,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(Radius.xLarge),
                    )
                    .padding(horizontal = Spacing.medium),
                contentAlignment = if (swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                    Alignment.CenterEnd
                } else {
                    Alignment.CenterStart
                },
            ) {
                ZeraIcon(
                    icon = ZeraIcon.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    ) {
        Notification(
            label = label,
            style = style,
            text = text,
            redirect = redirect,
            redirectLabel = redirectLabel,
            neutral = neutral,
            onDismiss = onDismiss,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DismissibleNotificationPreview() {
    ZeraTheme {
        DismissibleNotification(
            label = "Garantia vencendo: Notebook",
            onDismiss = {},
            redirect = {},
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
