package com.github.luoyemyy.framework.app

import android.util.Log

object Logger {

    private var enableLog: Boolean = false

    fun setEnableLog(enableLog: Boolean) {
        Logger.enableLog = enableLog
    }

    fun e(tag: String, msg: String, e: Throwable? = null) {
        Log.e(tag, msg, e)
        log("E", tag, msg, e)
    }

    fun i(tag: String, msg: String, e: Throwable? = null) {
        Log.i(tag, msg, e)
        log("I", tag, msg, e)
    }

    fun d(tag: String, msg: String, e: Throwable? = null) {
        Log.d(tag, msg, e)
        log("D", tag, msg, e)
    }

    private fun log(level: String, tag: String, msg: String, e: Throwable?) {
        if (enableLog) {
            LogWriter.single().write(e, Thread.currentThread().name, level, tag, msg)
        }
    }
}
