package com.github.luoyemyy.framework.logger

import android.os.*
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*

internal class LogWriter private constructor() {

    private var mWriterHandler: WriterHandler

    init {
        val handlerThread = HandlerThread("LogWriter")
        handlerThread.start()
        mWriterHandler = WriterHandler(handlerThread.looper)
    }

    fun write(throwable: Throwable?, threadName: String, level: String, tag: String, msg: String, path: String) {

        val message = mWriterHandler.obtainMessage(1)
        message.obj = throwable
        val bundle = Bundle()
        bundle.putString("thread", threadName)
        bundle.putString("level", level)
        bundle.putString("tag", tag)
        bundle.putString("msg", msg)
        bundle.putString("path", path)
        message.data = bundle

        mWriterHandler.sendMessage(message)

        mWriterHandler.sendEmptyMessageDelayed(2, mFreeTime)
    }

    data class Time(val year: Int, val month: Int, val day: Int, val hour: Int, val minute: Int, val second: Int)

    private inner class WriterHandler internal constructor(looper: Looper) : Handler(looper) {


        private var mDestroyTime: Long = 0

        fun time(): Time {
            val now = Calendar.getInstance()
            return Time(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH),
                    now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND))
        }

        override fun handleMessage(msg: Message) {
            if (msg.what == 2) {
                if (System.currentTimeMillis() > mDestroyTime) {
                    destroy()
                }
                return
            }
            mDestroyTime = System.currentTimeMillis() + mFreeTime

            var throwable: Throwable? = msg.obj as? Throwable
            val bundle = msg.data
            val thread = bundle.getString("thread")
            val level = bundle.getString("level")
            val tag = bundle.getString("tag")
            val text = bundle.getString("msg")
            val path = bundle.getString("path")

            val time = time()
            val logFileName = "${time.year}-${time.month}-${time.day}"
            val logDateTime = "$logFileName-${time.hour}-${time.minute}-${time.second}"


            if (path == null) {
                Log.e("WriterHandler", "handleMessage:  logPath=$path, logFileName=$logFileName, logDateTime=$logDateTime")
                return
            }
            val file = File(path, "$logFileName.log.txt")

            try {
                FileWriter(file, true).use { fileWriter ->
                    PrintWriter(fileWriter, true).use { writer ->
                        writer.println()
                        writer.println("$logDateTime [$thread]-$level/$tag:$text")
                        while (throwable != null) {
                            throwable!!.printStackTrace(writer)
                            throwable = throwable!!.cause
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.e("WriterHandler", "handleMessage:  ", ex)
            }
        }
    }

    companion object {

        private var mWriter: LogWriter? = null
        private const val mFreeTime: Long = 120000//空闲时间 2分钟，如果在这段时间内没有写入日志的请求，则销毁写入日志线程

        private fun destroy() {
            synchronized(LogWriter::class.java) {
                val writer = mWriter
                if (writer != null) {
                    writer.mWriterHandler.looper.quit()
                    mWriter = null
                    Log.i("LogWriter", "销毁写日记线程")
                }
            }
        }

        fun single(): LogWriter {
            if (mWriter == null) {
                synchronized(LogWriter::class.java) {
                    if (mWriter == null) {
                        mWriter = LogWriter()
                    }
                }
            }
            return mWriter!!
        }
    }
}


