package com.zera.android.view.components.buttons

/** Estado de carregamento de uma ação disparada por botão. */
sealed interface ButtonStates {
    /** Nenhuma ação em andamento; o botão responde normalmente ao toque. */
    data object Idle : ButtonStates

    /** Ação em andamento; o botão deve indicar carregamento e/ou ignorar novos toques. */
    data object Loading : ButtonStates
}
