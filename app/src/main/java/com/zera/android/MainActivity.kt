package com.zera.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.core.app.ActivityCompat
import com.zera.android.model.local.SqliteManager
import com.zera.android.view.navigation.PendingAlertDeepLink
import com.zera.android.view.navigation.ZeraNavHost
import com.zera.android.view.navigation.consumePendingAlertDeepLink
import com.zera.android.view.theme.ThemeManager
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.preferences.AppThemeOption

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SqliteManager.init(this)
        ThemeManager.load()
        PendingAlertDeepLink.stash(intent)
        requestNotificationPermission()
        applyEdgeToEdge(darkTheme = ThemeManager.current == AppThemeOption.Dark)
        setContent {
            val darkTheme = ThemeManager.current == AppThemeOption.Dark
            DisposableEffect(darkTheme) {
                applyEdgeToEdge(darkTheme)
                onDispose { }
            }
            ZeraTheme(darkTheme = darkTheme) {
                ZeraNavHost()
            }
        }
    }

    /**
     * Os ícones das barras do sistema seguem o tema do app. No tema claro mantém o
     * comportamento padrão do `enableEdgeToEdge()` (acompanha o modo do sistema).
     */
    private fun applyEdgeToEdge(darkTheme: Boolean) {
        val detectDarkMode = { resources: Resources ->
            darkTheme ||
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                detectDarkMode,
            ),
            navigationBarStyle = SystemBarStyle.auto(
                Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                Color.argb(0x80, 0x1b, 0x1b, 0x1b),
                detectDarkMode,
            ),
        )
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
