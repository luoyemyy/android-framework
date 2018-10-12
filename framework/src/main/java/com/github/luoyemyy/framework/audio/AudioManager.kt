package com.github.luoyemyy.framework.audio

import android.content.Context
import android.content.Intent
import android.net.Uri

import com.github.luoyemyy.framework.bus.BusManager
import com.github.luoyemyy.framework.bus.BusMsg

class AudioManager private constructor() {

    interface Callback : BusManager.Callback, AudioCallback {

        override fun interceptGroup(): Int = BusManager.GROUP_AUDIO

        override fun busResult(code: Long, msg: BusMsg) {
            when (msg.mInt) {
                AudioMsg.PLAY -> audioPlay(msg.code)
                AudioMsg.STOP -> audioStop(msg.code)
                AudioMsg.LEFT_SECOND -> audioPlaying(msg.code, msg.mLong.toInt())
            }
        }
    }

    companion object {

        fun register(callback: Callback) {
            BusManager.single().register(callback)
        }

        fun registerIfNotExist(callback: Callback) {
            BusManager.single().registerIfNotExist(callback)
        }

        fun unRegister(callback: Callback) {
            BusManager.single().unRegister(callback)
        }

        fun unRegister(group: Int) {
            BusManager.single().unRegister(group)
        }

        fun play(context: Context, id: Long, path: String) {
            val msg = AudioMsg(op = AudioMsg.PLAY, id = id, sourceType = AudioMsg.PATH, path = path)
            op(context, msg)
        }

        fun play(context: Context, id: Long, path: String, repeat: Int) {
            val msg = AudioMsg(op = AudioMsg.PLAY, id = id, sourceType = AudioMsg.PATH, path = path, repeat = repeat)
            op(context, msg)
        }

        fun play(context: Context, id: Long, rawId: Int) {
            val msg = AudioMsg(op = AudioMsg.PLAY, id = id, sourceType = AudioMsg.RAW, rawId = rawId)
            op(context, msg)
        }

        fun play(context: Context, id: Long, rawId: Int, repeat: Int) {
            val msg = AudioMsg(op = AudioMsg.PLAY, id = id, sourceType = AudioMsg.RAW, rawId = rawId, repeat = repeat)
            op(context, msg)
        }

        fun play(context: Context, id: Long, uri: Uri) {
            val msg = AudioMsg(op = AudioMsg.PLAY, id = id, sourceType = AudioMsg.URI, uri = uri)
            op(context, msg)
        }

        fun play(context: Context, id: Long, uri: Uri, repeat: Int) {
            val msg = AudioMsg(op = AudioMsg.PLAY, id = id, sourceType = AudioMsg.URI, uri = uri, repeat = repeat)
            op(context, msg)
        }

        fun stop(context: Context) {
            val msg = AudioMsg(op = AudioMsg.STOP)
            op(context, msg)
        }

        fun destroy(context: Context) {
            val msg = AudioMsg(op = AudioMsg.DESTROY)
            op(context, msg)
        }

        private fun op(context: Context, msg: AudioMsg) {
            val intent = Intent(context, AudioService::class.java)
            intent.putExtra(AudioMsg.TAG, msg)
            context.startService(intent)
        }

        fun destroyAndUnRegister(context: Context, callback: Callback) {
            destroy(context)
            unRegister(callback)
        }

        internal fun bus(group: Int = BusManager.GROUP_AUDIO, op: Int, id: Long, leftSecond: Int = 0) {
            BusManager.single().post(group, id, op, leftSecond.toLong(), false, null, null)
        }

    }
}
