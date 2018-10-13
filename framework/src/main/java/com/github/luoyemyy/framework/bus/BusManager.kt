package com.github.luoyemyy.framework.bus

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.MainThread
import android.util.SparseArray

/**
 *
 */
class BusManager private constructor() {

    private val mCallbacks = SparseArray<MutableList<Callback>>()
    private val mHandler = Handler(Looper.getMainLooper())

    /**
     * 注册回调
     *
     * @param callback
     */
    @MainThread
    fun registerIfNotExist(callback: Callback) {

        val group = callback.interceptGroup()
        mCallbacks.get(group)?.apply {
            if (none { it.callbackId == callback.callbackId }) {
                add(callback)
            }
        } ?: createGroup(group).apply { add(callback) }

    }

    private fun createGroup(group: Int): MutableList<Callback> {
        return mutableListOf<Callback>().apply { mCallbacks.put(group, this) }
    }

    /**
     * 注册回调
     *
     * @param callback
     */
    @MainThread
    fun register(callback: Callback) {
        val group = callback.interceptGroup()
        mCallbacks.get(group)?.apply { add(callback) } ?: createGroup(group).apply { add(callback) }
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

    @MainThread
    fun unRegister(callbacks: MutableList<Callback>) {
        if (callbacks.isEmpty()) return

        callbacks.forEach { callback ->
            val group = callback.interceptGroup()
            val groupCallbacks = mCallbacks.get(group)
            if (groupCallbacks != null) {
                groupCallbacks.remove(callback)
                if (groupCallbacks.size == 0) {
                    mCallbacks.remove(group)
                }
            }
        }
    }

    /**
     * 反注册回调，所有组别相同的都反注册
     *
     * @param group 组id
     */
    @MainThread
    fun unRegister(group: Int) {
        mCallbacks.remove(group)
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


    interface Callback : BusResult {

        val callbackId: String
        fun interceptGroup(): Int
        fun interceptEvent(): Array<out String>
    }

    companion object {

        private val single by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { BusManager() }

        internal const val GROUP_DEFAULT = 0

        internal const val GROUP_AUDIO = 1

        fun single(): BusManager = single
    }
}
