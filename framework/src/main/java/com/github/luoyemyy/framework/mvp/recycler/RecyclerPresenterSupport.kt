package com.github.luoyemyy.framework.mvp.recycler

import android.os.Bundle

/**
 * 对外提供给 Activity、Fragment、Adapter 调用
 */
interface RecyclerPresenterSupport<T> : LoadCallback<T> {

    fun getDataSet(): DataSet<T>
    fun loadInit(bundle: Bundle? = null)
    fun loadRefresh()
    fun loadMore()
    fun loadSearch(search: String)

}