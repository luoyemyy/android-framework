package com.github.luoyemyy.framework.audio

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import android.net.Uri

import com.github.luoyemyy.framework.bus.BusManager
import com.github.luoyemyy.framework.bus.BusMsg

/**
 * example:
 *
 */

object AudioManager {

    interface Callback : BusManager.Callback, AudioCallback

    fun play(context: Context, id: String, path: String, repeat: Int = 0) {
        val msg = AudioMsg(op = AudioMsg.PLAY, id = id, sourceType = AudioMsg.PATH, path = path, repeat = repeat)
        op(context, msg)
    }

    fun play(context: Context, id: String, rawId: Int, repeat: Int = 0) {
        val msg = AudioMsg(op = AudioMsg.PLAY, id = id, sourceType = AudioMsg.RAW, rawId = rawId, repeat = repeat)
        op(context, msg)
    }

    fun play(context: Context, id: String, uri: Uri, repeat: Int = 0) {
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

    internal fun bus(group: Int = BusManager.GROUP_AUDIO, op: Int, id: String?, leftSecond: Int = 0) {
        if (id != null) {
            BusManager.post(group, id, op, leftSecond.toLong(), false, null, null)
        }
    }

    /**
     * callback会绑定到lifecycle上，自动销毁
     */
    fun setCallback(lifecycle: Lifecycle, audioId: String, callback: AudioCallback) {
        AudioRegistry(lifecycle, audioId, callback)
    }

    /**
     * callback会绑定到lifecycle上，自动销毁
     */
    fun setCallback(lifecycle: Lifecycle, group: Int, audioId: String, callback: AudioCallback) {
        AudioRegistry(lifecycle, group, audioId, callback)
    }

}
