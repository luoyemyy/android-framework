package com.github.luoyemyy.framework.audio

interface AudioCallback {

    fun audioPlay(id: String)

    fun audioStop(id: String)

    fun audioPlaying(id: String, leftSecond: Int)
}
