package com.github.luoyemyy.framework.mvp.recycler.presenter

import com.github.luoyemyy.framework.mvp.recycler.Paging
import com.github.luoyemyy.framework.mvp.recycler.adapter.RecyclerAdapterSupport

/**
 * 子类调用的一些方法
 */
interface RecyclerPresenterWrapper {

    fun getPaging(): Paging
    fun getAdapterSupport(): RecyclerAdapterSupport<*>?
    fun isLoadInit(loadType: Int) = loadType == 1
    fun isLoadRefresh(loadType: Int) = loadType == 2
    fun isLoadMore(loadType: Int) = loadType == 3
    fun isLoadSearch(loadType: Int) = loadType == 4

}