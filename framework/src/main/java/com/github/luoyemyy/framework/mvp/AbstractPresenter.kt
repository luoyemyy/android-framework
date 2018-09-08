package com.github.luoyemyy.framework.mvp

import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.databinding.PropertyChangeRegistry
import com.github.luoyemyy.framework.mvp.action.IGeneralPresenter
import com.github.luoyemyy.framework.mvp.action.IGeneralValue

abstract class AbstractPresenter<T>(app: Application) : BasePresenter(app), IGeneralValue<T>, IGeneralPresenter {

    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    val mLiveData = MutableLiveData<T>()

    fun attach(owner: LifecycleOwner, observer: Observer<T>) {
        super.attach(owner)
        mLiveData.observe(owner, observer)
    }

    override fun setValue(value: T?) {
        mLiveData.value = value
    }
}