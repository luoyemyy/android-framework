package com.github.luoyemyy.audio

interface AudioCallback {

    fun audioPlay(id: String)

    fun audioStop(id: String)

    fun audioPlaying(id: String, leftSecond: Int)
}
