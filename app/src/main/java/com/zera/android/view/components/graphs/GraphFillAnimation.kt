package com.zera.android.view.components.graphs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import com.zera.android.view.transition.ReduceAnimationsManager

private const val GraphFillDurationMillis = 800

/**
 * Preenchimento animado de um gráfico: sobe de `0f` até [target] quando o gráfico entra na
 * composição, e vai até o novo valor se [target] mudar. Fica parado em [target] com
 * "Reduzir animações" ligado e nas `@Preview`, que desenham um quadro só.
 *
 * @param target fração final do preenchimento, de `0f` a `1f`.
 */
@Composable
internal fun rememberGraphFill(target: Float): State<Float> {
    val animate = !ReduceAnimationsManager.enabled && !LocalInspectionMode.current
    val fill = remember { Animatable(if (animate) 0f else target) }

    LaunchedEffect(target, animate) {
        if (animate) {
            fill.animateTo(target, tween(GraphFillDurationMillis, easing = FastOutSlowInEasing))
        } else {
            fill.snapTo(target)
        }
    }
    return fill.asState()
}
