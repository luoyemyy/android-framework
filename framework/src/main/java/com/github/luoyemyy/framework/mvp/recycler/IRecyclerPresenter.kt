package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle

interface IRecyclerPresenter<T> : LoadCallback<T> {

    fun getDataSet(): DataSet<T>
    fun setup(owner: LifecycleOwner, adapterBridge: RecyclerAdapterBridge<T>)

    fun loadInit(bundle: Bundle? = null)
    fun loadRefresh()
    fun loadMore()
    fun loadSearch(search: String)

    fun isLoadInit(loadType: Int) = loadType == 1
    fun isLoadRefresh(loadType: Int) = loadType == 2
    fun isLoadMore(loadType: Int) = loadType == 3
    fun isLoadSearch(loadType: Int) = loadType == 4
}