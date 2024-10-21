package com.azhu.basic.provider

import android.app.Application
import com.tencent.mmkv.MMKV

object StoreProvider {

    private lateinit var instance: MMKV

    fun init(application: Application) {
        MMKV.initialize(application)
        instance = MMKV.defaultMMKV()
    }

    fun save(key: String, value: String) {
        instance.encode(key, value)
    }

    fun save(key: String, value: Boolean) {
        instance.encode(key, value)
    }

    fun getString(key: String): String? {
        return instance.decodeString(key)
    }

    fun getBool(key: String): Boolean {
        return instance.decodeBool(key)
    }

    fun remove(key: String) = instance.remove(key)
}
