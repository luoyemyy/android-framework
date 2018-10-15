@file:Suppress("unused")

package com.github.luoyemyy.framework.ext

import android.os.Handler
import android.os.Looper
import com.github.luoyemyy.framework.async.AsyncRun

/**
 * thread run
 */
fun runOnWorker(run: () -> Unit) = AsyncRun.newCall<Any>().create { run() }

fun runOnMain(run: () -> Unit) = Handler(Looper.getMainLooper()).post(run)
