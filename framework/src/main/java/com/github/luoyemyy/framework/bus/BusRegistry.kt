package com.github.luoyemyy.framework.bus

import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner

class BusRegistry private constructor(private val mResult: BusResult, lifecycle: Lifecycle, codes: LongArray) : Bus.Callback, GenericLifecycleObserver {

    private val mCodes: LongArray = codes

    init {
        lifecycle.addObserver(this)
        Bus.single().register(this)
    }

    override fun onStateChanged(source: LifecycleOwner?, event: Lifecycle.Event?) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            Bus.single().unRegister(this)
            source?.lifecycle?.removeObserver(this)
        }
    }

    override fun interceptGroup(): Int = Bus.GROUP_DEFAULT

    override fun interceptCode(): LongArray = mCodes

    override fun busResult(group: Int, code: Long, msg: BusMsg) {
        mResult.busResult(group, code, msg)
    }

    companion object {
        fun init(result: BusResult, lifecycle: Lifecycle, vararg codes: Long) = BusRegistry(result, lifecycle, codes)
    }
}
