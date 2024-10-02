package com.azhu.v2ex.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * @description:
 * @author: azhu
 * @date: 2024-09-29 22:23
 * @version: 1.0.0
 */
open class BaseViewModel : ViewModel() {

    val title = mutableStateOf(value = "")

    fun toast(context: Context, message: String?) {
        if (message.isNullOrBlank()) {
            return
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}