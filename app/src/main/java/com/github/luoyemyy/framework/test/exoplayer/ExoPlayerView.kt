package com.github.luoyemyy.framework.test.exoplayer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.github.luoyemyy.framework.test.R
import com.google.android.exoplayer2.ui.PlayerView

class ExoPlayerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0, 0)
    constructor(context: Context) : this(context, null, 0, 0)

    var mPlayerView: PlayerView

    init {
        LayoutInflater.from(context).inflate(R.layout.exo_player_view, this)
        mPlayerView = findViewById(R.id.playerView)
    }


}