package com.github.luoyemyy.framework.async

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log

class AsyncRun {

    companion object {

        @JvmStatic
        fun <T> newCall(): Call<T> {
            return Delegate<T, Call<T>>().let {
                Call(it).apply {
                    it.call = this
                }
            }
        }

        @JvmStatic
        fun <R : Result> newResultCall(): ResultCall<R> {
            return Delegate<R, ResultCall<R>>().let {
                ResultCall(it).apply {
                    it.call = this
                }
            }
        }
    }

    interface BaseCall<T, CALL> {
        fun start(s: (CALL) -> Unit): CALL
        fun create(c: (CALL) -> T?): CALL
        fun result(r: (T?, Throwable?) -> Unit): CALL
        fun cancel()
    }

    interface BaseResultCall<T, CALL> : BaseCall<T, CALL> {
        fun success(success: (T?) -> Unit): CALL
        fun failure(failure: (T?, Throwable?) -> Unit): CALL
    }

    class Call<T> internal constructor(delegate: BaseCall<T, Call<T>>) : BaseCall<T, Call<T>> by delegate

    class ResultCall<R : Result> internal constructor(delegate: BaseResultCall<R, ResultCall<R>>) : BaseResultCall<R, ResultCall<R>> by delegate

    private class Delegate<T, CALL : BaseCall<T, CALL>> : BaseResultCall<T, CALL> {

        internal lateinit var call: CALL

        private val mMainHandler: Handler = Handler(Looper.getMainLooper())
        //back
        private var back: T? = null
        //back exception
        private var throwable: Throwable? = null

        //start
        private var s: (CALL) -> Unit = { }

        //create
        private lateinit var c: (CALL) -> T?

        //result
        private var r: (T?, Throwable?) -> Unit = { _, _ -> }
        private var ok: (T?) -> Unit = {}
        private var fail: (T?, Throwable?) -> Unit = { _, _ -> }

        //cancel
        private var cancel: Boolean = false

        override fun start(s: (CALL) -> Unit): CALL {
            this.s = s
            return call
        }

        override fun create(c: (CALL) -> T?): CALL {
            this.c = c
            mMainHandler.post(startRunnable)
            return call
        }

        override fun result(r: (T?, Throwable?) -> Unit): CALL {
            this.r = r
            return call
        }

        override fun success(success: (T?) -> Unit): CALL {
            ok = success
            return call
        }

        override fun failure(failure: (T?, Throwable?) -> Unit): CALL {
            fail = failure
            return call
        }

        override fun cancel() {
            this.cancel = true
        }

        //main thread
        private val startRunnable = {
            if (!cancel) {
                s(call)
                AsyncTask.execute(createRunnable)
            }
        }

        //work thread
        private val createRunnable = {
            if (!cancel) {
                back = try {
                    c(call)
                } catch (e: Throwable) {
                    Log.e("AsyncRun.ResultCall", "execute", e)
                    throwable = e
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
                r(back, throwable)
                val result = back
                if (result == null) {
                    fail(result, throwable)
                } else if (result is Result) {
                    if (result.isSuccess) {
                        ok(result)
                    } else {
                        fail(result, throwable)
                    }
                }
            }
        }

    }

    interface Result {
        var isSuccess: Boolean
    }
}
