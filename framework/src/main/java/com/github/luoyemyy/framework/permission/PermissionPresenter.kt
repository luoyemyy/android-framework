package com.github.luoyemyy.framework.permission

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData

class PermissionPresenter(app: Application) : AndroidViewModel(app) {

    val data = MutableLiveData<Array<String>>()

}