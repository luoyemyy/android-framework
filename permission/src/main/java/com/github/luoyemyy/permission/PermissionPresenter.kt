package com.github.luoyemyy.permission

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

class PermissionPresenter(app: Application) : AndroidViewModel(app) {

    private var data: MutableLiveData<Array<String>> = MutableLiveData()

    fun addObserver(owner: LifecycleOwner, observer: Observer<Array<String>>) {
        if (data.hasObservers()) {
            data.removeObservers(owner)
        }
        data.observe(owner, observer)
    }

    fun removeObserver(observer: Observer<Array<String>>) {
        data.removeObserver(observer)
    }

    fun postValue(array: Array<String>) {
        data.postValue(array)
    }
}