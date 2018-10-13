package com.github.luoyemyy.framework.bus

import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner

/**
 * bus 管理注册器
 * 注册后，此事件监听会绑定生命周期，不用手动去释放
 */
class BusRegistry private constructor(lifecycle: Lifecycle, private val mResult: BusResult, private var mEvents: Array<out String>) : BusManager.Callback, GenericLifecycleObserver {

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

    override val callbackId: String by lazy {
        interceptEvent().sorted().joinToString("&")
    }

    override fun interceptGroup(): Int = BusManager.GROUP_DEFAULT

    override fun interceptEvent(): Array<out String> = mEvents

    override fun busResult(event: String, msg: BusMsg) {
        mResult.busResult(event, msg)
    }

    companion object {
        fun init(lifecycle: Lifecycle, result: BusResult, vararg events: String) = BusRegistry(lifecycle, result, events)
    }
}
