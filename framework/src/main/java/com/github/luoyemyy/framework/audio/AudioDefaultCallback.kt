package com.github.luoyemyy.framework.audio

import com.github.luoyemyy.framework.bus.Bus

class AudioDefaultCallback(private val mCallback: AudioCallback, vararg codes: Long) : AudioManager.Callback {

    private val mCodes: LongArray = codes

    fun register() {
        Bus.single().register(this)
    }

    fun unRegister() {
        Bus.single().unRegister(this)
    }

    override fun interceptGroup(): Int = Bus.GROUP_AUDIO

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
}
