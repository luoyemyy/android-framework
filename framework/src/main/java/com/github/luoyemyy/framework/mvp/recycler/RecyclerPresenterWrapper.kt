package com.github.luoyemyy.framework.mvp.recycler

/**
 * 子类调用的一些方法
 */
interface RecyclerPresenterWrapper {

    fun getPaging(): Paging
    fun getAdapterSupport(): RecyclerAdapterSupport<*>?

}