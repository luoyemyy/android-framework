package com.github.luoyemyy.framework.app

import android.app.Application
import com.github.luoyemyy.framework.manager.FileManager

object AppInfo {

    lateinit var packageName: String
    lateinit var preferencesName: String
    var logPath: String? = null

    fun init(app: Application, enableLog: Boolean = true, spfName: String? = null) {
        AppError.init(app)
        packageName = app.packageName
        preferencesName = spfName ?: "app_info"
        logPath = FileManager.getInstance(app).inner().dir(FileManager.LOG)?.absolutePath

        Logger.setEnableLog(enableLog)
    }
}
