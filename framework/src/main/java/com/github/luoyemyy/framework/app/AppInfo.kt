package com.github.luoyemyy.framework.app

import android.app.Application
import com.github.luoyemyy.framework.manager.FileManager

object AppInfo {

    lateinit var packageName: String
    lateinit var preferencesName: String
    var logPath: String? = null
    var profile: Profile = Profile(Profile.DEV)

    fun init(app: Application, enableConsoleLog: Boolean = true, enableFileLog: Boolean = true, spfName: String? = null, profileType: Int = Profile.DEV) {
        app.registerActivityLifecycleCallbacks(ActivityLifecycleManager.instance)

        FileManager.initManager(app)
        AppError.init(app)
        packageName = app.packageName
        preferencesName = spfName ?: "app_info"
        logPath = FileManager.getInstance().inner().dir(FileManager.LOG)?.absolutePath
        profile.setNewType(profileType)

        Logger.enableConsoleLog = enableConsoleLog
        Logger.enableFileLog = enableFileLog
    }
}
