package com.github.luoyemyy.framework.permission

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

class PermissionPresenter(app: Application) : AndroidViewModel(app) {

    private var data: MutableLiveData<Array<String>>? = null

    fun addObserver(owner: LifecycleOwner, observer: Observer<Array<String>>) {
        data = MutableLiveData<Array<String>>().apply {
            observe(owner, observer)
        }
    }

    fun removeObserver(observer: Observer<Array<String>>) {
        data?.removeObserver(observer)
        data = null
    }

    fun postValue(array: Array<String>) {
        data?.postValue(array)
    }
}