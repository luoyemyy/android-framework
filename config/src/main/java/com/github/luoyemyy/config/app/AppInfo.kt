package com.github.luoyemyy.config.app

import android.app.Application
import android.os.StrictMode
import com.github.luoyemyy.file.FileManager
import com.github.luoyemyy.logger.AppError
import com.github.luoyemyy.logger.Logger

object AppInfo {

    lateinit var packageName: String
    lateinit var preferencesName: String
    var profile: Profile = Profile(Profile.DEV)

    fun init(app: Application, enableConsoleLog: Boolean = true, enableFileLog: Boolean = true, spfName: String? = null, profileType: Int = Profile.DEV) {

        packageName = app.packageName
        preferencesName = spfName ?: "app_info"
        profile.setNewType(profileType)

        FileManager.initManager(app)
        AppError.init(app)
        Logger.enableConsoleLog = enableConsoleLog
        Logger.enableFileLog = enableFileLog
        Logger.logPath = FileManager.getInstance().inner().dir(FileManager.LOG)?.absolutePath

        StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog()
        StrictMode.VmPolicy.Builder().detectAll().penaltyLog()

    }
}
