package com.github.luoyemyy.framework.mvp.recycler

import android.os.Bundle

interface IRecyclerPresenter<T> {

    fun getDataSet(): DataSet<T>

    fun loadInit(bundle: Bundle? = null)

    fun loadRefresh()

    fun loadMore()

    fun loadSearch(search: String)
}