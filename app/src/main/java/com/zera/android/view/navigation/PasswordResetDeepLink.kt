package com.zera.android.view.navigation

import android.content.Intent

object PendingPasswordResetDeepLink {
    @Volatile
    private var token: String? = null

    fun stash(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.scheme != "zera" || uri.host != "reset-password") return

        token = uri.getQueryParameter("token")?.trim().orEmpty()
    }

    fun takeToken(): String? {
        val current = token
        token = null
        return current
    }
}

fun consumePendingPasswordResetDeepLink(): Boolean {
    val token = PendingPasswordResetDeepLink.takeToken() ?: return false
    ZeraNavigator.pushAndPopAll(Route.PasswordRecoveryNewPassword(token))
    return true
}
