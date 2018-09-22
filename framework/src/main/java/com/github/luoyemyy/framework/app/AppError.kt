package com.github.luoyemyy.framework.app

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import com.github.luoyemyy.framework.ext.toast
import com.github.luoyemyy.framework.manager.FileManager
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter


internal class AppError private constructor(private val mApp: Application, private val mDefaultHandler: Thread.UncaughtExceptionHandler) : Thread.UncaughtExceptionHandler {

    //用来存储设备信息和异常信息
    private var deviceInfo: String? = null

    override fun uncaughtException(thread: Thread?, ex: Throwable?) {
        if (!handleException(ex)) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex)

            mApp.toast("app error")
            Handler().postDelayed({
                ActivityLifecycleManager.instance.exit()
            }, 3000)
        }
    }

    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        if (deviceInfo == null) {
            //收集设备参数信息
            collectDeviceInfo(mApp)
        }
        //打印和保存日志文件
        val log = collectExceptionInfo(ex)
        FileOutputStream(FileManager.getInstance(mApp).log()).use { it.write(log.toByteArray()) }
        Log.e("AppError", "handleException:  $log")
        return true
    }

    @Suppress("DEPRECATION")
    private fun collectDeviceInfo(context: Context) {
        val stringBuilder = StringBuilder()
        try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(context.packageName, 0)
            stringBuilder.append("versionCode").append("=").append(pi.versionCode).append("\n")
            stringBuilder.append("versionName").append("=").append(pi.versionName).append("\n")

            val fields = Build::class.java.declaredFields
            for (field in fields) {
                field.isAccessible = true
                stringBuilder.append(field.name).append("=").append(field.get(null).toString()).append("\n")
            }
        } catch (e: Exception) {
            Log.e("AppError", "collectDeviceInfo", e)
        }
        deviceInfo = stringBuilder.toString()
    }

    private fun collectExceptionInfo(throwable: Throwable?): String {
        var ex = throwable
        val sb = StringBuilder()
        try {
            StringWriter().use {
                PrintWriter(it).use {
                    do {
                        ex?.printStackTrace(it)
                        ex = ex?.cause
                    } while (ex != null)
                }

                sb.append(deviceInfo)
                sb.append(it.buffer)
            }
        } catch (e: Exception) {
            Log.e("AppError", "collectExceptionInfo", e)
        }
        return sb.toString()
    }

    companion object {

        fun init(app: Application) {
            val appError = AppError(app, Thread.getDefaultUncaughtExceptionHandler())
            Thread.setDefaultUncaughtExceptionHandler(appError)
        }
    }
}