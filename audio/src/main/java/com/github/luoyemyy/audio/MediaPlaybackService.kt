package com.github.luoyemyy.audio

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat

class MediaPlaybackService : MediaBrowserServiceCompat() {

    override fun onLoadChildren(parentMediaId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
//        MediaMetadataCompat.Builder().putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)
//        MediaBrowserCompat.MediaItem
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot("play", null)
    }
}