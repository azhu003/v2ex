package com.azhu.basic

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.azhu.basic.provider.logger

/**
 * @author: azhu
 * @date: 2024-10-02 21:02
 * @version: 1.0.0
 */
open class DefaultActivityLifecycleCallbacks : ActivityLifecycleCallbacks {
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        logger.i("onCreated ${p0.javaClass.name}")
    }

    override fun onActivityStarted(p0: Activity) {
        logger.i("onStarted ${p0.javaClass.name}")
    }

    override fun onActivityResumed(p0: Activity) {
        logger.i("onResumed ${p0.javaClass.name}")
    }

    override fun onActivityPaused(p0: Activity) {
        logger.i("onPaused ${p0.javaClass.name}")
    }

    override fun onActivityStopped(p0: Activity) {
        logger.i("onStopped ${p0.javaClass.name}")
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        logger.i("onSaveInstanceState ${p0.javaClass.name}")
    }

    override fun onActivityDestroyed(p0: Activity) {
        logger.i("onDestroyed ${p0.javaClass.name}")
    }
}