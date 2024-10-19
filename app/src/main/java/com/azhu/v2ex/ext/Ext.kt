package com.azhu.v2ex.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import com.azhu.v2ex.http.ApiException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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

fun <T> Flow<Result<T>>.success(onSuccess: suspend (T) -> Unit): Flow<Result<T>> {
    return onEach {
        if (it.isSuccess) {
            onSuccess.invoke(it.getOrThrow())
        }
    }
}

fun <T> Flow<Result<T>>.error(onError: suspend (Throwable?) -> Unit): Flow<Result<T>> {
    return onEach {
        if (it.isFailure) {
            onError.invoke(it.exceptionOrNull())
        }
    }
}

fun <T> Flow<Result<T>>.complete(onComplete: suspend () -> Unit): Flow<Result<T>> {
    return onEach {
        if (it.isFailure || it.isSuccess) {
            onComplete.invoke()
        }
    }
}

/**
 * @description: 对map函数做一层包装，在Result返回错误时绕过transform函数的执行
 * @author: Jerry
 * @date: 2024/10/13 04:03
 * @version: v1.0
 */
fun <T, R> Flow<Result<T>>.smap(transform: suspend (T) -> Result<R>): Flow<Result<R>> {
    return map {
        if (it.isFailure) {
            Result.failure(it.exceptionOrNull() ?: ApiException("throwable is null"))
        } else {
            val value = it.getOrNull()
            if (value == null) Result.failure(ApiException("result value is null"))
            else transform.invoke(value)
        }
    }
}

fun String.toColor(): Color {
    return Color(android.graphics.Color.parseColor(this))
}