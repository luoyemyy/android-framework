package com.github.luoyemyy.logger

import android.util.Log

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
