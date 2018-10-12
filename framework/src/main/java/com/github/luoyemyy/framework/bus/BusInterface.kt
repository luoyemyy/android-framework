package com.github.luoyemyy.framework.bus

import android.support.annotation.MainThread

interface BusIntercept {
    fun interceptCode(): LongArray
    fun interceptGroup(): Int
}

interface BusResult {
    @MainThread
    fun busResult(code: Long, msg: BusMsg)
}