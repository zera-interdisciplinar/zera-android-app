package com.zera.android.viewmodel

import androidx.lifecycle.ViewModel
import com.zera.android.model.config.AppConfig
import com.zera.android.model.entity.config.Environments

open class ZeraViewModel(
    private val environmentsOverride: Environments? = null,
) : ViewModel() {
    protected val environments: Environments
        get() = environmentsOverride ?: AppConfig.environments
}
