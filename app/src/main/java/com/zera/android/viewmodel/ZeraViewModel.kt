package com.zera.android.viewmodel

import androidx.lifecycle.ViewModel
import com.zera.android.config.Environments
import com.zera.android.config.SetupApp

// base view model for after-splash screens
// it loads the environments variable to be used through the app
open class ZeraViewModel(
    private val environmentsOverride: Environments? = null,
) : ViewModel() {
    protected val environments: Environments
        get() = environmentsOverride ?: SetupApp.environments
}
