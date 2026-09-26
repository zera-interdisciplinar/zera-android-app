package com.zera.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zera.android.model.usecase.auth.RestoreSession
import com.zera.android.model.usecase.config.Boot
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.time.Duration.Companion.milliseconds


class SplashViewModel : ViewModel() {
    private val boot = Boot()
    private val restoreSession = RestoreSession()

    init {
        viewModelScope.launch {
            val minSplash = async { delay(2_000.milliseconds) }
            boot.execute()
            minSplash.await()
            navigateAfterBoot()
        }
    }

    private suspend fun navigateAfterBoot() {
        if (!restoreSession.hasSavedLogin()) {
            ZeraNavigator.pushAndPop(Route.Welcome)
            return
        }

        try {
            val selfUser = restoreSession.execute()
            val destination = homeRouteForRole(selfUser.role)
            if (destination == null) {
                restoreSession.clearSession()
                ZeraNavigator.pushAndPop(Route.Welcome)
                return
            }
            ZeraNavigator.pushAndPopAll(destination)
        } catch (error: Exception) {
            if (error is HttpException && error.code() in AUTH_FAILURE_CODES) {
                restoreSession.clearSession()
            }
            ZeraNavigator.pushAndPop(Route.Welcome)
        }
    }

    companion object {
        private val AUTH_FAILURE_CODES = listOf(401, 403)
    }
}
