package com.github.luoyemyy.framework.bus

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.MainThread
import android.util.SparseArray
import com.github.luoyemyy.framework.ext.compare

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
        var callbacks: MutableList<Callback>? = mCallbacks.get(group)
        if (callbacks == null) {
            callbacks = mutableListOf(callback)
            mCallbacks.put(group, callbacks)
        } else {
            if (!callbacks.any { it.interceptCode().compare(callback.interceptCode()) }) {
                callbacks.add(callback)
            }
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
        var callbacks: MutableList<Callback>? = mCallbacks.get(group)
        if (callbacks == null) {
            callbacks = mutableListOf()
            mCallbacks.put(group, callbacks)
        }
        callbacks.add(callback)

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

    fun post(code: Long, anInt: Int = 0, aLong: Long = 0L, aBoolean: Boolean = false, string: String? = null, bundle: Bundle? = null) {
        post(GROUP_DEFAULT, code, anInt, aLong, aBoolean, string, bundle)
    }

    /**
     * 派发消息
     */
    fun post(group: Int, code: Long, anInt: Int = 0, aLong: Long = 0L, aBoolean: Boolean = false, string: String? = null, bundle: Bundle? = null) {
        /* 开始派发消息 */
        mHandler.post {
            val msg = BusMsg(group, code, anInt, aLong, aBoolean, string, bundle)
            val callbacks = mCallbacks.get(msg.group)
            if (callbacks != null && callbacks.size > 0) {
                callbacks.asSequence()
                        .filter {
                            it.interceptGroup() == msg.group && it.interceptCode().contains(msg.code)
                        }
                        .forEach {
                            it.busResult(msg.code, msg)
                        }
            }
        }
    }


    interface Callback : BusIntercept, BusResult

    companion object {

        private val single by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { BusManager() }

        internal const val GROUP_DEFAULT = 0

        internal const val GROUP_AUDIO = 1

        fun single(): BusManager = single
    }
}
