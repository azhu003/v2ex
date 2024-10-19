package com.azhu.v2ex.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * 使实现[LifecycleObserver]的类可观察生命周期
 * @author: Jerry
 * @date: 2024/10/19 20:31
 * @version: v1.0
 */
@Composable
fun ObserveLifecycleLayout(observer: LifecycleObserver, content: @Composable () -> Unit) {
    observer.ObserveLifecycleEvents(LocalLifecycleOwner.current.lifecycle)
    content()
}

@Composable
private fun LifecycleObserver.ObserveLifecycleEvents(lifecycle: Lifecycle) {
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(this@ObserveLifecycleEvents)
        onDispose {
            lifecycle.removeObserver(this@ObserveLifecycleEvents)
        }
    }
}