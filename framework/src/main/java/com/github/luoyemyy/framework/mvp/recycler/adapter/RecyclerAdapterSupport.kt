package com.github.luoyemyy.framework.mvp.recycler.adapter

import android.support.v7.widget.RecyclerView
import com.github.luoyemyy.framework.mvp.recycler.LoadCallback
import com.github.luoyemyy.framework.mvp.recycler.presenter.RecyclerPresenterSupport

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
     * 是否需要空数据样式
     */
    fun enableEmpty(): Boolean {
        return true
    }
}