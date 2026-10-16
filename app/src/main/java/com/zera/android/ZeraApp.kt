package com.zera.android

import android.app.Application
import com.zera.android.model.config.AppConfig
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.usecase.config.Boot

class ZeraApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SqliteManager.init(this)
        AppConfig.loadEnvironments()?.let(Boot::apply)
    }
}
