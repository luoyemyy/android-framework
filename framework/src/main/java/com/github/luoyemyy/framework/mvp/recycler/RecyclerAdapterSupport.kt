package com.github.luoyemyy.framework.mvp.recycler

import android.support.v7.widget.RecyclerView

interface RecyclerAdapterSupport<T> : LoadCallback<T> {

    fun setup(presenterSupport: RecyclerPresenterSupport<T>)

    fun getAdapter(): RecyclerView.Adapter<*>

    fun attachToRecyclerView()

    /**
     * 获得指定位置的数据，如果是加载更多或空数据项，则为null
     */
    fun getItem(position: Int): T?

    /**
     * 设置刷新控件样式
     */
    fun setRefreshState(refreshing: Boolean) {

    }

    /**
     * 是否需要加载更多样式
     */
    fun enableLoadMore(): Boolean {
        return true
    }

    /**
     * 是否需要空数据样式
     */
    fun enableEmpty(): Boolean {
        return true
    }
}