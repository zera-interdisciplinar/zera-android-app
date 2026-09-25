package com.zera.android.view.transition

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

enum class ScreenAnimation {
    Fade,
    SlideHorizontal,
    SlideVertical,
    None;

    /** Tela nova entrando ao ser empilhada. */
    fun enter(): EnterTransition = when (this) {
        Fade -> fadeIn()
        SlideHorizontal -> slideInHorizontally { it }
        SlideVertical -> slideInVertically { it }
        None -> EnterTransition.None
    }

    /** Tela anterior saindo quando a nova é empilhada. */
    fun exit(): ExitTransition = when (this) {
        Fade -> fadeOut()
        SlideHorizontal -> slideOutHorizontally { -it / 3 } + fadeOut()
        SlideVertical -> ExitTransition.None
        None -> ExitTransition.None
    }

    /** Tela anterior voltando quando a empilhada é removida. */
    fun popEnter(): EnterTransition = when (this) {
        Fade -> fadeIn()
        SlideHorizontal -> slideInHorizontally { -it / 3 } + fadeIn()
        SlideVertical -> EnterTransition.None
        None -> EnterTransition.None
    }

    /** Tela empilhada saindo ao ser removida. */
    fun popExit(): ExitTransition = when (this) {
        Fade -> fadeOut()
        SlideHorizontal -> slideOutHorizontally { it }
        SlideVertical -> slideOutVertically { it }
        None -> ExitTransition.None
    }
}
