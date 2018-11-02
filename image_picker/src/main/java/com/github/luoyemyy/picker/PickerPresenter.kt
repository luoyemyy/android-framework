package com.github.luoyemyy.picker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer


/**
 * example
 *
 * ViewModelProviders.of(fragmentActivity).get(PickerPresenter::class.java).getImages(owner){images->
 *
 * }
 *
 */
class PickerPresenter(app: Application) : AndroidViewModel(app) {

    private var data: MutableLiveData<List<String>>? = null

    fun addObserver(owner: LifecycleOwner, observer: Observer<List<String>>) {
        data = MutableLiveData<List<String>>().apply {
            observe(owner, observer)
        }
    }

    fun removeObserver(owner: LifecycleOwner) {
        data?.removeObservers(owner)
        data = null
    }

    fun postValue(array: List<String>?) {
        data?.postValue(array)
    }

}