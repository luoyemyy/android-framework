package com.github.luoyemyy.bus

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.MainThread
import android.util.SparseArray

/**
 *
 */
object BusManager {

    private val mCallbacks = SparseArray<MutableList<Callback>>()
    private val mHandler = Handler(Looper.getMainLooper())

    internal const val GROUP_DEFAULT = 0

    const val GROUP_AUDIO = 1

    interface Callback : BusResult {

        val callbackId: String

        fun interceptGroup(): Int

        fun interceptEvent(): Array<out String>
    }

    private fun getCallbacks(group: Int): MutableList<Callback> {
        return mCallbacks.get(group) ?: mutableListOf<Callback>().apply {
            mCallbacks.put(group, this)
        }
    }

    /**
     * 注册回调
     *
     * @param callback
     */
    @MainThread
    fun register(callback: Callback) {
        val group = callback.interceptGroup()
        getCallbacks(group).add(callback)
    }

    @MainThread
    fun replaceRegister(callback: Callback) {
        val group = callback.interceptGroup()
        mCallbacks.get(group)?.removeAll { it.callbackId == callback.callbackId }
        getCallbacks(group).add(callback)
    }

    /**
     * 反注册回调
     *
     * @param callback
     */
    @MainThread
    fun unRegister(callback: Callback) {

        val group = callback.interceptGroup()
        val callbacks = mCallbacks.get(group)
        if (callbacks != null) {
            callbacks.remove(callback)
            if (callbacks.size == 0) {
                mCallbacks.remove(group)
            }
        }

    }

    fun post(event: String, intValue: Int = 0, longValue: Long = 0L, boolValue: Boolean = false, stringValue: String? = null, extra: Bundle? = null) {
        post(GROUP_DEFAULT, event, intValue, longValue, boolValue, stringValue, extra)
    }

    /**
     * 派发消息
     */
    fun post(group: Int, event: String, intValue: Int = 0, longValue: Long = 0L, boolValue: Boolean = false, stringValue: String? = null, extra: Bundle? = null) {
        /* 开始派发消息 */
        mHandler.post {
            val msg = BusMsg(group, event, intValue, longValue, boolValue, stringValue, extra)
            val callbacks = mCallbacks.get(msg.group)
            if (callbacks != null && callbacks.size > 0) {
                callbacks.asSequence()
                        .filter {
                            it.interceptGroup() == msg.group && it.interceptEvent().contains(msg.event)
                        }
                        .forEach {
                            it.busResult(msg.event, msg)
                        }
            }
        }
    }

    fun setCallback(lifecycle: Lifecycle, result: BusResult, vararg events: String) {
        BusRegistry(false, lifecycle, result, events).register()
    }

    fun replaceCallback(lifecycle: Lifecycle, result: BusResult, vararg events: String) {
        BusRegistry(true, lifecycle, result, events).register()
    }

    fun releaseEvents(vararg events: String) {
        mHandler.post {
            mCallbacks[GROUP_DEFAULT].removeAll { callback ->
                events.map { callback.interceptEvent().contains(it) }.firstOrNull { it } ?: false
            }
        }
    }

}
