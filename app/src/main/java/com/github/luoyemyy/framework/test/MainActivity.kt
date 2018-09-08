package com.github.luoyemyy.framework.test

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.luoyemyy.framework.mvp.AbstractPresenter
import com.github.luoyemyy.framework.test.databinding.ActivityMainBinding
import com.github.luoyemyy.framework.utils.getPresenter

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mBinding.also {
            it.setLifecycleOwner(this)
            it.presenter = getPresenter(this)
        }

        mBinding.presenter?.load()
    }

    class Presenter(app: Application) : AbstractPresenter(app) {

        val user = MutableLiveData<User>()

        override fun load(bundle: Bundle?) {
            user.value = User("username", "password")
        }
    }

    class User(val username: String, val password: String)
}


