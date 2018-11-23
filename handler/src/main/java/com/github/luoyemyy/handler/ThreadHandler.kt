package com.github.luoyemyy.handler

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

class ThreadHandler {

    private var mHandlerThread: HandlerThread = HandlerThread("ThreadHandler")
    private var mHandler: Handler

    init {
        mHandlerThread.start()
        mHandler = Handler(mHandlerThread.looper)
    }

    companion object {

        private val single by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ThreadHandler() }

        fun post(run: () -> Unit) {
            single.mHandler.post(run)
        }

        fun getLooper(): Looper {
            return single.mHandlerThread.looper
        }
    }
}