package com.github.luoyemyy.mvp.recycler

import android.os.Bundle

/**
 * 扩展
 */
interface RecyclerPresenterWrapper<T> : LoadCallback<T> {

    fun getAdapterSupport(): RecyclerAdapterSupport<*>?

    fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle? = null, search: String? = null): List<T>?
}