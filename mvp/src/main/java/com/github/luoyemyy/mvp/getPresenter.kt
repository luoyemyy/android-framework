package com.github.luoyemyy.mvp

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.github.luoyemyy.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.RecyclerAdapterSupport

/**
 * presenter
 */
inline fun <reified T : AndroidViewModel> Fragment.getPresenter(): T = ViewModelProviders.of(this).get(T::class.java)

inline fun <reified T : AndroidViewModel> FragmentActivity.getPresenter(): T = ViewModelProviders.of(this).get(T::class.java)

inline fun <R, reified T : AbstractRecyclerPresenter<R>> Fragment.getRecyclerPresenter(owner: LifecycleOwner, adapter: RecyclerAdapterSupport<R>): T {
    return ViewModelProviders.of(this).get(T::class.java).apply {
        setup(owner, adapter)
    }
}

inline fun <R, reified T : AbstractRecyclerPresenter<R>> FragmentActivity.getRecyclerPresenter(owner: LifecycleOwner, adapter: RecyclerAdapterSupport<R>): T {
    return ViewModelProviders.of(this).get(T::class.java).apply {
        setup(owner, adapter)
    }
}
