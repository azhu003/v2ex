package com.azhu.basic.provider

import android.app.Application
import com.tencent.mmkv.MMKV

object StoreProvider {

    lateinit var instance: MMKV
        private set

    fun init(application: Application) {
        MMKV.initialize(application)
        instance = MMKV.defaultMMKV()
    }

}
