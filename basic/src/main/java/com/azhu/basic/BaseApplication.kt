package com.azhu.basic

import android.app.Application
import com.azhu.basic.provider.AppThemeProvider
import com.azhu.basic.provider.ContextProvider
import com.azhu.basic.provider.Logger
import com.azhu.basic.provider.StoreProvider

open class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppManager.init(this)
        StoreProvider.init(this)
        AppThemeProvider.init(this)
        ContextProvider.init(this)
        Logger.newInstance(BuildConfig.DEBUG)
    }

    override fun onTerminate() {
        super.onTerminate()
        AppManager.destroy(this)
    }
}