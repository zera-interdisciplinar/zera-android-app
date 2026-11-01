package com.zera.android.viewmodel.error

import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

class FatalErrorViewModel : ZeraViewModel() {

    /** Reinicia o app pela Splash, que refaz o boot e restaura a sessão. */
    fun onConfirmClick() {
        ZeraNavigator.pushAndPopAll(Route.Splash)
    }
}
