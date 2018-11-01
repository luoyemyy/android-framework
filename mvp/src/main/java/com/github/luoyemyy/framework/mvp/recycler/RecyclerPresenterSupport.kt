package com.github.luoyemyy.framework.mvp.recycler

import android.os.Bundle
import android.support.annotation.MainThread

/**
 * 扩展
 */
interface RecyclerPresenterSupport<T>: PresenterLoadCallback<T>{

    fun getDataSet(): DataSet<T>
    fun getPaging(): Paging
    fun getAdapterSupport(): RecyclerAdapterSupport<*>?

    @MainThread
    fun loadInit(bundle: Bundle? = null)

    @MainThread
    fun loadRefresh()

    @MainThread
    fun loadMore()

    @MainThread
    fun loadSearch(search: String)

    fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle? = null, search: String? = null): List<T>?

}