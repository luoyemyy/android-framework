package com.github.luoyemyy.framework.mvp

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.github.luoyemyy.framework.helper.newSimpleCall

class MvpPresenter(var app: Application) : AbstractPresenter(app) {

    val liveData = MutableLiveData<String>()
    private val mModel by lazy { MvpModel(app) }

    override fun load(bundle: Bundle?) {

        newSimpleCall()
                .start { true }
                .create {
                    mModel.login()
                }
                .success {
                    liveData.value = "success"
                }
                .failure {
                    liveData.value = "failure"
                }
    }
}