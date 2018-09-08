package com.github.luoyemyy.framework.mvp

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.support.annotation.CallSuper

abstract class BasePresenter(app: Application) : AndroidViewModel(app), GenericLifecycleObserver {

    @CallSuper
    open fun attach(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    @CallSuper
    open fun detach(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
    }

    open fun onCreate(owner: LifecycleOwner) {}
    open fun onStart(owner: LifecycleOwner) {}
    open fun onResume(owner: LifecycleOwner) {}
    open fun onPause(owner: LifecycleOwner) {}
    open fun onStop(owner: LifecycleOwner) {}
    open fun onDestroy(owner: LifecycleOwner) {}

    private fun onDestroyEvent(owner: LifecycleOwner) {
        detach(owner)
        onDestroy(owner)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> onCreate(source)
            Lifecycle.Event.ON_START -> onStart(source)
            Lifecycle.Event.ON_RESUME -> onResume(source)
            Lifecycle.Event.ON_PAUSE -> onPause(source)
            Lifecycle.Event.ON_STOP -> onStop(source)
            Lifecycle.Event.ON_DESTROY -> onDestroyEvent(source)
            Lifecycle.Event.ON_ANY -> {
            }
        }
    }
}