package com.github.luoyemyy.framework.async

import android.os.Handler
import android.os.Looper
import android.util.Log

class AsyncRun {

    private val mMainHandler: Handler = Handler(Looper.getMainLooper())

    inner class Call<M : AsyncRunResult>(private val error: M) {

        private var back: M = error
        //start
        private var s: () -> Boolean = { true }

        //create
        private lateinit var c: () -> M

        //result
        private var r: (M) -> Unit = {}
        private var ok: (M) -> Unit = {}
        private var fail: (M) -> Unit = {}

        //cancel
        private var cancel: Boolean = false

        fun start(s: () -> Boolean): Call<M> {
            this.s = s
            return this
        }

        fun create(c: () -> M): Call<M> {
            this.c = c
            mMainHandler.post(startRunnable)
            return this
        }

        fun result(r: (M) -> Unit = {}): Call<M> {
            this.r = r
            return this
        }

        fun success(success: (M) -> Unit): Call<M> {
            ok = success
            return this
        }

        fun failure(failure: (M) -> Unit): Call<M> {
            fail = failure
            return this
        }

        fun cancel() {
            this.cancel = true
        }

        private val startRunnable = {
            if (!cancel && s()) {
                Worker.execute(createRunnable)
            }
        }

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

        private val single by lazy { AsyncRun() }

        fun single(): AsyncRun = single
    }
}
