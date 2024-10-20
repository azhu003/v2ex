package com.azhu.basic.provider

import android.app.UiModeManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppThemeProvider {

    private const val KEY_APP_THEME = "keyAppTheme"
    private val defaultTheme = AppTheme.Light
    var appTheme by mutableStateOf(value = defaultTheme)
        private set

    fun init(context: Context) {
        appTheme = getAppThemeOfDefault(context)
        initThemeDelegate(appTheme)
    }

    private fun getAppThemeOfDefault(context: Context): AppTheme {
        var theme = StoreProvider.getString(KEY_APP_THEME)
        if (theme == null) {
            val manager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            theme = if (manager.nightMode == UiModeManager.MODE_NIGHT_YES) {
                AppTheme.Dark.name
            } else {
                AppTheme.Light.name
            }
        }
        return AppTheme.entries.find { it.name == theme } ?: defaultTheme
    }

    fun onAppThemeChanged(theme: AppTheme) {
        StoreProvider.save(KEY_APP_THEME, theme.name)
        initThemeDelegate(theme)
        appTheme = theme
    }

    fun isDark(): Boolean {
        return appTheme == AppTheme.Dark
    }

    private fun initThemeDelegate(appTheme: AppTheme) {
        when (appTheme) {
            AppTheme.Light -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            AppTheme.Dark -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }
}

@Stable
enum class AppTheme {
    Light,
    Dark;
}

fun AppTheme.nextTheme(): AppTheme {
    val values = AppTheme.entries
    return values.getOrElse(ordinal + 1) {
        values[0]
    }
}