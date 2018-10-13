package com.github.luoyemyy.framework.audio

import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.github.luoyemyy.framework.bus.BusManager
import com.github.luoyemyy.framework.bus.BusMsg

internal class AudioRegistry(lifecycle: Lifecycle, private val mGroup: Int, audioId: String, private val mCallback: AudioCallback) : AudioManager.Callback, GenericLifecycleObserver {

    constructor(lifecycle: Lifecycle, audioId: String, mCallback: AudioCallback) : this(lifecycle, BusManager.GROUP_AUDIO, audioId, mCallback)

    private val mEvents: Array<String> = arrayOf(audioId)

    init {
        lifecycle.addObserver(this)
        BusManager.register(this)
    }

    override fun onStateChanged(source: LifecycleOwner?, event: Lifecycle.Event?) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            BusManager.unRegister(this)
            source?.lifecycle?.removeObserver(this)
        }
    }

    override val callbackId: String = "${interceptGroup()}@$audioId"

    override fun interceptGroup(): Int = mGroup

    override fun interceptEvent(): Array<out String> = mEvents

    override fun busResult(event: String, msg: BusMsg) {
        when (msg.intValue) {
            AudioMsg.PLAY -> audioPlay(msg.event)
            AudioMsg.STOP -> audioStop(msg.event)
            AudioMsg.LEFT_SECOND -> audioPlaying(msg.event, msg.longValue.toInt())
        }
    }

    override fun audioPlay(id: String) {
        mCallback.audioPlay(id)
    }

    override fun audioStop(id: String) {
        mCallback.audioStop(id)
    }

    override fun audioPlaying(id: String, leftSecond: Int) {
        mCallback.audioPlaying(id, leftSecond)
    }
}
