package com.github.luoyemyy.framework.mvp

import android.app.Application
import com.github.luoyemyy.framework.async.SimpleBack
import com.github.luoyemyy.framework.helper.getApi

class MvpModel(app: Application) : BaseModel(app) {

    fun login(): SimpleBack {
        getApi().login()
        return SimpleBack()
    }
}