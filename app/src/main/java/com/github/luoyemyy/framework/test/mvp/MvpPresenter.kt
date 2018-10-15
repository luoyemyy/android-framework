package com.github.luoyemyy.framework.test.mvp

import android.app.Application
import android.os.Bundle
import com.github.luoyemyy.framework.bus.BusManager
import com.github.luoyemyy.framework.bus.BusMsg
import com.github.luoyemyy.framework.bus.BusResult
import com.github.luoyemyy.framework.ext.runOnWorker
import com.github.luoyemyy.framework.mvp.AbstractPresenter

class MvpPresenter(var app: Application) : AbstractPresenter<String>(app) {

    override fun load(bundle: Bundle?) {
        runOnWorker {
            BusManager.post(MvpActivity.EVENT_BUS)
        }
    }
}