package com.github.luoyemyy.framework.async

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log

class AsyncRun {

    class Call<R : Result> {

        private val mMainHandler: Handler = Handler(Looper.getMainLooper())

        //back
        private var back: R? = null

        //start
        private var s: (Call<R>) -> Unit = { }

        //create
        private lateinit var c: (Call<R>) -> R

        //result
        private var r: (R?) -> Unit = {}
        private var ok: (R?) -> Unit = {}
        private var fail: (R?) -> Unit = {}

        //cancel
        private var cancel: Boolean = false

        fun start(s: (Call<R>) -> Unit): Call<R> {
            this.s = s
            return this
        }

        fun create(c: (Call<R>) -> R): Call<R> {
            this.c = c
            mMainHandler.post(startRunnable)
            return this
        }

        fun result(r: (R?) -> Unit = {}): Call<R> {
            this.r = r
            return this
        }

        fun success(success: (R?) -> Unit): Call<R> {
            ok = success
            return this
        }

        fun failure(failure: (R?) -> Unit): Call<R> {
            fail = failure
            return this
        }

        fun cancel() {
            this.cancel = true
        }

        //main thread
        private val startRunnable = {
            if (!cancel) {
                s(this)
                AsyncTask.execute(createRunnable)
            }
        }

        //work thread
        private val createRunnable = {
            if (!cancel) {
                back = try {
                    c(this)
                } catch (e: Throwable) {
                    Log.e("AsyncRun.Call", "execute", e)
                    null
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
                val result = back
                if (result != null && result.isSuccess) {
                    ok(result)
                } else {
                    fail(result)
                }
            }
        }
    }

    interface Result {
        var isSuccess: Boolean
    }
}
