package com.github.luoyemyy.framework.mvp.recycler

import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

interface RecyclerAdapterOp<T, BIND : ViewDataBinding> {

    fun getItem(position: Int): T?

    /**
     * 绑定数据
     */
    fun bindContentViewHolder(binding: BIND, content: T, position: Int)

    /**
     * 创建内容view
     */
    fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BIND

    /**
     * 获得内容类型id，如果是多类型需要重写该方法
     */
    fun getContentType(position: Int, item: T?): Int

    /**
     * 设置刷新控件样式
     */
    fun setRefreshState(refreshing: Boolean) {

    }

    /**
     * 如果绑定了item的点击事件，则需要重写该方法，
     */
    fun onItemClickListener(binding: BIND?, view: View?, position: Int) {

    }

    fun getItemClickViews(binding: BIND): Array<View> {
        return arrayOf(binding.root)
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

    fun getMoreLoadingLayout(): Int {
        return 0
    }

    fun getMoreEndLayout(): Int {
        return 0
    }

    fun getEmptyLayout(): Int {
        return 0
    }
}