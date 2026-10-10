package com.zera.android.viewmodel.manager

import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

class CategoryCreationSuccessViewModel : ZeraViewModel() {
    fun onCloseClick() {
        ZeraNavigator.pushAndPopAll(Route.ManagerHome)
    }

    fun onCreateAnotherClick() {
        ZeraNavigator.pushAndPop(Route.CategoryCreation, popCount = 2)
    }

    fun onBackToHomeClick() {
        ZeraNavigator.pushAndPopAll(Route.ManagerHome)
    }
}
