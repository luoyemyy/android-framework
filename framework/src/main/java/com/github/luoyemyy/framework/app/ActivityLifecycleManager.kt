package com.github.luoyemyy.framework.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Process

class ActivityLifecycleManager : Application.ActivityLifecycleCallbacks {

    private val activities = mutableListOf<Activity?>()

    fun exit() {
        activities.forEach {
            it?.finish()
        }
        Process.killProcess(Process.myPid())
    }

    companion object {
        val instance: ActivityLifecycleManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ActivityLifecycleManager()
        }
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        activities.add(activity)
    }

    override fun onActivityDestroyed(activity: Activity?) {
        activities.remove(activity)
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }


}