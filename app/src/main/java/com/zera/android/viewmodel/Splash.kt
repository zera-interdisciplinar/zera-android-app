package com.zera.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zera.android.config.SetupApp
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    init {
        viewModelScope.launch {
            val minSplash = async { delay(2_000) }
            SetupApp.setup()
            minSplash.await()
            ZeraNavigator.pushAndPop(Route.Welcome)
        }
    }
}
