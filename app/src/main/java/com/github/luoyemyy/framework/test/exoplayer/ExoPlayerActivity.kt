package com.github.luoyemyy.framework.test.exoplayer

import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityExoplayerBinding
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class ExoPlayerActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityExoplayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_exoplayer)

        val player = ExoPlayerFactory.newSimpleInstance(this)
        mBinding.playView.mPlayerView.player = player
        player.prepare(DefaultDataSourceFactory(this,Util.getUserAgent(this,packageName)).let {
            ExtractorMediaSource.Factory(it).createMediaSource(Uri.parse("http://jzvd.nathen.cn/c494b340ff704015bb6682ffde3cd302/64929c369124497593205a4190d7d128-5287d2089db37e62345123a1be272f8b.mp4"))
        })

    }
}