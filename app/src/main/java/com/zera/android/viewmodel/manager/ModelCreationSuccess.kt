package com.zera.android.viewmodel.manager

import androidx.lifecycle.viewModelScope
import com.zera.android.model.usecase.auth.GetSelfUser
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.homeRouteForRoleOrManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class ModelCreationSuccessViewModel : ZeraViewModel() {
    private val getSelfUser = GetSelfUser()

    fun onCloseClick() {
        goHome()
    }

    fun onCreateAnotherClick() {
        ZeraNavigator.pushAndPop(Route.ModelCreation, popCount = 2)
    }

    fun onBackToHomeClick() {
        goHome()
    }

    private fun goHome() {
        viewModelScope.launch {
            val destination = try {
                homeRouteForRoleOrManager(getSelfUser.execute().role)
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                homeRouteForRoleOrManager("")
            }
            ZeraNavigator.pushAndPopAll(destination)
        }
    }
}
