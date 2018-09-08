package com.github.luoyemyy.framework.async

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Worker {

    private val mExecutorService: ExecutorService = Executors.newCachedThreadPool()

    fun execute(runnable: Runnable) {
        mExecutorService.execute(runnable)
    }

    fun execute(runnable: () -> Unit) {
        mExecutorService.execute(runnable)
    }
}