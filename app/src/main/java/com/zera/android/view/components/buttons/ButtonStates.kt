package com.zera.android.view.components.buttons

sealed interface ButtonStates {
    data object Idle : ButtonStates
    data object Loading : ButtonStates
}