package com.azhu.basic.provider

import android.app.Application

/**
 * @description:
 * @author: azhu
 * @date: 2024-09-30 20:10
 * @version: 1.0.0
 */
object ContextProvider {
    lateinit var context: Application

    fun init(application: Application) {
        context = application
    }
}