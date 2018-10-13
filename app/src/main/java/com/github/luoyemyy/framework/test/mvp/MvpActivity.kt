package com.github.luoyemyy.framework.test.mvp

import android.databinding.DataBindingUtil.setContentView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.luoyemyy.framework.audio.AudioCallback
import com.github.luoyemyy.framework.audio.AudioManager
import com.github.luoyemyy.framework.bus.BusManager
import com.github.luoyemyy.framework.bus.BusMsg
import com.github.luoyemyy.framework.bus.BusResult
import com.github.luoyemyy.framework.ext.getPresenter
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityMvpBinding

class MvpActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMvpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setContentView(this, R.layout.activity_mvp)

        val presenter = getPresenter<MvpPresenter>()

        mBinding.setLifecycleOwner(this)
        mBinding.presenter = presenter

        mBinding.presenter?.load()

        BusManager.setCallback(lifecycle, object : BusResult {
            override fun busResult(event: String, msg: BusMsg) {

            }
        }, this::class.java.name)
        BusManager.post(this::class.java.name, 0)

        AudioManager.setCallback(lifecycle, "1", object : AudioCallback {
            override fun audioPlay(id: String) {

            }

            override fun audioStop(id: String) {

            }

            override fun audioPlaying(id: String, leftSecond: Int) {

            }

        })
        AudioManager.play(this, "1", "")
    }


}