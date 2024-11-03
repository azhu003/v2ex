package com.azhu.v2ex.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
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

fun Dp.toPx(): Float {
    return value * Resources.getSystem().displayMetrics.density.toInt()
}

/**
 * LocalContext.current 提供的 Context 有时是 ContextThemeWrapper 类型，
 * 而不是 Activity。这在某些情况下（例如在对话框、弹出菜单或 ModalBottomSheet 中）
 * 尤其常见，因为这些组件包装了一个主题上下文，而不是直接的 Activity。
 * 为了避免这种类型转换错误，可以尝试检查 Context 的类型，确保它是 Activity 实例。
 * 可以通过 Context 的 baseContext 逐层检查，直到找到 Activity。
 */
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}