package com.github.luoyemyy.mvp.recycler

import android.support.v7.widget.RecyclerView

interface RecyclerAdapterSupport<T> : LoadCallback<T> {

    fun setup(presenterSupport: RecyclerPresenterSupport<T>)

    fun getAdapter(): RecyclerView.Adapter<*>

    fun attachToRecyclerView()

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
     * 加载完全部数据后，是否隐藏该项目
     */
    fun loadMoreEndGone(): Boolean {
        return false
    }

    /**
     * 是否需要空数据样式
     */
    fun enableEmpty(): Boolean {
        return true
    }

}