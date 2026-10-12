package com.zera.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import com.zera.android.model.local.SqliteManager
import com.zera.android.view.navigation.PendingAlertDeepLink
import com.zera.android.view.navigation.ZeraNavHost
import com.zera.android.view.navigation.consumePendingAlertDeepLink
import com.zera.android.view.theme.ZeraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SqliteManager.init(this)
        PendingAlertDeepLink.stash(intent)
        requestNotificationPermission()
        enableEdgeToEdge()
        setContent {
            ZeraTheme {
                ZeraNavHost()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        PendingAlertDeepLink.stash(intent)
        consumePendingAlertDeepLink()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            0,
        )
    }
}
