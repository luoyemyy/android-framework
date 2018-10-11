package com.github.luoyemyy.framework.test.mvp

import android.databinding.DataBindingUtil.setContentView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.luoyemyy.framework.ext.getPresenter
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityMvpBinding

class MvpActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMvpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setContentView(this, R.layout.activity_mvp)
        mBinding.setLifecycleOwner(this)
        mBinding.presenter = getPresenter()

        mBinding.presenter?.load()
    }

}