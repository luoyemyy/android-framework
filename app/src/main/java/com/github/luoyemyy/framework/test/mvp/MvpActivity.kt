package com.github.luoyemyy.framework.test.mvp

import android.databinding.DataBindingUtil.setContentView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.luoyemyy.bus.BusManager
import com.github.luoyemyy.bus.BusMsg
import com.github.luoyemyy.bus.BusResult
import com.github.luoyemyy.config.ext.getPresenter
import com.github.luoyemyy.config.ext.toast
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityMvpBinding

class MvpActivity : AppCompatActivity(), BusResult {

    private lateinit var mBinding: ActivityMvpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setContentView(this, R.layout.activity_mvp)

        val presenter = getPresenter<MvpPresenter>()

        mBinding.setLifecycleOwner(this)
        mBinding.presenter = presenter

        mBinding.presenter?.load()

        BusManager.setCallback(lifecycle, this, EVENT_BUS)

    }

    override fun busResult(event: String, msg: BusMsg) {
        toast(message = event)
    }

    companion object {
        const val EVENT_BUS = "com.github.luoyemyy.framework.test.mvp.MvpActivity"
    }
}