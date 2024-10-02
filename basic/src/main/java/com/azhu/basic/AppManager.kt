package com.azhu.basic

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * @author: azhu
 * @date: 2024-10-02 20:52
 * @version: 1.0.0
 */
object AppManager {

    private val activitys: LinkedHashMap<String, Activity> = linkedMapOf()
    private var mCurrentActivityReference: WeakReference<Activity>? = null

    private val activityLifecycleCallbacks = object : DefaultActivityLifecycleCallbacks() {
        override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            addActivity(p0)
        }

        override fun onActivityDestroyed(p0: Activity) {
            removeActivity(p0)
        }
    }

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun destroy(application: Application) {
        val iterator = activitys.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val activity = entry.value
            if (!activity.isFinishing && !activity.isDestroyed) {
                activity.finish()
            }
            iterator.remove()
        }
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    private fun addActivity(activity: Activity) {
        activitys[activity.javaClass.simpleName] = activity
        mCurrentActivityReference = WeakReference(activity)
    }

    private fun removeActivity(activity: Activity) {
        activitys.remove(activity.javaClass.simpleName)
        mCurrentActivityReference?.clear()
        if (activitys.isNotEmpty()) {
            mCurrentActivityReference = WeakReference(activitys[activitys.keys.last()])
        }
    }

    fun getCurrentActivity(): Activity? {
        return mCurrentActivityReference?.get()
    }
}