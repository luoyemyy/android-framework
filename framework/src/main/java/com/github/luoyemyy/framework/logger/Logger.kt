package com.github.luoyemyy.framework.logger

import android.util.Log

fun loge(tag: String, message: String, e: Throwable? = null) = Logger.e(tag, message, e)

fun logd(tag: String, message: String, e: Throwable? = null) = Logger.d(tag, message, e)

fun logi(tag: String, message: String, e: Throwable? = null) = Logger.i(tag, message, e)

object Logger {

    var enableFileLog: Boolean = false
    var enableConsoleLog: Boolean = true
    var logPath: String? = null

    fun e(tag: String, msg: String, e: Throwable? = null) {
        if (enableConsoleLog) {
            Log.e(tag, msg, e)
        }
        log("E", tag, msg, e)
    }

    fun i(tag: String, msg: String, e: Throwable? = null) {
        if (enableConsoleLog) {
            Log.i(tag, msg, e)
        }
        log("I", tag, msg, e)
    }

    fun d(tag: String, msg: String, e: Throwable? = null) {
        if (enableConsoleLog) {
            Log.d(tag, msg, e)
        }
        log("D", tag, msg, e)
    }

    private fun log(level: String, tag: String, msg: String, e: Throwable?) {
        val path = logPath ?: let {
            Log.w(tag, "logPath is null")
            return
        }
        if (enableFileLog) {
            LogWriter.single().write(e, Thread.currentThread().name, level, tag, msg, path)
        }
    }
}
