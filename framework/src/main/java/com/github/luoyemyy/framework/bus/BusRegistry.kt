package com.github.luoyemyy.framework.bus

import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner

/**
 * bus 管理注册器
 * 注册后，此事件监听会绑定生命周期，不用手动去释放
 */
class BusRegistry private constructor(private val mResult: BusResult, lifecycle: Lifecycle, codes: LongArray) : BusManager.Callback, GenericLifecycleObserver {

    private val mCodes: LongArray = codes

    init {
        lifecycle.addObserver(this)
        BusManager.single().register(this)
    }

    override fun onStateChanged(source: LifecycleOwner?, event: Lifecycle.Event?) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            BusManager.single().unRegister(this)
            source?.lifecycle?.removeObserver(this)
        }
    }

    override fun interceptGroup(): Int = BusManager.GROUP_DEFAULT

    override fun interceptCode(): LongArray = mCodes

    override fun busResult(code: Long, msg: BusMsg) {
        mResult.busResult(code, msg)
    }

    companion object {
        fun init(result: BusResult, lifecycle: Lifecycle, vararg codes: Long) = BusRegistry(result, lifecycle, codes)
    }
}
