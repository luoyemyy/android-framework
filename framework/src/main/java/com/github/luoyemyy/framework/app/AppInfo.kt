package com.github.luoyemyy.framework.app

import android.app.Application
import com.github.luoyemyy.framework.logger.Logger
import com.github.luoyemyy.framework.file.FileManager

object AppInfo {

    lateinit var packageName: String
    lateinit var preferencesName: String
    var profile: Profile = Profile(Profile.DEV)

    fun init(app: Application, enableConsoleLog: Boolean = true, enableFileLog: Boolean = true, spfName: String? = null, profileType: Int = Profile.DEV) {
        app.registerActivityLifecycleCallbacks(ActivityLifecycleAdapter.instance)

        FileManager.initManager(app)
        AppError.init(app)
        packageName = app.packageName
        preferencesName = spfName ?: "app_info"
        profile.setNewType(profileType)

        Logger.enableConsoleLog = enableConsoleLog
        Logger.enableFileLog = enableFileLog
        Logger.logPath = FileManager.getInstance().inner().dir(FileManager.LOG)?.absolutePath
    }
}
