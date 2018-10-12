package com.github.luoyemyy.framework.test.mvp

import android.databinding.DataBindingUtil.setContentView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.luoyemyy.framework.bus.BusRegistry
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

        BusRegistry.init(presenter, lifecycle, 1)

        mBinding.presenter?.load()
    }

}