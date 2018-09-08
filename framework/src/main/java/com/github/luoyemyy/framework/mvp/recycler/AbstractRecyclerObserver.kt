package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.Observer

abstract class AbstractRecyclerObserver<T> : Observer<RecyclerData<T>> {

    override fun onChanged(value: RecyclerData<T>?) {
        when {
            value == null -> {
                //nothing
            }
            value.isInit -> onInitChanged(value.data)
            value.isMore -> onMoreChanged(value.data)
            value.isRefresh -> onRefreshChanged(value.data)
            value.isSearch -> onSearchChanged(value.data)
        }
    }

    abstract fun onInitChanged(list: List<T>?)
    open fun onMoreChanged(list: List<T>?) {}
    open fun onRefreshChanged(list: List<T>?) {}
    open fun onSearchChanged(list: List<T>?) {}
}