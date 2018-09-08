package com.github.luoyemyy.framework.test

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import com.github.luoyemyy.framework.mvp.action.IGeneralPresenter
import com.github.luoyemyy.framework.test.databinding.ActivityMainBinding
import com.github.luoyemyy.framework.utils.getPresenter

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mPresenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.setLifecycleOwner(this)

        mPresenter = getPresenter(this)
        mBinding.presenter = mPresenter

        mPresenter.load()
    }

    class Presenter(app: Application) : AndroidViewModel(app), IGeneralPresenter {

        val user = MutableLiveData<User>()

        override fun load(bundle: Bundle?) {
            user.value = User("username", "password")
        }

        fun check(v:View,checked:Boolean,u: User) {
            user.value = User("username1", "password1")
        }

    }
}


