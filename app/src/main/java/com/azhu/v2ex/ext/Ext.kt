package com.azhu.v2ex.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import kotlin.reflect.KClass

/**
 * @description:
 * @author: azhu
 * @date: 2024-10-02 00:16
 * @version: 1.0.0
 */
fun <T : Activity> Context.startActivityClass(target: KClass<T>) {
    startActivity(Intent(this, target.java))
}