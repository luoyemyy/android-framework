package com.github.luoyemyy.framework.app

import android.app.Application
import com.github.luoyemyy.framework.manager.FileManager

object AppInfo {

    lateinit var packageName: String
    lateinit var preferencesName: String
    var logPath: String? = null
    var profile: Profile = Profile(Profile.DEV)

    fun init(app: Application, enableLog: Boolean = true, spfName: String? = null, profileType: Int = Profile.DEV) {
        app.registerActivityLifecycleCallbacks(ActivityLifecycleManager.instance)

        AppError.init(app)
        packageName = app.packageName
        preferencesName = spfName ?: "app_info"
        logPath = FileManager.getInstance(app).inner().dir(FileManager.LOG)?.absolutePath
        profile.setNewType(profileType)

        Logger.setEnableLog(enableLog)
    }
}
