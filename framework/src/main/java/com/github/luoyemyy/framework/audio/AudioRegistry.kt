package com.github.luoyemyy.framework.audio

import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.github.luoyemyy.framework.bus.BusManager

class AudioRegistry(private val mCallback: AudioCallback, lifecycle: Lifecycle, codes: LongArray) : AudioManager.Callback, GenericLifecycleObserver {

    private val mCodes: LongArray = codes

    init {
        BusManager.single().register(this)
        lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner?, event: Lifecycle.Event?) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            BusManager.single().unRegister(this)
            source?.lifecycle?.removeObserver(this)
        }
    }

    override fun interceptCode(): LongArray = mCodes

    override fun audioPlay(id: Long) {
        mCallback.audioPlay(id)
    }

    override fun audioStop(id: Long) {
        mCallback.audioStop(id)
    }

    override fun audioPlaying(id: Long, leftSecond: Int) {
        mCallback.audioPlaying(id, leftSecond)
    }

    companion object {
        fun init(callback: AudioCallback, lifecycle: Lifecycle, vararg codes: Long) = AudioRegistry(callback, lifecycle, codes)
    }
}
