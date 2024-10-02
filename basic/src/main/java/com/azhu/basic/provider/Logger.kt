package com.azhu.basic.provider

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel

/**
 * @description:
 * @author: azhu
 * @date: 2024-10-01 14:20
 * @version: 1.0.0
 */
class Logger(private val on: Boolean) {

    companion object {
        lateinit var instance: Logger

        fun newInstance(on: Boolean) {
            instance = Logger(on)
        }
    }

    private val TAG = "V2ex"

    fun debug(msg: String) {
        if (on)
            Log.d(TAG, msg)
    }

    fun info(msg: String) {
        if (on)
            Log.i(TAG, msg)
    }

    fun error(msg: String) {
        if (on)
            Log.e(TAG, msg)
    }

    fun warning(msg: String) {
        if (on)
            Log.w(TAG, msg)
    }
}

val logger: Logger get() = Logger.instance