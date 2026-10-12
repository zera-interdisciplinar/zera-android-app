package com.zera.android

import android.app.Application
import com.zera.android.model.local.SqliteManager

class ZeraApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SqliteManager.init(this)
    }
}
