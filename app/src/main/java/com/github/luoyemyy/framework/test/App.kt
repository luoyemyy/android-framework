package com.github.luoyemyy.framework.test

import android.app.Application
import com.github.luoyemyy.config.app.AppInfo

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInfo.init(this)
    }
}