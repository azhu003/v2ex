package com.azhu.v2ex

import com.azhu.basic.BaseApplication
import com.azhu.v2ex.http.Retrofits
import com.azhu.v2ex.http.cookie.PersistentCookieStore

class V2exApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        PersistentCookieStore.init(this)
        Retrofits.init(this, "https://www.v2ex.com")
    }
}