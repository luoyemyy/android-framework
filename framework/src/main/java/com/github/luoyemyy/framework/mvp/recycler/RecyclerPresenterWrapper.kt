package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner

interface RecyclerPresenterWrapper<T> {

    fun setup(owner: LifecycleOwner, adapter: RecyclerAdapterBridge<T>)
    fun isLoadInit(loadType: Int) = loadType == 1
    fun isLoadRefresh(loadType: Int) = loadType == 2
    fun isLoadMore(loadType: Int) = loadType == 3
    fun isLoadSearch(loadType: Int) = loadType == 4

}