package com.github.luoyemyy.audio

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

class AudioService : MediaBrowserServiceCompat() {

    private var mMediaSession: MediaSessionCompat? = null
    private lateinit var mPlaybackState: PlaybackStateCompat.Builder

    inner class Callback : MediaSessionCompat.Callback() {
        override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {

        }

        override fun onPlay() {
        }

        override fun onPause() {
        }

        override fun onStop() {
        }

        override fun onSeekTo(pos: Long) {

        }
    }

    override fun onCreate() {
        super.onCreate()
        mMediaSession = MediaSessionCompat(baseContext, "AudioService").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            mPlaybackState = PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)
            setPlaybackState(mPlaybackState.build())

            setCallback(Callback())

            val controller = MediaControllerCompat(this@AudioService, this)
            controller.registerCallback(object : MediaControllerCompat.Callback() {

            })
        }
    }


    override fun onLoadChildren(parentMediaId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(AudioManager.getMedias(parentMediaId))
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot("play", null)
    }
}