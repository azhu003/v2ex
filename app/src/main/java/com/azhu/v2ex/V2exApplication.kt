package com.azhu.v2ex

import com.azhu.basic.BaseApplication
import com.azhu.v2ex.http.Retrofits

class V2exApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        Retrofits.init(this, "https://www.v2ex.com")
    }
}