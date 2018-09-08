package com.github.luoyemyy.framework.audio

interface AudioCallback {

    fun audioPlay(id: Long)

    fun audioStop(id: Long)

    fun audioPlaying(id: Long, leftSecond: Int)
}
