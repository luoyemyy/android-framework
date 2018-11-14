package com.github.luoyemyy.logger

import android.os.AsyncTask
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

internal class LogWriter private constructor() {

    private val mWriterHandler: Handler

    init {
        val handlerThread = HandlerThread("LogWriter")
        handlerThread.start()
        mWriterHandler = Handler(handlerThread.looper)
    }

    companion object {

        private val mWriter: LogWriter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { LogWriter() }

        fun single(): LogWriter {
            return mWriter
        }
    }

    fun write(throwable: Throwable?, threadName: String, level: String, tag: String, msg: String, path: String) {
        AsyncTask.execute {
            val logDateTime = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(Date())
            val logInfo = try {
                StringWriter().use { sw ->
                    PrintWriter(sw, true).use { writer ->
                        writer.println()
                        writer.println("$logDateTime [$threadName]-$level/$tag:$msg")
                        var e = throwable
                        while (e != null) {
                            e.printStackTrace(writer)
                            e = e.cause
                        }
                    }
                    sw
                }.toString()
            } catch (e: Throwable) {
                Log.e("LogWriter", "AsyncTask.execute:  ")
                null
            } ?: return@execute

            val logFileName = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            mWriterHandler.post {
                try {
                    FileWriter("$path$logFileName.log.txt", true).write(logInfo)
                } catch (e: Throwable) {
                    Log.e("LogWriter", "write:  ")
                }
            }
        }
    }
}


