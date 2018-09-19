package com.github.luoyemyy.framework.test.mvp

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.github.luoyemyy.framework.mvp.AbstractPresenter
import com.github.luoyemyy.framework.test.helper.getApi
import com.github.luoyemyy.framework.test.helper.success

class MvpPresenter(var app: Application) : AbstractPresenter(app) {

    val liveData = MutableLiveData<String>()

    override fun load(bundle: Bundle?) {
        getApi().login()
                .success {
                    liveData.value = it.data
                }.failure {
                    liveData.value = it.data
                }
    }
}