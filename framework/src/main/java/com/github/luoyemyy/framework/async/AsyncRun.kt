package com.github.luoyemyy.framework.async

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log

class AsyncRun {

    private val mMainHandler: Handler = Handler(Looper.getMainLooper())

    inner class Call<R : Result>(private val error: R) {

        private var back: R = error
        //start
        private var s: () -> Boolean = { true }

        //create
        private lateinit var c: () -> R

        //result
        private var r: (R) -> Unit = {}
        private var ok: (R) -> Unit = {}
        private var fail: (R) -> Unit = {}

        //cancel
        private var cancel: Boolean = false

        fun start(s: () -> Boolean): Call<R> {
            this.s = s
            return this
        }

        fun create(c: () -> R): Call<R> {
            this.c = c
            mMainHandler.post(startRunnable)
            return this
        }

        fun result(r: (R) -> Unit = {}): Call<R> {
            this.r = r
            return this
        }

        fun success(success: (R) -> Unit): Call<R> {
            ok = success
            return this
        }

        fun failure(failure: (R) -> Unit): Call<R> {
            fail = failure
            return this
        }

        fun cancel() {
            this.cancel = true
        }

        //main thread
        private val startRunnable = {
            if (!cancel && s()) {
                AsyncTask.execute(createRunnable)
            }
        }

        //work thread
        private val createRunnable = {
            if (!cancel) {
                back = try {
                    c()
                } catch (e: Throwable) {
                    Log.e("AsyncRun.Call", "execute", e)
                    error
                }
                if (!cancel) {
                    mMainHandler.post(resultRunnable)
                }
            }
        }

        //main thread
        private val resultRunnable = {
            if (!cancel) {
                r(back)
                if (back.isSuccess) {
                    ok(back)
                } else {
                    fail(back)
                }
            }
        }
    }

    companion object {

        private val single by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AsyncRun() }

        fun single(): AsyncRun = single
    }

    interface Result {
        var isSuccess: Boolean
    }
}
