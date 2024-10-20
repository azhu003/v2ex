package com.azhu.v2ex.viewmodels

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @author: Jerry
 * @date: 2024-10-19 20:21
 * @version: 1.0.0
 */
open class LifecycleViewModel : BaseViewModel(), DefaultLifecycleObserver {

    private var isInitialized = false

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (!isInitialized) {
            isInitialized = true
            onLazyResume()
        }
    }

    open fun onLazyResume() {}
}