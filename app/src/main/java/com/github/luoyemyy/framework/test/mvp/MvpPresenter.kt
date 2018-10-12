package com.github.luoyemyy.framework.test.mvp

import android.app.Application
import android.os.Bundle
import com.github.luoyemyy.framework.bus.BusMsg
import com.github.luoyemyy.framework.bus.BusResult
import com.github.luoyemyy.framework.mvp.AbstractPresenter
import com.github.luoyemyy.framework.test.helper.create
import com.github.luoyemyy.framework.test.helper.getApi

class MvpPresenter(var app: Application) : AbstractPresenter<String>(app), BusResult {

    override fun load(bundle: Bundle?) {
        getApi().login()
                .create {
                    it
                }
                .success {
                    data.value = it.data
                }.failure {
                    data.value = it.data
                }
    }

    override fun busResult(code: Long, msg: BusMsg) {

    }
}