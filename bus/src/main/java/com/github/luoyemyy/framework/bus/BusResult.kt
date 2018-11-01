package com.github.luoyemyy.framework.bus

import android.support.annotation.MainThread

interface BusResult {
    @MainThread
    fun busResult(event: String, msg: BusMsg)
}