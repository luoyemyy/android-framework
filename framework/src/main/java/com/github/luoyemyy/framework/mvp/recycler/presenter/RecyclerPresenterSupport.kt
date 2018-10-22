package com.github.luoyemyy.framework.mvp.recycler.presenter

import android.os.Bundle
import com.github.luoyemyy.framework.mvp.recycler.DataSet
import com.github.luoyemyy.framework.mvp.recycler.LoadCallback

interface RecyclerPresenterSupport<T> : LoadCallback<T> {

    fun getDataSet(): DataSet<T>
    fun loadInit(bundle: Bundle? = null)
    fun loadRefresh()
    fun loadMore()
    fun loadSearch(search: String)

}